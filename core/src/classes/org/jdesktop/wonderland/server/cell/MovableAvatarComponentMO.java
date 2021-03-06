/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.server.cell;

import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.ComponentLookupClass;
import org.jdesktop.wonderland.common.cell.messages.MovableAvatarMessage;
import org.jdesktop.wonderland.common.cell.messages.MovableMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.MovableAvatarComponentClientState;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import com.sun.sgs.app.AppContext;

/**
 *
 * @author paulby
 */
@ComponentLookupClass(MovableComponentMO.class)
public class MovableAvatarComponentMO extends MovableComponentMO {

    private int currentTrigeer=-1;
    private String currentAnimationName=null;
    
    public MovableAvatarComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected Class getMessageClass() {
        return MovableAvatarMessage.class;
    }

    @Override
    public void moveRequest(WonderlandClientID clientID, MovableMessage msg) {
        MovableAvatarMessage aMsg = (MovableAvatarMessage) msg;
        CellTransform transform = msg.getCellTransform();

        CellMO cell = cellRef.getForUpdate();
        ChannelComponentMO channelComponent;
        cell.setLocalTransform(transform);

        //store trigger value
        if(aMsg.getTrigger()!=-1) {
            currentTrigeer = aMsg.getTrigger();
            currentAnimationName = aMsg.getAnimationName();
            AppContext.getDataManager().markForUpdate(this);
        }
        
        channelComponent = channelComponentRef.getForUpdate();

        if (cell.isLive()) {
            channelComponent.sendAll(clientID, 
                    MovableAvatarMessage.newMovedMessage(cell.getCellID(), 
                    transform, aMsg.getTrigger(), aMsg.isPressed(), 
                    aMsg.getAnimationName(), aMsg.getHeight(),
                    aMsg.isCollision()));
        }
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {
        
        // If the given cellClientState is null, create a new one
        if (state == null) {
            state = new MovableAvatarComponentClientState();
        }
        ((MovableAvatarComponentClientState)state).setAnimationName(currentAnimationName);
        ((MovableAvatarComponentClientState)state).setTrigger(currentTrigeer);
        
        return super.getClientState(state, clientID, capabilities);
    }
    
    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.client.cell.MovableAvatarComponent";
    }
}
