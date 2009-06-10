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
package org.jdesktop.wonderland.modules.placemarks.common;

import org.jdesktop.wonderland.common.comms.ConnectionType;

/**
 * A connection to tell the client the list of Placemarks has changed.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarkConfigConnectionType extends ConnectionType {
    /** The server manager client type */
    public static final ConnectionType CONNECTION_TYPE = new PlacemarkConfigConnectionType();
    
    /** Use the static CLIENT_TYPE, not this constructor */
    public PlacemarkConfigConnectionType() {
        super ("__PlacemarksConfigConnection");
    }
}
