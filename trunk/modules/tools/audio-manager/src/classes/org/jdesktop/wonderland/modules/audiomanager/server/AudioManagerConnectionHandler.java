/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.audiomanager.server;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.DisconnectCallMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetVoiceBridgeMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetVoiceBridgeResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.MuteCallMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlaceCallMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlayTreatmentMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.TransferCallMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.VoiceChatMessage;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.common.comms.ConnectionType;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellMO;

import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;

import java.io.Serializable;

import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.BridgeInfo;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.VoiceManager;

import com.sun.voip.CallParticipant;

import com.sun.voip.client.connector.CallStatus;

import java.io.IOException;

import java.math.BigInteger;

/**
 * Audio Manager
 * 
 * @author jprovino
 */
public class AudioManagerConnectionHandler 
        implements ClientConnectionHandler, Serializable {

    private static final Logger logger =
            Logger.getLogger(AudioManagerConnectionHandler.class.getName());
    
    private ConcurrentHashMap<BigInteger, String> sessionCallIDMap = 
	new ConcurrentHashMap();

    private static AudioManagerConnectionHandler handler;

    public static AudioManagerConnectionHandler getInstance() {
	if (handler == null) {
	    handler = new AudioManagerConnectionHandler();
	}

	return handler;
    }

    private AudioManagerConnectionHandler() {
        super();
    }

    public ConnectionType getConnectionType() {
        return AudioManagerConnectionType.CONNECTION_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
	logger.fine("Audio Server manager connection registered");
    }

    public void clientConnected(WonderlandClientSender sender, 
	    WonderlandClientID clientID, Properties properties) {

	logger.fine("client connected...");
    }

    public void messageReceived(WonderlandClientSender sender, 
	    WonderlandClientID clientID, Message message) {

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	if (message instanceof GetVoiceBridgeMessage) {
	    //System.out.println("Got GetVoiceBridgeMessage");

	    BridgeInfo bridgeInfo;

	    try {
		bridgeInfo = vm.getVoiceBridge();

	        System.out.println("Senging voice bridge info '" + bridgeInfo + "'");
	    } catch (IOException e) {
		System.out.println("unable to get voice bridge:  " + e.getMessage());
		return;
	    }

	    sender.send(clientID, new GetVoiceBridgeResponseMessage(bridgeInfo.toString()));
	    return;
	}

	if (message instanceof PlaceCallMessage) {
	    logger.fine("Got PlaceCallMessage from " + clientID);

	    placeCall(clientID, (PlaceCallMessage) message);
	    return;
	}

	if (message instanceof MuteCallMessage) {
	    MuteCallMessage msg = (MuteCallMessage) message;

	    String callID = msg.getCallID();

	    Call call = vm.getCall(callID);

	    if (call == null) {
		logger.fine("Unable to mute/unmute call " + callID);
		return;
	    }	

	    try {
	        call.mute(msg.isMuted());
	    } catch (IOException e) {
		logger.warning("Unable to mute/unmute call " + callID + ": "
		    + e.getMessage());
		return;
	    }

	    return;
	}
	
	if (message instanceof TransferCallMessage) {
	    TransferCallMessage msg = (TransferCallMessage) message;

	    String callID = msg.getPresenceInfo().callID;

	    Call call = vm.getCall(callID);

	    if (call == null) {
		if (msg.getCancel() == true) {
		    return;
		}

		double x = 0;
		double y = 0;
		double z = 0;
		double orientation = 0;

		Player player = vm.getPlayer(callID);

		if (player != null) {
		    x = -player.getX();
		    y = player.getY();
		    z = player.getZ();
		    orientation = player.getOrientation();
		}
		
		placeCall(clientID, new PlaceCallMessage(msg.getPresenceInfo(), msg.getPhoneNumber(), 
		    x, y, z, orientation, true));
		return;
	    }

	    CallParticipant cp = call.getSetup().cp;

	    if (msg.getCancel() == true) {
	        try {
	            call.transfer(cp, true);
	        } catch (IOException e) {
		    logger.warning("Unable to cancel call transfer:  " + e.getMessage());
	        }
		return;
	    }

            cp.setPhoneNumber(msg.getPhoneNumber());

	    setJoinConfirmation(cp);

	    try {
	        call.transfer(cp, false);
	    } catch (IOException e) {
		logger.warning("Unable to transfer call:  " + e.getMessage());
	    }
	    return;
	}

	if (message instanceof DisconnectCallMessage) {
	    logger.fine("got DisconnectCallMessage");
	    return;
	}

	if (message instanceof VoiceChatMessage) {
	    VoiceChatHandler.getInstance().processVoiceChatMessage(sender, clientID, 
		(VoiceChatMessage) message);
	    return;
	}

	if (message instanceof PlayTreatmentMessage) {
	    PlayTreatmentMessage msg = (PlayTreatmentMessage) message;

	    Call call = vm.getCall(msg.getCallID());

	    if (call == null) {
		logger.warning("No call for " + msg.getCallID());
		return;
	    }

	    try {
		call.playTreatment(msg.getTreatment());
	    } catch (IOException e) {
		logger.warning("Unable to play treatment " + msg.getTreatment() 
		    + " to call " + call + ": " + e.getMessage());
	    }
	    return;
	}

        throw new UnsupportedOperationException("Unknown message:  " + message);
    }

    private void placeCall(WonderlandClientID clientID, PlaceCallMessage msg) {
	PresenceInfo info = msg.getPresenceInfo();

	CellMO cellMO = CellManagerMO.getCellManager().getCell(info.cellID);

	AudioParticipantComponentMO audioParticipantComponentMO = 
	    cellMO.getComponent(AudioParticipantComponentMO.class);

	if (audioParticipantComponentMO == null) {
	    logger.warning("Cell " + cellMO.getCellID() 
		+ " doesn't have an AudioParticipantComponent!");
	    return;
	}

	CallSetup setup = new CallSetup();

	CallParticipant cp = new CallParticipant();

	setup.cp = cp;

	String callID = info.callID;

	if (callID == null) {
	    logger.fine("Can't place call to " + msg.getSipURL()
		+ ".  No cell for " + callID);
	    return;
	}

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	vm.removeCallStatusListener(audioParticipantComponentMO, callID);
	vm.addCallStatusListener(audioParticipantComponentMO, callID);

	cp.setCallId(callID);
	cp.setName(info.userID.getUsername());
        cp.setPhoneNumber(msg.getSipURL());

	setJoinConfirmation(cp);

        cp.setConferenceId(vm.getVoiceManagerParameters().conferenceId);
        cp.setVoiceDetection(true);
        cp.setDtmfDetection(true);
        cp.setVoiceDetectionWhileMuted(true);
        cp.setHandleSessionProgress(true);

	sessionCallIDMap.put(clientID.getID(), callID);

	try {
	    setupCall(callID, setup, msg.getX(), 
		 msg.getY(), msg.getZ(), msg.getDirection());
	} catch (IOException e) {
	    logger.warning("Unable to place call " + cp + " " 
		+ e.getMessage());
	    sessionCallIDMap.remove(clientID.getID());
	}
    }

    private void setJoinConfirmation(CallParticipant cp) {
	if (cp.getPhoneNumber().startsWith("sip:")) {
	    return;
	}

	cp.setJoinConfirmationTimeout(90);

	String callAnsweredTreatment = System.getProperty(
            "com.sun.sgs.impl.app.voice.CALL_ANSWERED_TREATMENT");

	if (callAnsweredTreatment == null || callAnsweredTreatment.length() == 0) {
	    callAnsweredTreatment = "dialtojoin.au";
	}

        cp.setCallAnsweredTreatment(callAnsweredTreatment);
        cp.setCallEstablishedTreatment("joinCLICK.au");
    }

    public static void setupCall(String callID, CallSetup setup, double x, 
	    double y, double z, double direction) throws IOException {

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	Player p = vm.getPlayer(callID);

	Call call;

        call = vm.createCall(callID, setup);

	callID = call.getId();

        PlayerSetup ps = new PlayerSetup();

	if (p == null) {
            ps.x = x;
            ps.y = y;
            ps.z = z;
	} else {
	    ps.x = p.getSetup().x;
	    ps.y = p.getSetup().y;
	    ps.z = p.getSetup().z;
	}

        ps.orientation = direction;
        ps.isLivePlayer = true;

	Player player = vm.createPlayer(callID, ps);

        call.setPlayer(player);
        player.setCall(call);

        vm.getVoiceManagerParameters().livePlayerAudioGroup.addPlayer(player,
            new AudioGroupPlayerInfo(true, AudioGroupPlayerInfo.ChatType.PUBLIC));

        AudioGroupPlayerInfo info = 
	    new AudioGroupPlayerInfo(false, AudioGroupPlayerInfo.ChatType.PUBLIC);

        info.defaultSpeakingAttenuation = 0;

        vm.getVoiceManagerParameters().stationaryPlayerAudioGroup.addPlayer(player, info);
    }

    public void clientDisconnected(WonderlandClientSender sender, WonderlandClientID clientID) {
	BigInteger sessionID = clientID.getID();

	String callID = sessionCallIDMap.get(sessionID);

	if (callID == null) {
	    logger.warning("Unable to find callID for client session " 
		+ sessionID);
	    return;
	}

	sessionCallIDMap.remove(sessionID);

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	Call call = vm.getCall(callID);

	if (call == null) {
	    logger.fine("Can't find call for " + callID);

	    Player player = vm.getPlayer(callID);

	    if (player != null) {
		vm.removePlayer(player);
	    }
	    return;
	}

	try {
	    AppContext.getManager(VoiceManager.class).endCall(call, true);
	} catch (IOException e) {
	    logger.warning("Unable to end call " + call + " " + e.getMessage());
	}
    }

}

