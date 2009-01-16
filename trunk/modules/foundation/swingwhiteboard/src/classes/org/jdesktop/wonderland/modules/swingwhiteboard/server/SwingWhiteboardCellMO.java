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
package org.jdesktop.wonderland.modules.swingwhiteboard.server;

import com.jme.math.Vector2f;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.modules.swingwhiteboard.common.SwingWhiteboardCellServerState;
import org.jdesktop.wonderland.modules.swingwhiteboard.common.WhiteboardCompoundCellMessage;
import org.jdesktop.wonderland.modules.swingwhiteboard.common.WhiteboardAction.Action;
import org.jdesktop.wonderland.modules.swingwhiteboard.common.SwingWhiteboardCellClientState;
import org.jdesktop.wonderland.modules.swingwhiteboard.common.WhiteboardCommand.Command;
import org.jdesktop.wonderland.modules.swingwhiteboard.common.SwingWhiteboardTypeName;
import org.jdesktop.wonderland.modules.appbase.server.App2DCellMO;
import org.jdesktop.wonderland.modules.appbase.server.AppTypeMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.cell.ChannelComponentImplMO;

/**
 * A server cell associated with a whiteboard
 *
 * @author nsimpson,deronj
 */

@ExperimentalAPI
public class SwingWhiteboardCellMO extends App2DCellMO {

    private static final Logger logger = Logger.getLogger(SwingWhiteboardCellMO.class.getName());
    
    // The messages list contains the current state of the whiteboard.
    // It's updated every time a client makes a change to the whiteboard
    // so that when new clients join, they receive the current state
    private static LinkedList<WhiteboardCompoundCellMessage> messages;
    private static WhiteboardCompoundCellMessage lastMessage;
    
    /** The communications component used to broadcast to all clients */
    private ManagedReference<SwingWhiteboardComponentMO> commComponentRef = null;

    /** The preferred width (from the WFS file) */
    private int preferredWidth;

    /** The preferred height (from the WFS file) */
    private int preferredHeight;

    /** Default constructor, used when the cell is created via WFS */
    public SwingWhiteboardCellMO() {
        super();
        addComponent(new ChannelComponentImplMO(this), ChannelComponentMO.class);
	SwingWhiteboardComponentMO commComponent = new SwingWhiteboardComponentMO(this);
        commComponentRef = AppContext.getDataManager().createReference(commComponent); 
        addComponent(commComponent);
        messages = new LinkedList<WhiteboardCompoundCellMessage>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getClientCellClassName(WonderlandClientID clientID, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.modules.swingwhiteboard.client.SwingWhiteboardCell";
    }

    /** 
     * {@inheritDoc}
     */
    public AppTypeMO getAppType () {
	return new SwingWhiteboardAppTypeMO();
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    protected CellClientState getClientState (CellClientState cellClientState, 
						  WonderlandClientID clientID, 
						  ClientCapabilities capabilities) {
	System.err.println("Enter SwingWhiteboardCellMO.getClientState");
        if (cellClientState == null) {
            cellClientState = new SwingWhiteboardCellClientState(pixelScale);
        }
        ((SwingWhiteboardCellClientState)cellClientState).setPreferredWidth(preferredWidth);
        ((SwingWhiteboardCellClientState)cellClientState).setPreferredHeight(preferredHeight);
        return super.getClientState(cellClientState, clientID, capabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerState(CellServerState state) {
	super.setServerState(state);

	SwingWhiteboardCellServerState serverState = (SwingWhiteboardCellServerState) state;
	preferredWidth = serverState.getPreferredWidth();
	preferredHeight = serverState.getPreferredHeight();
	pixelScale = new Vector2f(serverState.getPixelScaleX(), serverState.getPixelScaleY());
    }

    /**
     * Process a message from a client.
     *
     * Sync message: send all accumulated messages back to the client (the sender).
     * All other messages: broadcast to <bold>all</bold> cells (including the sender!)
     *
     * @param clientSender The sender object for the client who sent the message.
     * @param clientSession The session for the client who sent the message.
     * @param message The message which was received.
     * @param commComponent The communications component that received the message.
     */
    public void receivedMessage(WonderlandClientSender clientSender, WonderlandClientID clientID, CellMessage message) {
        WhiteboardCompoundCellMessage cmsg = (WhiteboardCompoundCellMessage)message;
        logger.fine("received whiteboard message: " + cmsg);

	SwingWhiteboardComponentMO commComponent = commComponentRef.getForUpdate();

        if (cmsg.getAction() == Action.REQUEST_SYNC) {
            logger.fine("sending " + messages.size() + " whiteboard sync messages");
            Iterator<WhiteboardCompoundCellMessage> iter = messages.iterator();
            
            while (iter.hasNext()) {
                WhiteboardCompoundCellMessage msg = iter.next();
		clientSender.send(clientID, msg);
            }
        } else {

	    // Create the copy of the message to be broadcast to clients
            WhiteboardCompoundCellMessage msg = new WhiteboardCompoundCellMessage(
						    cmsg.getClientID(), cmsg.getCellID(), cmsg.getAction());
            switch (cmsg.getAction()) {
                case SET_COLOR:
                    // color
                    msg.setColor(cmsg.getColor());
                    break;
                case MOVE_TO:
                case DRAG_TO:
                    // position
                    msg.setPositions(cmsg.getPositions());
                    break;
                case REQUEST_SYNC:
                    break;
                case EXECUTE_COMMAND:
                    // command
                    msg.setCommand(cmsg.getCommand());
                    break;
            }
            
            // record the message in setup data (move events are not recorded)
            if (cmsg.getAction() == Action.EXECUTE_COMMAND) {
                if (cmsg.getCommand() == Command.ERASE) {
                    // clear the action history
                    logger.fine("clearing message history");
                    messages.clear();
                }
            } else {
                if (cmsg.getAction() != Action.MOVE_TO) {
                    if ((lastMessage != null) &&
			lastMessage.getAction() == Action.MOVE_TO) {
                        messages.add(lastMessage);
                    }

		    // Must guarantee that the original sender doesn't ignore this when it is played back during a sync
		    cmsg.setClientID(null);

                    messages.add(cmsg);
                }
            }
            lastMessage = cmsg;

	    // Broadcast message to all clients (including the original sender of the message).
            commComponent.sendAllClients(clientID, msg);
        }
    }
}
