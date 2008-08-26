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

import com.jme.bounding.BoundingVolume;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.setup.CellSetup;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;

/**
 * The client side representation of a cell. Cells are created via the 
 * CellCache and should not be instantiated directly by the user on the client.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class Cell {
    private BoundingVolume cachedVWBounds;
    private BoundingVolume computedWorldBounds;
    private BoundingVolume localBounds;
    private Cell parent;
    private ArrayList<Cell> children = null;
    private CellTransform localTransform;
    private CellTransform local2VW = new CellTransform(null, null);
    private CellID cellID;
    private String name=null;
    private CellStatus currentStatus = CellStatus.DISK;
    private CellCache cellCache;
    
    private HashMap<Class, CellComponent> components = new HashMap<Class, CellComponent>();
    
    public enum RendererType { RENDERER_JME, RENDERER_2D };
    
    private HashMap<RendererType, CellRenderer> cellRenderers = new HashMap();
    
    protected static Logger logger = Logger.getLogger(Cell.class.getName());
    
    private ArrayList<TransformChangeListener> transformChangeListeners = null;
    
    /**
     * Instantiate a new cell
     * @param cellID the cells unique ID
     * @param cellCache the cell cache which instantiated, and owns, this cell
     */
    public Cell(CellID cellID, CellCache cellCache) {
        this.cellID = cellID;
        this.cellCache = cellCache;
    }
    
    /**
     * Return the unique id of this cell
     * @return the cell id
     */
    public CellID getCellID() {
        return cellID;
    }
    
    /**
     * Return the cells parent, or null if it have no parent
     * @return
     */
    public Cell getParent() {
        return parent;
    }
    
    /**
     * Return the list of children for this cell, or an empty list if there
     * are no children
     * @return
     */
    public List<Cell> getChildren() {
        if (children==null)
            return new ArrayList<Cell>(0);
        
        synchronized(children) {
            return (List<Cell>) children.clone();
        }
    }
    
    /**
     * Add the child to the set of children of this cell. Throws a MultipleParentException
     * if child is already a child to another cell
     * @param child to add
     * @throws org.jdesktop.wonderland.common.cell.MultipleParentException
     */
    public void addChild(Cell child) throws MultipleParentException {
        if (child.getParent()!=null) {
            throw new MultipleParentException();
        }
        
        if (children==null) {
            children=new ArrayList<Cell>();
        }
        
        synchronized(children) {
            children.add(child);
            child.setParent(this);
        }
    }
    
    /**
     * If this cell supports the capabilities of cellComponent then
     * return an instance of cellComponent associated with this cell. Otherwise
     * return null.
     * 
     * @see MovableCellComponent
     * @param cellComponent
     * @return
     */
    public <T extends CellComponent> T getComponent(Class<T> cellComponentClass) {
        return (T) components.get(cellComponentClass);
    }
    
    /**
     * Add a component to this cell. Only a single instance of each component
     * class can be added to a cell. Adding duplicate components will result in
     * an IllegalArgumentException.
     * 
     * When a component is added component.setStatus is called automatically with
     * the current status of this cell.
     * 
     * @param component
     */
    public void addComponent(CellComponent component) {
        CellComponent previous = components.put(component.getClass(),component);
        if (previous!=null)
            throw new IllegalArgumentException("Adding duplicate component of class "+component.getClass().getName()); 
        component.setStatus(currentStatus);
    }
    
    /**
     * Return a collection of all the components in this cell.
     * The collection is a clone of the internal data structure, so this is a
     * snapshot of the component set.
     * 
     * @return
     */
    public Collection<CellComponent> getComponents() {
        return new ArrayList<CellComponent>(components.values());
    }
    
    /**
     * Set the parent of this cell, called from addChild
     * @param parent
     */
    void setParent(Cell parent) {
        assert(this.parent==null);
        this.parent = parent;
    }
    
    /**
     * Return the number of children
     * 
     * @return
     */
    public int getNumChildren() {
        if (children==null)
            return 0;
        return children.size();
    }
    
    /**
     * Return the transform for this cell
     * @return
     */
    public CellTransform getLocalTransform() {
        if (localTransform==null)
            return null;
        return (CellTransform) localTransform.clone();
    }
    
    /**
     * Set the transform for this cell.
     * 
     * Users should not call this method directly, rather MovableComponent should
     * be used, which will keep the client and server in sync.
     * 
     * @param localTransform
     */
    void setLocalTransform(CellTransform localTransform) {
        if (localTransform==null) {
            this.localTransform=null;
            // Get parent local2VW
            Cell current=getParent();
            while(current!=null) {
                CellTransform parentLocal2VW = current.getLocalToWorldTransform();
                if (parentLocal2VW!=null) {
                    setLocalToWorldTransform(parentLocal2VW);  // this method also calls notifyTransformChangeListeners
                    current = null;
                } else
                    current = current.getParent();
            }
        } else {
            this.localTransform = (CellTransform) localTransform.clone();
            if (parent!=null) {
                local2VW = (CellTransform) localTransform.clone();
                local2VW = local2VW.mul(parent.getLocalToWorldTransform());
                cachedVWBounds = localBounds.clone(cachedVWBounds);
                local2VW.transform(cachedVWBounds);                
            } else if (this instanceof RootCell) {
                System.out.println("SETTING ROOT");
                local2VW = (CellTransform) localTransform.clone();
                cachedVWBounds = localBounds.clone(cachedVWBounds);               
            }
            
            notifyTransformChangeListeners();
        }
        
        if (cachedVWBounds==null) {
            System.out.println("********** NULL cachedVWBounds "+getName() +"  "+localBounds+"  "+localTransform);
            Thread.dumpStack();
        }
                
        for(Cell child : getChildren())
            transformTreeUpdate(this, child);      

        // Notify Renderers that the cell has moved
        for(CellRenderer rend : cellRenderers.values())
            rend.cellTransformUpdate(local2VW);
        
    }
        
    /**
     * Return the local to Virtual World transform for this cell.
     * @return cells local to VWorld transform
     */
    public CellTransform getLocalToWorldTransform() {
        if (local2VW==null)
            return null;
        return (CellTransform) local2VW.clone();
    }
    
    
    /**
     * Set the localToVWorld transform for this cell
     * @param localToVWorld
     */
    void setLocalToWorldTransform(CellTransform localToVWorld) {
        local2VW = (CellTransform) localToVWorld.clone();
        cachedVWBounds = localBounds.clone(cachedVWBounds);
        local2VW.transform(cachedVWBounds);
        
        notifyTransformChangeListeners();
    }
    
    /**
     * Compute the local to vworld of the cell, this for test purposes only
     * @param parent
     * @return
     */
//    private CellTransform computeLocal2VWorld(Cell cell) {
//        LinkedList<CellTransform> transformStack = new LinkedList<CellTransform>();
//        
//        // Get the root
//        Cell current=cell;
//        while(current.getParent()!=null) {
//            transformStack.addFirst(current.localTransform);
//            current = current.getParent();
//        }
//        CellTransform ret = new CellTransform(null, null);
//        for(CellTransform t : transformStack) {
//            if (t!=null)
//                ret.mul(t);
//        }
//        
//        return ret;
//    }

    /**
     * Update local2VWorld and bounds of child and all its children recursively 
     * to reflect changes in a parent
     * 
     * @param parent
     * @param child
     * @return the combined bounds of the child and all it's children
     */
    private BoundingVolume transformTreeUpdate(Cell parent, Cell child) {
        CellTransform parentL2VW = parent.getLocalToWorldTransform();
        
        CellTransform childTransform = child.getLocalTransform();
        
        if (childTransform!=null) {
            childTransform.mul(parentL2VW);
            child.setLocalToWorldTransform(childTransform);
        } else {
            child.setLocalToWorldTransform(parentL2VW);
        }
        
        BoundingVolume ret = child.getWorldBounds();
        
        Iterator<Cell> it = child.getChildren().iterator();
        while(it.hasNext()) {
            ret.mergeLocal(transformTreeUpdate(child, it.next()));
        }
        
        child.setWorldBounds(ret);
                
        return null;
    }
    
    /**
     * Returns the world bounds, this is the local bounds transformed into VW 
     * coordinates. These bounds do not include the subgraph bounds. This call 
     * is only valid for live cells.
     * 
     * @return world bounds
     */
    public BoundingVolume getWorldBounds() {
        return cachedVWBounds;
    }

    /**
     * Set the World Bounds for this cell
     * @param cachedVWBounds
     */
    private void setWorldBounds(BoundingVolume cachedVWBounds) {
        this.cachedVWBounds = cachedVWBounds;
    }
    
    /**
     * Return the name for this cell (defaults to cellID)
     * @return
     */
    public String getName() {
        if (name==null)
            return cellID.toString();
        return name;
    }

    /**
     * Set a name for the cell
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the local bounds for this cell. Local bounds are in the cells
     * coordinate system
     * 
     * @return local bounds for this cell
     */
    public BoundingVolume getLocalBounds() {
        return localBounds.clone(null);
    }

    /**
     * Set the local bounds for this cell
     * @param localBounds
     */
    public void setLocalBounds(BoundingVolume localBounds) {
        this.localBounds = localBounds;
    }

    /**
     * Return the cell cache which instantiated and owns this cell.
     */
    public CellCache getCellCache() {
        return cellCache;
    }
    
    /**
     * Returns the status of this cell
     * Cell states
     *
     * DISK - Cell is on disk with no memory footprint
     * BOUNDS - Cell object is in memory with bounds initialized, NO geometry is loaded
     * INACTIVE - All cell data is in memory
     * ACTIVE - Cell is within the avatars proximity bounds
     * VISIBLE - Cell is in the view frustum
     *
     * @return returns CellStatus
     */
    public CellStatus getStatus() {
        return this.currentStatus;
    }
    
    /**
     * Set the status of this cell
     *
     *
     * Cell states
     *
     * DISK - Cell is on disk with no memory footprint
     * BOUNDS - Cell object is in memory with bounds initialized, NO geometry is loaded
     * INACTIVE - All cell data is in memory
     * ACTIVE - Cell is within the avatars proximity bounds
     * VISIBLE - Cell is in the view frustum
     * 
     * The system guarantees that if a change is made between non adjacent status, say from BOUNDS to VISIBLE
     * that setStatus will automatically be called for the intermediate values.
     * 
     * If you overload this method in your own class you must call super.setStatus(...) as the first operation
     * in your method.
     *
     * @param status the cell status
     * @return true if the status was changed, false if the new and previous status are the same
     */
    public boolean setStatus(CellStatus status) {
        if (currentStatus==status)
            return false;
        
        int ord = status.ordinal();
        int currentOrd = currentStatus.ordinal();
        if (ord>currentOrd+1 || ord<currentOrd-1) {
            int t = currentOrd;
            int dir = (ord>currentOrd ? 1 : -1);
            setStatus(CellStatus.values()[t+dir]);
        }
        
        currentStatus = status;
        
        for(CellComponent component : components.values())
            component.setStatus(status);
        
        switch(status) {
            case DISK :
                if (transformChangeListeners!=null) {
                    transformChangeListeners.clear();
                    transformChangeListeners = null;
                }
                
                if (components!=null) {
                    components.clear();
                }
                break;
        }
        
        CellManager.getCellManager().notifyCellStatusChange(this, status);
        
        return true;
    }
    
    /**
     * Called once after the cell is instantiated, for cells that require
     * setup data. This method is called once the cell has been placed in
     * the cell tree hierarchy.
     * 
     * @param setupData
     */
    public void setupCell(CellSetup setupData) {
        
    }
    
    /**
     * Called occasioanally by the server if the cell needs to reconfigure
     * it's internal state. This is intended to be a heavyweight operation
     * not for continous changes.
     * 
     * Example use is when the wfs world is reloaded
     * 
     * @param updateData
     */
    public void reconfigureCell(CellSetup updateData) {
        
    }
    
    /**
     * Create the renderer for this cell
     */
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        Logger.getAnonymousLogger().warning(this.getClass().getName()+" createEntity returning null");
        return null;
    }
    
    /**
     * Return the renderer of the given type for this cell. If a renderer of the
     * requested type is not available null will be returned
     * @return
     */
    public CellRenderer getCellRenderer(RendererType rendererType) {
        CellRenderer ret = cellRenderers.get(rendererType);
        if (ret==null) {
            ret = createCellRenderer(rendererType);
            cellRenderers.put(rendererType, ret);
        }
        
        return ret;
    }
    
    /**
     * Add a TransformChangeListener to this cell. The listener will be
     * called for any changes to the cells transform
     * 
     * @param listener to add
     */
    public void addTransformChangeListener(TransformChangeListener listener) {
        if (transformChangeListeners==null)
            transformChangeListeners = new ArrayList();
        transformChangeListeners.add(listener);
    }
    
    /**
     * Remove the specified listener.
     * @param listener to be removed
     */
    public void removeTransformChangeListener(TransformChangeListener listener) {
        transformChangeListeners.remove(listener);
    }
    
    private void notifyTransformChangeListeners() {
        if (transformChangeListeners==null)
            return;
        
        for(TransformChangeListener listener : transformChangeListeners)
            listener.transformChanged(this);
    }
}
