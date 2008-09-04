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
package org.jdesktop.wonderland.client.cell;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 *
 * Provides an interface to the cell rendering code. This abstraction allows
 * for both 2D and 3D renderers for a cells content.
 * 
 * TODO Add Heuristic based LOD listeners
 * TODO Add view distance listeners
 * TODO Frustum enter/exit listeners
 * 
 * @author paulby
 */
@ExperimentalAPI
public interface CellRenderer {
    
    /**
     * The cell has moved, the transform is the cell position in world coordinates
     * @param cellLocal2World the cell Local to World transform
     */
    public void cellTransformUpdate(CellTransform cellLocal2World);
}
