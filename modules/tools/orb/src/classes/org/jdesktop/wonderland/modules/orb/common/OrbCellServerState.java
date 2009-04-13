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
package org.jdesktop.wonderland.modules.orb.common;

import java.util.ArrayList;
import java.util.HashMap;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.spi.CellServerStateSPI;

/**
 * The OrbCellServerState class is the cell that renders an orb cell in
 * world.
 * 
 * @author jprovino
 */
@XmlRootElement(name="orb-cell")
public class OrbCellServerState extends CellServerState
        implements Serializable, CellServerStateSPI {

    private String username;
    private String callID;
    private String playerWithVpCallID;

    /** Default constructor */
    public OrbCellServerState() {
    }
    
    public OrbCellServerState(String username, String callID,
	    String playerWithVpCallID) {

	this.username = username;
	this.callID = callID;
	this.playerWithVpCallID = playerWithVpCallID;
    }
    
    public String getUsername() {
	return username;
    }

    public String getCallID() {
	return callID;
    }

    public String getPlayerWithVpCallID() {
	return playerWithVpCallID;
    }

    public String getServerClassName() {
        return "org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO";
    }

}
