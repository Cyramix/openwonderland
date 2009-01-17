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
package org.jdesktop.wonderland.modules.audiomanager.client;

import java.util.ArrayList;
import org.jdesktop.wonderland.common.cell.CellTransform;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.ProximityComponent;
import org.jdesktop.wonderland.client.cell.ProximityListener;

import org.jdesktop.wonderland.client.comms.ClientConnection;
import org.jdesktop.wonderland.client.comms.ResponseListener;

import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;

import org.jdesktop.wonderland.common.ExperimentalAPI;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.ConeOfSilenceEnterCellMessage;

import com.jme.bounding.BoundingVolume;

/**
 * A component that provides a cone of silence
 * 
 * @author jprovino
 */
@ExperimentalAPI
public class ConeOfSilenceComponent extends CellComponent implements ProximityListener {
    
    private static Logger logger = Logger.getLogger(ConeOfSilenceComponent.class.getName());

    private ChannelComponent channelComp;
    
    private ChannelComponent.ComponentMessageReceiver msgReceiver;
    
    public ConeOfSilenceComponent(Cell cell) {
        super(cell);
    }
    
    @Override
    public void setStatus(CellStatus status) {
        switch(status) {
	case DISK:
	    if (msgReceiver!=null) {
                channelComp.removeMessageReceiver(ConeOfSilenceEnterCellMessage.class);
                msgReceiver = null;
            }
            break;

	case BOUNDS:
	    if (msgReceiver==null) {
                msgReceiver = new ChannelComponent.ComponentMessageReceiver() {
                    public void messageReceived(CellMessage message) {
                        ConeOfSilenceEnterCellMessage msg = (ConeOfSilenceEnterCellMessage)message;
                    }
                };                    
        	channelComp = cell.getComponent(ChannelComponent.class);
                channelComp.addMessageReceiver(ConeOfSilenceEnterCellMessage.class, msgReceiver);

		ProximityComponent comp = new ProximityComponent(cell);

		BoundingVolume[] boundingVolume = new BoundingVolume[1];

		boundingVolume[0] = cell.getLocalBounds();

		comp.addProximityListener(this, boundingVolume);

		cell.addComponent(comp);
            }
	    break;
        }
    }

    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);
    }
    
    public void viewEnterExit(boolean entered, Cell cell, CellID viewCellID, BoundingVolume proximityVolume,
	    int proximityIndex) {

	logger.info("cellID " + cell.getCellID() + " viewCellID " + viewCellID + " entered = " + entered);

	SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

	channelComp.send(new ConeOfSilenceEnterCellMessage(cell.getCellID(), viewCellID, sc.getCallID(), entered));
    }

}
