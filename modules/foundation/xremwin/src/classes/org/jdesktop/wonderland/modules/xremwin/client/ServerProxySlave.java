/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State$
 */
package org.jdesktop.wonderland.modules.xremwin.client;

import com.jme.math.Vector3f;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CreateWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DestroyWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ConfigureWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.PositionWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.RestackWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetDecoratedMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetUserDisplMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetRotateYMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetBorderWidthMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayPixelsMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CopyAreaMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ControllerStatusMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ServerMessageType;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SetWindowTitleMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.UserNameMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SetPopupParentMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SlaveCloseWindowMsgArgs;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.utils.clientsocket.ClientSocketListener;
import org.jdesktop.wonderland.modules.appbase.client.utils.clientsocket.SlaveClientSocket;

// TODO: 0.4 protocol
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MoveCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowCursorMsgArgs;

/**
 * An implementation of a ServerProxy which lives in the slave clients
 * and receives messages from an Xremwin master.
 *
 * @author deronj
 */
@ExperimentalAPI
class ServerProxySlave implements ServerProxy {

    interface DisconnectListener {
        public void disconnected();
    }

    private static final boolean debugIO = true;
    private static final int BUTTON4_MASK = 0x08;
    private static final int BUTTON5_MASK = 0x10;
    /** This app's Wonderland session. */
    protected WonderlandSession session;
    private SlaveClientSocket slaveSocket;
    private int clientId;
    private ClientXrwSlave client;
    private AppXrwConnectionInfo connectionInfo;
    private DisconnectListener disconnectListener;
    private int scanLineWidth;
    private byte[] scanLineBuf;

    // List of byte arrays which have come from the remote window server
    private DataBufferQueue bufQueue = new DataBufferQueue();
    /** Which user is currently controlling the app */
    private String controllingUserName = null;
    private byte[] keyEventBuf = new byte[Proto.KEY_EVENT_MESSAGE_SIZE];
    private byte[] pointerEventBuf = new byte[Proto.POINTER_EVENT_MESSAGE_SIZE];
    private byte[] takeControlBuf = new byte[Proto.TAKE_CONTROL_MESSAGE_SIZE];
    private byte[] releaseControlBuf = new byte[Proto.RELEASE_CONTROL_MESSAGE_SIZE];
    private byte[] setWindowTitleBuf = new byte[Proto.SET_WINDOW_TITLE_MESSAGE_SIZE];
    private byte[] setUserDisplBuf = new byte[Proto.WINDOW_SET_USER_DISPL_MESSAGE_SIZE];
    private byte[] setSizeBuf = new byte[Proto.WINDOW_SET_SIZE_MESSAGE_SIZE];
    private byte[] setRotateYBuf = new byte[Proto.WINDOW_SET_ROTATE_Y_MESSAGE_SIZE];
    private byte[] toFrontBuf = new byte[Proto.WINDOW_TO_FRONT_MESSAGE_SIZE];
    private byte[] destroyWindowBuf = new byte[Proto.DESTROY_WINDOW_MESSAGE_SIZE];
    private byte[] slaveCloseWindowBuf = new byte[Proto.SLAVE_CLOSE_WINDOW_MESSAGE_SIZE];
    private int lastPointerX = 0;
    private int lastPointerY = 0;

    /**
     * Create a new instance of ServerProxySlave.
     * @param client The slave client.
     * @param session This app's Wonderland session.
     * @param connectionInfo Subclass-specific data for making a peer-to-peer connection between master and slave.
     * @param disconnectListener The listener to call when the slave is disconnected.
     */
    public ServerProxySlave(ClientXrwSlave client, WonderlandSession session,
                            AppXrwConnectionInfo connectionInfo, DisconnectListener disconnectListener) {
        this.client = client;
        this.session = session;
        this.connectionInfo = connectionInfo;
        this.disconnectListener = disconnectListener;
    }

    public void connect() throws IOException {
        establishConnection();
        initialHandshake();
    }

    public void disconnect() {
        slaveSocket.close();
        if (disconnectListener != null) {
            disconnectListener.disconnected();
        }
    }

    private void establishConnection() throws IOException {

        Socket socket = new Socket(connectionInfo.getHostName(), connectionInfo.getPortNum());
        if (socket == null) {
            throw new IOException("Cannot create socket to connect to host " + connectionInfo.getHostName() +
                    " and port number " + connectionInfo.getPortNum());
        }

        slaveSocket = new SlaveClientSocket(session.getID(), socket, new MyListener());
        if (slaveSocket == null) {
            throw new IOException("Cannot create SlaveSocketchannel for slave");
        }
        slaveSocket.initialize();
    }

    public void cleanup() {
        disconnect();
        client = null;
        scanLineBuf = null;
        bufQueue = null;
    }

    private class MyListener implements ClientSocketListener {

        private boolean welcomeReceived = false;

        public void receivedMessage(BigInteger otherClientID, byte[] message) {

            // Ignore all messages until the welcome message is received
            if (welcomeReceived) {
                bufQueue.enqueue(message);
            } else {
                if (message[0] == ServerMessageType.WELCOME.ordinal()) {
                    bufQueue.enqueue(message);
                    welcomeReceived = true;
                }
            }
        }

        public void otherClientHasLeft(BigInteger otherClientID) {
            cleanup();
            AppXrw.logger.info("Master has disconnected: " + otherClientID);
        }
    }

    private void initialHandshake() {

        String userName = session.getUserID().getUsername();
        int strLen = userName.length();

        // Inform the server  that we have connected by sending a hello message
        // with the name of this user
        byte[] helloBuf = new byte[Proto.HELLO_MESSAGE_SIZE + strLen];
        helloBuf[0] = (byte) Proto.ClientMessageType.HELLO.ordinal();
        helloBuf[1] = (byte) 0; // pad
        helloBuf[2] = (byte) ((strLen >> 8) & 0xff);
        helloBuf[3] = (byte) (strLen & 0xff);
        System.arraycopy(userName.getBytes(), 0, helloBuf, 4, strLen);
        try {
            slaveSocket.send(helloBuf);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        AppXrw.logger.info("Broadcast slave Hello message for user " + userName);

        // Get the welcome message from the server. This contains the client id
        // Note: because the master hub broadcasts to all slaves, there is a chance
        // there might be irrelevant messages queued up for this
        // slave. These should be ignored. This come from the fact that it takes
        // some time after the slave joins the connection for the master to become
        // aware of it and to compose and send the welcome message. During this time
        // if there are any incoming messages from the X server they will be
        // forwarded to this slave even if it has not yet been officially welcomed.
        // Since the content of these messages will be reflected in the welcome message
        // and the slave can't really do anything until it is welcomed we need
        // to ignore these messages.

        ServerMessageType type = null;
        do {
            type = getMessageType();
        } while (type != ServerMessageType.WELCOME);
        // TODO: eventually we should add a timeout

        // Skip 3 bytes of pad
        bufQueue.nextByte();
        bufQueue.nextShort();

        clientId = bufQueue.nextInt();
        System.err.println("My clientId = " + clientId);
        client.setClientId(clientId);

        // Read the initial window state synchronization
        // part of the welcome message
        System.err.println("Waiting to get numWins");
        int numWins = bufQueue.nextInt();
        System.err.println("numWins = " + numWins);
        for (int i = 0; i < numWins; i++) {
            syncWindowStateNext();
        }

        client.restackFromZOrders();
    }

    private void syncWindowStateNext() {
        System.err.println("Enter syncWindowStateNext");

        CreateWindowMsgArgs crtMsgArgs = new CreateWindowMsgArgs();
        WindowXrw win;
        int controllingUserLen;
        int zOrder;
        float rotY;
        Vector3f userDispl = new Vector3f();

        crtMsgArgs.wid = bufQueue.nextInt();
        crtMsgArgs.x = (short) bufQueue.nextInt();
        crtMsgArgs.y = (short) bufQueue.nextInt();
        crtMsgArgs.wAndBorder = bufQueue.nextInt();
        crtMsgArgs.hAndBorder = bufQueue.nextInt();
        crtMsgArgs.borderWidth = bufQueue.nextInt();
        controllingUserLen = bufQueue.nextInt();
        zOrder= bufQueue.nextInt();
        rotY = bufQueue.nextFloat();
        System.err.println("rotY = " + rotY);
        userDispl.x = bufQueue.nextFloat();
        userDispl.y = bufQueue.nextFloat();
        userDispl.z = bufQueue.nextFloat();
        System.err.println("userDispl = " + userDispl);
        /* TODO: 0.4 protocol:
        int transientFor = bufQueue.nextInt();
        System.err.println("transientFor = " + transientFor);
         */
        // TODO: 0.4 protocol: skip isTransient
        int transientFor = bufQueue.nextInt();

        crtMsgArgs.decorated = (bufQueue.nextByte() == 1) ? true : false;
        System.err.println("client = " + client);
        System.err.println("crtMsgArgs = " + crtMsgArgs);
        System.err.println("zOrder= " + zOrder);

        // Make sure window is ready to receive data on creation
        win = client.createWindow(crtMsgArgs);
        if (win == null) {
            AppXrw.logger.warning("Cannot create slave window for " + crtMsgArgs.wid);
            return;
        }

        win.setZOrder(zOrder);

        /* TODO: window config 
        win.setRotateY(rotY);
        win.setUserDisplacement(userDispl);
         */

        boolean show = (bufQueue.nextByte() == 1) ? true : false;
        System.err.println("show = " + show);

        if (controllingUserLen > 0) {
            byte[] controllingUserBuf = bufQueue.nextBuffer();
            String controllingUser = new String(controllingUserBuf);
            System.err.println("controlling user = " + controllingUser);
            win.setControllingUser(controllingUser);
        }

        int srcWidth = crtMsgArgs.wAndBorder;
        int srcHeight = crtMsgArgs.hAndBorder;
        int[] pixels = new int[srcWidth * srcHeight];

        for (int y = 0; y < srcHeight; y++) {
            int srcLineOffset = y * srcWidth;
            for (int x = 0; x < srcWidth; x++) {
                pixels[srcLineOffset + x] = bufQueue.nextInt();
            }
        }
        win.displayPixels(0, 0, srcWidth, srcHeight, pixels);

        /* TODO: 0.4 protocol:
        WindowXrw winTransientFor = client.lookupWindow(transientFor);
        win.setVisibleApp(show, winTransientFor);
         */
        win.setVisibleApp(show);
    }

    private static void encode(byte[] buf, int startIdx, short value) {
        buf[startIdx + 0] = (byte) ((value >> 8) & 0xff);
        buf[startIdx + 1] = (byte) ((value) & 0xff);
    }

    private static void encode(byte[] buf, int startIdx, int value) {
        buf[startIdx] = (byte) ((value >> 24) & 0xff);
        buf[startIdx + 1] = (byte) ((value >> 16) & 0xff);
        buf[startIdx + 2] = (byte) ((value >> 8) & 0xff);
        buf[startIdx + 3] = (byte) ((value) & 0xff);
    }

    private void chanWrite(int value)
            throws IOException {
        byte[] buf = new byte[4];
        encode(buf, 0, value);
        slaveSocket.send(buf);
    }

    public Proto.ServerMessageType getMessageType() {
        int msgCode = (int) bufQueue.nextByte();
        Proto.ServerMessageType msgType = Proto.ServerMessageType.values()[msgCode];
        return msgType;
    }

    public void getData(CreateWindowMsgArgs msgArgs) {
        //System.err.println("In viewer createwin getData");
        //System.err.print("curBuf = ");
        //bufQueue.printCurBufNumeric();

        msgArgs.decorated = bufQueue.nextByte() != 0;
        bufQueue.nextShort(); // Skip 2 bytes of pad
        msgArgs.wid = bufQueue.nextInt();
        msgArgs.x = bufQueue.nextShort();
        msgArgs.y = bufQueue.nextShort();
        msgArgs.wAndBorder = bufQueue.nextInt();
        msgArgs.hAndBorder = bufQueue.nextInt();
    }

    public void getData(DestroyWindowMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.wid = bufQueue.nextInt();
    }

    public void getData(ShowWindowMsgArgs msgArgs) {
        msgArgs.show = (bufQueue.nextByte() != 0);

        // TODO: 0.4 protocol: skip 2 bytes of transient and pad (ignore transient for now)
        // TODO: 0.5 protocol: skip 2 bytes of pad
        bufQueue.nextShort(); 

        msgArgs.wid = bufQueue.nextInt();

        // TODO: 0.5 protocol: not yet
        //msgArgs.transientFor = bufQueue.nextInt();
    }

    public void getData(ConfigureWindowMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.clientId = bufQueue.nextInt();
        msgArgs.wid = bufQueue.nextInt();
        msgArgs.x = bufQueue.nextShort();
        msgArgs.y = bufQueue.nextShort();
        msgArgs.wAndBorder = bufQueue.nextInt();
        msgArgs.hAndBorder = bufQueue.nextInt();
        msgArgs.sibid = bufQueue.nextInt();
    }

    public void getData(PositionWindowMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.clientId = bufQueue.nextInt();
        msgArgs.wid = bufQueue.nextInt();
        msgArgs.x = bufQueue.nextShort();
        msgArgs.y = bufQueue.nextShort();
    }

    public void getData(RestackWindowMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.clientId = bufQueue.nextInt();
        msgArgs.wid = bufQueue.nextInt();
        msgArgs.sibid = bufQueue.nextInt();
    }

    public void getData(WindowSetDecoratedMsgArgs msgArgs) {
        msgArgs.decorated = (bufQueue.nextByte() == 1) ? true : false;
        bufQueue.nextShort(); // Skip 2 bytes of pad
        msgArgs.wid = bufQueue.nextInt();
    }

    public void getData(WindowSetBorderWidthMsgArgs msgArgs) {
        bufQueue.nextByte();  // Skip 1 byte of pad
        msgArgs.borderWidth = bufQueue.nextShort();
        msgArgs.wid = bufQueue.nextInt();
    }

    public void getData(WindowSetUserDisplMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.clientId = bufQueue.nextInt();
        msgArgs.wid = bufQueue.nextInt();
        int ix = bufQueue.nextInt();
        int iy = bufQueue.nextInt();
        int iz = bufQueue.nextInt();
        msgArgs.userDispl = new Vector3f(Float.intBitsToFloat(ix),
                Float.intBitsToFloat(iy),
                Float.intBitsToFloat(iz));
    }

    public void getData(WindowSetRotateYMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.clientId = bufQueue.nextInt();
        msgArgs.wid = bufQueue.nextInt();
        int iroty = bufQueue.nextInt();
        msgArgs.roty = Float.intBitsToFloat(iroty);
    }

    public void getData(DisplayPixelsMsgArgs msgArgs) {
        int encodingCode = bufQueue.nextByte();
        msgArgs.encoding = Proto.PixelEncoding.values()[encodingCode];

        msgArgs.x = bufQueue.nextShort();
        msgArgs.wid = bufQueue.nextInt();
        msgArgs.y = bufQueue.nextShort();
        msgArgs.w = bufQueue.nextShort();
        msgArgs.h = bufQueue.nextShort();

        // Skip 2 bytes of pad
        bufQueue.nextShort();
    }

    public void getData(CopyAreaMsgArgs msgArgs) {
        // Skip 3 bytes of pad
        bufQueue.nextByte();
        bufQueue.nextShort();

        msgArgs.wid = bufQueue.nextInt();
        msgArgs.srcX = bufQueue.nextInt();
        msgArgs.srcY = bufQueue.nextInt();
        msgArgs.width = bufQueue.nextInt();
        msgArgs.height = bufQueue.nextInt();
        msgArgs.dstX = bufQueue.nextInt();
        msgArgs.dstY = bufQueue.nextInt();
    }

    public void getData(ControllerStatusMsgArgs msgArgs) {
        int status = (int) bufQueue.nextByte();
        msgArgs.status = Proto.ControllerStatus.values()[status];

        // Skip 2 bytes of pad
        bufQueue.nextShort();

        msgArgs.clientId = bufQueue.nextInt();
    }

    public void getData(SetWindowTitleMsgArgs msgArgs) {
        // Skip 3 byte of pad
        bufQueue.nextByte();
        bufQueue.nextShort();

        msgArgs.wid = bufQueue.nextInt();
        int strLen = bufQueue.nextInt();

        byte[] bytes = new byte[strLen];
        for (int i = 0; i < strLen; i++) {
            bytes[i] = bufQueue.nextByte();
        }
        msgArgs.title = new String(bytes);
    }

    public void getData(SlaveCloseWindowMsgArgs msgArgs) {
        bufQueue.skipBytes(3); // Skip 3 bytes of pad
        msgArgs.clientId = bufQueue.nextInt();
        msgArgs.wid = bufQueue.nextInt();
    }

    public void getData() {
    }

    public void getData(UserNameMsgArgs msgArgs) {

        // Skip 1 byte of pad
        bufQueue.nextByte();

        int strLen = bufQueue.nextShort();
        if (strLen > 0) {
            byte[] bytes = new byte[strLen];
            for (int i = 0; i < strLen; i++) {
                bytes[i] = bufQueue.nextByte();
            }
            msgArgs.userName = new String(bytes);
        } else {
            msgArgs.userName = null;
        }

        controllingUserName = msgArgs.userName;
    }

    // Returns null if no user has control
    public String getControllingUser() {
        return controllingUserName;
    }

    public void getData(SetPopupParentMsgArgs msgArgs) {

        // Skip 3 bytes of pad
        bufQueue.nextByte();
        bufQueue.nextShort();

        msgArgs.wid = bufQueue.nextInt();
        msgArgs.parentWid = bufQueue.nextInt();
    }


    // TODO: 0.4 protocol
    public void getData(DisplayCursorMsgArgs msg) {
        // Skip 3 bytes of pad
        bufQueue.nextByte();
        bufQueue.nextShort();

        msg.width = bufQueue.nextShort();
        msg.height = bufQueue.nextShort();
        msg.xhot = bufQueue.nextShort();
        msg.yhot = bufQueue.nextShort();

        int numPixels = msg.width * msg.height;
        msg.pixels = new int[numPixels];
        for (int i : msg.pixels) {
            msg.pixels[i] = bufQueue.nextInt();
        }
    }

    // TODO: 0.4 protocol
    public void getData(MoveCursorMsgArgs msg) {
        // Skip 3 bytes of pad
        bufQueue.nextByte();
        bufQueue.nextShort();

        msg.wid = bufQueue.nextInt();
        msg.x = bufQueue.nextInt();
        msg.y = bufQueue.nextInt();
    }

    // TODO: 0.4 protocol
    public void getData(ShowCursorMsgArgs msg) {
        msg.show = (bufQueue.nextByte() == 1) ? true : false;
    }

    public void setScanLineWidth(int width) {
        // No need to do anything
    }

    public byte[] readScanLine() {
        return bufQueue.nextBuffer();
    }

    public int readRleInt() {
        int value = bufQueue.nextInt();
        return value;
    }

    public void readRleChunk(byte[] buf) {
        bufQueue.nextBytes(buf);
    }

    public void readRleChunk(byte[] buf, int len) {
        bufQueue.nextBytes(buf, len);
    }

    public void writeEvent(int wid, MouseEvent event) throws IOException {
        int mask = 0;
        int n = 0;

        int modifiers = event.getModifiersEx();
        if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            mask |= 0x01;
        }
        if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            mask |= 0x02;
        }
        if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            mask |= 0x04;
        }

        int x = event.getX();
        int y = event.getY();
        lastPointerX = x;
        lastPointerY = y;

        //System.err.println("Send ptr: xy = " + x + ", " + y +
        //		   ", mask = 0x" + Integer.toHexString(mask));

        pointerEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_POINTER.ordinal();
        pointerEventBuf[n++] = (byte) mask;
        pointerEventBuf[n++] = (byte) ((x >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (x & 0xff);
        pointerEventBuf[n++] = (byte) ((y >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (y & 0xff);
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) ((wid >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (wid & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (clientId & 0xff);

        slaveSocket.send(pointerEventBuf);
    }

    public void writeWheelEvent(int wid, MouseWheelEvent event) throws IOException {
        int wheelRotation = event.getWheelRotation();
        int mask = (wheelRotation == -1) ? BUTTON4_MASK : BUTTON5_MASK;
        int n;

        //System.err.println("send MouseWheelEvent = " + event);

        /* First send button pressevent */
        n = 0;
        pointerEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_POINTER.ordinal();
        pointerEventBuf[n++] = (byte) mask;
        pointerEventBuf[n++] = (byte) ((lastPointerX >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerX & 0xff);
        pointerEventBuf[n++] = (byte) ((lastPointerY >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerY & 0xff);
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) ((wid >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (wid & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (clientId & 0xff);
        slaveSocket.send(pointerEventBuf);

        /* Then send button release event */
        mask = 0;
        n = 0;
        pointerEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_POINTER.ordinal();
        pointerEventBuf[n++] = (byte) mask;
        pointerEventBuf[n++] = (byte) ((lastPointerX >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerX & 0xff);
        pointerEventBuf[n++] = (byte) ((lastPointerY >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerY & 0xff);
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) ((wid >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (wid & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (clientId & 0xff);
        slaveSocket.send(pointerEventBuf);
    }

    public void writeEvent(KeyEvent event) throws IOException {
        char keyChar;
        int keyCode;
        int keySym;
        int n = 0;

        /* First, try to get the keysym from the keychar */
        keyChar = event.getKeyChar();
        keyCode = event.getKeyCode();
        keySym = UnicodeToKeysym.getKeysym(keyChar);

        /* Next, try to get it from the keycode */
        if (keySym == -1) {
            keySym = KeycodeToKeysym.getKeysym(keyCode);
        }

        /*
         ** If we still don't have it and the keychar is less than 0x20
         ** the control key is pressed and has already been sent. So
         ** just send the key and not its controlled version.
         */
        if (keySym == -1) {
            if (keyChar < 0x20) {
                keySym = keyChar + 0x60;
            }
        }

        if (keySym == -1) {
            AppXrw.logger.warning("Could not find keysym for key event = " + event);
            AppXrw.logger.warning("Key event not sent to remote window server");
            return;
        }

        //System.err.println("Send keySym = " + keySym);

        keyEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_KEY.ordinal();
        keyEventBuf[n++] = (byte) ((event.getID() == KeyEvent.KEY_PRESSED) ? 1 : 0);
        keyEventBuf[n++] = (byte) 0;
        keyEventBuf[n++] = (byte) 0;
        keyEventBuf[n++] = (byte) ((keySym >> 24) & 0xff);
        keyEventBuf[n++] = (byte) ((keySym >> 16) & 0xff);
        keyEventBuf[n++] = (byte) ((keySym >> 8) & 0xff);
        keyEventBuf[n++] = (byte) (keySym & 0xff);
        keyEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        keyEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        keyEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        keyEventBuf[n++] = (byte) (clientId & 0xff);

        slaveSocket.send(keyEventBuf);
    }

    public void writeTakeControl(boolean steal) throws IOException {
        int n = 0;

        System.err.println("ServerProxySlave: clientId = " + clientId);
        takeControlBuf[n++] = (byte) Proto.ClientMessageType.TAKE_CONTROL.ordinal();
        takeControlBuf[n++] = (byte) (steal ? 1 : 0);
        takeControlBuf[n++] = (byte) 0;
        takeControlBuf[n++] = (byte) 0;
        takeControlBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        takeControlBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        takeControlBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        takeControlBuf[n++] = (byte) (clientId & 0xff);
        slaveSocket.send(takeControlBuf);
    }

    public void writeReleaseControl() throws IOException {
        int n = 0;

        System.err.println("ServerProxySlave: clientId = " + clientId);
        releaseControlBuf[n++] = (byte) Proto.ClientMessageType.RELEASE_CONTROL.ordinal();
        releaseControlBuf[n++] = (byte) 0;
        releaseControlBuf[n++] = (byte) 0;
        releaseControlBuf[n++] = (byte) 0;
        releaseControlBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        releaseControlBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        releaseControlBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        releaseControlBuf[n++] = (byte) (clientId & 0xff);
        slaveSocket.send(releaseControlBuf);
    }

    public void writeSetWindowTitle(int wid, String title) throws IOException {
        int strLen = title.length();
        int n = 0;

        /* First send header */
        setWindowTitleBuf[n++] = (byte) Proto.ClientMessageType.SET_WINDOW_TITLE.ordinal();
        setWindowTitleBuf[n++] = 0; // Pad
        setWindowTitleBuf[n++] = 0; // Pad
        setWindowTitleBuf[n++] = 0; // Pad
        setWindowTitleBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setWindowTitleBuf[n++] = (byte) (wid & 0xff);
        setWindowTitleBuf[n++] = (byte) ((strLen >> 24) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((strLen >> 16) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((strLen >> 8) & 0xff);
        setWindowTitleBuf[n++] = (byte) (strLen & 0xff);
        slaveSocket.send(setWindowTitleBuf);

        /* Then send the string */
        byte[] strBytes = title.getBytes();
        slaveSocket.send(strBytes);

        /* DEBUG */
        if (strBytes.length != strLen) {
            throw new RuntimeException("title length mismatch");
        }

    }

    public void windowSetUserDisplacement(int cid, int wid, Vector3f userDispl) throws IOException {
        int n = 0;
        int ix = Float.floatToRawIntBits(userDispl.x);
        int iy = Float.floatToRawIntBits(userDispl.y);
        int iz = Float.floatToRawIntBits(userDispl.z);

        setUserDisplBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_SET_USER_DISPLACEMENT.ordinal();
        setUserDisplBuf[n++] = 0; // Pad
        setUserDisplBuf[n++] = 0; // Pad
        setUserDisplBuf[n++] = 0; // Pad
        setUserDisplBuf[n++] = (byte) ((cid >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((cid >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((cid >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (cid & 0xff);
        setUserDisplBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (wid & 0xff);
        setUserDisplBuf[n++] = (byte) ((ix >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((ix >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((ix >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (ix & 0xff);
        setUserDisplBuf[n++] = (byte) ((iy >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iy >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iy >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (iy & 0xff);
        setUserDisplBuf[n++] = (byte) ((iz >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iz >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iz >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (iz & 0xff);

        slaveSocket.send(setUserDisplBuf);
    }

    public void windowSetSize(int cid, int wid, int w, int h) throws IOException {
        int n = 0;

        setSizeBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_SET_SIZE.ordinal();
        setSizeBuf[n++] = 0; // Pad
        setSizeBuf[n++] = 0; // Pad
        setSizeBuf[n++] = 0; // Pad
        setSizeBuf[n++] = (byte) ((cid >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((cid >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((cid >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (cid & 0xff);
        setSizeBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (wid & 0xff);
        setSizeBuf[n++] = (byte) ((w >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((w >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((w >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (w & 0xff);
        setSizeBuf[n++] = (byte) ((h >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((h >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((h >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (h & 0xff);

        slaveSocket.send(setSizeBuf);
    }

    public void windowSetRotateY(int cid, int wid, float rotY) throws IOException {
        int n = 0;
        int irotY = Float.floatToRawIntBits(rotY);

        setRotateYBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_SET_ROTATE_Y.ordinal();
        ;
        setRotateYBuf[n++] = 0; // Pad
        setRotateYBuf[n++] = 0; // Pad
        setRotateYBuf[n++] = 0; // Pad
        setRotateYBuf[n++] = (byte) ((cid >> 24) & 0xff);
        setRotateYBuf[n++] = (byte) ((cid >> 16) & 0xff);
        setRotateYBuf[n++] = (byte) ((cid >> 8) & 0xff);
        setRotateYBuf[n++] = (byte) (cid & 0xff);
        setRotateYBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setRotateYBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setRotateYBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setRotateYBuf[n++] = (byte) (wid & 0xff);
        setRotateYBuf[n++] = (byte) ((irotY >> 24) & 0xff);
        setRotateYBuf[n++] = (byte) ((irotY >> 16) & 0xff);
        setRotateYBuf[n++] = (byte) ((irotY >> 8) & 0xff);
        setRotateYBuf[n++] = (byte) (irotY & 0xff);

        slaveSocket.send(setRotateYBuf);
    }

    public void windowToFront(int cid, int wid) throws IOException {
        int n = 0;

        toFrontBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_TO_FRONT.ordinal();
        toFrontBuf[n++] = 0; // Pad
        toFrontBuf[n++] = 0; // Pad
        toFrontBuf[n++] = 0; // Pad
        toFrontBuf[n++] = (byte) ((cid >> 24) & 0xff);
        toFrontBuf[n++] = (byte) ((cid >> 16) & 0xff);
        toFrontBuf[n++] = (byte) ((cid >> 8) & 0xff);
        toFrontBuf[n++] = (byte) (cid & 0xff);
        toFrontBuf[n++] = (byte) ((wid >> 24) & 0xff);
        toFrontBuf[n++] = (byte) ((wid >> 16) & 0xff);
        toFrontBuf[n++] = (byte) ((wid >> 8) & 0xff);
        toFrontBuf[n++] = (byte) (wid & 0xff);

        slaveSocket.send(toFrontBuf);
    }

    public void destroyWindow(int wid) throws IOException {
        int n = 0;

        destroyWindowBuf[n++] = (byte) Proto.ClientMessageType.DESTROY_WINDOW.ordinal();
        destroyWindowBuf[n++] = 0; // Pad
        destroyWindowBuf[n++] = 0; // Pad
        destroyWindowBuf[n++] = 0; // Pad
        destroyWindowBuf[n++] = (byte) ((wid >> 24) & 0xff);
        destroyWindowBuf[n++] = (byte) ((wid >> 16) & 0xff);
        destroyWindowBuf[n++] = (byte) ((wid >> 8) & 0xff);
        destroyWindowBuf[n++] = (byte) (wid & 0xff);

        slaveSocket.send(destroyWindowBuf);
    }

    public void slaveCloseWindow(int clientId, int wid) throws IOException {
        int n = 0;

        slaveCloseWindowBuf[n++] = (byte) Proto.ClientMessageType.SLAVE_CLOSE_WINDOW.ordinal();
        slaveCloseWindowBuf[n++] = 0; // Pad
        slaveCloseWindowBuf[n++] = 0; // Pad
        slaveCloseWindowBuf[n++] = 0; // Pad
        slaveCloseWindowBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        slaveCloseWindowBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        slaveCloseWindowBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        slaveCloseWindowBuf[n++] = (byte) (clientId & 0xff);
        slaveCloseWindowBuf[n++] = (byte) ((wid >> 24) & 0xff);
        slaveCloseWindowBuf[n++] = (byte) ((wid >> 16) & 0xff);
        slaveCloseWindowBuf[n++] = (byte) ((wid >> 8) & 0xff);
        slaveCloseWindowBuf[n++] = (byte) (wid & 0xff);

        slaveSocket.send(slaveCloseWindowBuf);
    }


    // For Debug
    private static void print10bytes(byte[] bytes) {
        int n = (bytes.length > 10) ? 10 : bytes.length;
        for (int i = 0; i < n; i++) {
            System.err.print(Integer.toHexString(bytes[i] & 0xff) + " ");
        }
        System.err.println();
    }
}
