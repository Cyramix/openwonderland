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
package org.jdesktop.wonderland.client.input;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
/**
 * A singleton container for all of the processor objects in the Wonderland input subsystem.
 *
 * The <code>InputManager</code> supports event listener (<code>EventListener</code>) objects. 
 * These listeners can be added to entities
 * in the world in order to allow these entities to respond to events. These events can be generated as a result
 * of user input or can be programmatically generated by other parts of the client.
 *
 * The <code>InputManager</code> also supports a set of global event listeners. These are independent of 
 * any entities. The system always delivers all events to global event listeners which are willing 
 * to consume these events. Note: the return values of <code>propagateToParent()</code> and 
 * <code>propagateToUnder()</code> for global listeners
 * are ignored. Note: The <code>pickDetails</code> field of an event is null for events received by the 
 * global listeners.
 *
 * In Wonderland Release 0.4, the Wonderland client provided the notion of an "event mode." The event mode 
 * could be either App or World. When the event mode was App, only event listeners which controlled the
 * shared apps were to consume events. When the event mode was World, only event listeners which controlled
 * the non-app parts of the scene (aka "world") were to consume events. This essentially provided a single focus for
 * all input events, which had a binary value. The new Wonderland <code>InputManager</code> provides a more
 * general model of focus.
 *
 * The <code>InputManager</code> provides "focus sets" for different classes of events. Each distinct event class 
 * can have its own focus set. A focus set is a set of one or more entities that have focus for that 
 * class of event. For example, the <code>KeyEvent3D</code> class can have a focus set consisting of Entity1 and the
 * <code>MouseEvent3D</code> class can have a focus set consisting of Entity2 and Entity3. Unlike Wonderland Release 0.4,
 * each event class can have a different set of focussed entities. Notice also that there can be multiple
 * focussed entities instead of just one.

 * When an entity is handed to an event listener via an event listener method such as, <code>consumesEvent</code> etc.,
 * that method can determine the entity to which the event was distributed by calling <code>Event.getEntity()</code>.
 * The method can determine if the entity has focus by calling <code>Event.isFocussed()</code>. (Note that 
 * <code>isFocussed</code> returns whether the entity had focus at the time the event was delivered to the 
 * listener).
 *
 * Note that the concept of focus in this input system is merely advisory. Event listeners can be programmed to 
 * use the <code>isFocussed</code> flag or ignore it. For example, a shared app event listener could be written
 * to ignore events when the entity of the shared app does not have focus. Or, a 3D "xeyes" type of app could 
 * be written to accept all mouse motion events, regardless of whether their entity has focus,
 * and change the gaze of the eyes to follow the mouse pointer's location no matter where it moves on the screen.
 *
 * It is intended that the Wonderland client provide one or more focus managers which change the 
 * focussed entities based on user input or some other stimuli. These focus managers will be the responsible
 * for implementing the focus management policy--the <code>InputManager</code> merely provides the mechanism.
 * The goal is to allow multiple and different focus managers to be experimented with over time.
 *
 * @author deronj
 */

// TODO: generate 3D enter/exit events for canvas enter exit

@ExperimentalAPI
public abstract class InputManager 
    implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
    private static final Logger logger = Logger.getLogger(InputManager.class.getName());

    /* TODO: the non-embedded swing case is for prototyping only. Eventually this should be true */
    private static boolean ENABLE_EMBEDDED_SWING = false;

    /** The singleton input manager */
    protected static InputManager inputManager;

    /** The singleton input picker. (Used only in non-embedded swing case). */
    protected InputPicker inputPicker;

    /** The singleton event distributor. */
    protected EventDistributor eventDistributor;

    /** The canvas from which this input manager should receive events. */
    protected Canvas canvas;

    /** The global focus wildcard entity */
    protected Entity globalFocusEntity;

    /** The ways that a focus set can be changed. */
    public enum FocusChangeAction { 
	/** Add the given entities to the focus sets of the given event classes. */
	ADD, 
	/** Remove the given entities from the focus sets of the given event classes. */
	REMOVE, 
	/** 
	  * Replace the previous focus sets for the given event classes with a new focus set
	  * which contains the given entities. Note that the new set may be the empty set.
	  */
        REPLACE 
    };

    /**
     * An object which indicates how to modify the focus set.
     */
    public class FocusChange {

	/** The action to perform on the focus sets. */
	public FocusChangeAction action;

	/** The event classes whose focus set is to be changed. */
	public Class[] eventClasses;

	/** The target entities */
	public Entity[] entities;

        /** 
	 * Create an instance of FocusChange.
	 * @param action The action to perform on the focus sets.
	 * @param eventClasses The event classes whose focus sets are to be changed.
	 * @param entities The target entities.
	 * @throws IllegalArgumentException If any class in <code>eventClasses</code> are not the Wonderland 
	 * base event class or one of its subclasses.
	 * 
	 */
	public FocusChange (FocusChangeAction action, Class[] eventClasses, Entity[] entities) {
	    for (Class clazz : eventClasses) {
		if (!Event.class.isAssignableFrom(clazz)) {
		    throw new IllegalArgumentException("Class is not a Wonderland event class or one of its subclasses: " + clazz.getName());
		}
	    }
	    this.action = action;
	    this.eventClasses = eventClasses;
	    this.entities = entities;
	}
    }

    /**
     * Return the input manager singleton.
     */
    static InputManager inputManager () {
        return inputManager;
    }

    /**
     * Return the event distributor singleton.
     */
    EventDistributor getEventDistributor () {
	return eventDistributor;
    }

    /** 
     * Initialize the input manager to receive input events from the given AWT canvas
     * and start the input manager running. This method does not define a camera
     * component, so picking on events will not start occuring until a camera component
     * is specified with a subsequent call to <code>setCameraComponent</code>.
     * @param wm The mtgame world manager.
     * @param canvas The AWT canvas which generates AWT user events.
     */
    public void initialize (WorldManager wm, Canvas canvas) {
	initialize(wm, canvas, null);
    }

    /** 
     * Initialize the input manager to receive input events from the given AWT canvas
     * and start the input manager running. The input manager will perform picks with the
     * given camera. This routine can only be called once. To subsequently change the 
     * camera, use <code>setCameraComponent</code>. To subsequently change the focus manager,
     * use <code>setFocusManager</code>.
     * @param wm The mtgame world manager.
     * @param canvas The AWT canvas which generates AWT user events.
     * @param cameraComp The mtgame camera component to use for picking operations.
     */
    public void initialize (WorldManager wm, Canvas canvas, CameraComponent cameraComp) {
	if (this.canvas != null) {
	    throw new IllegalStateException("initialize has already been called for this InputManager");
	}
	this.canvas = canvas;
	inputPicker.setCanvas(canvas);

	setCameraComponent(cameraComp);

	initializeGlobalFocus(wm);

	canvas.addKeyListener(this);

	if (ENABLE_EMBEDDED_SWING) {
	    logger.fine("Input System init: Embedded Swing case.");
	} else {
	    // When not using Embedded Swing the input manager receives events directly from the AWT canvas.
	    logger.fine("Input System init: Non Swing case.");
	    canvas.addMouseListener(this);
	    canvas.addMouseMotionListener(this);
	    canvas.addMouseWheelListener(this);
	}

	logger.fine("Input System initialization complete.");
    }

    /**
     * Initialize the global focus wildcard entity.
     */
    private void initializeGlobalFocus (WorldManager wm) {
	globalFocusEntity = new Entity("Global Focus");
	wm.addEntity(globalFocusEntity);
    }

    /**
     * Returns the global focus entity.
     */
    public Entity getGlobalFocusEntity () {
	return globalFocusEntity;
    }

    /** 
     * TODO: later: use for injecting a non-deliverable mouse event in order to implement programmatic 
     * (non-user-generated) enter/exit events.
     * INTERNAL ONLY.
     * <br>
     * The synthetic, "priming" mouse event should not be delivered to users. 
     */
    @InternalAPI
    public static class NondeliverableMouseEvent extends MouseEvent {
	NondeliverableMouseEvent (Component source, int id, long when, int modifiers, int x, int y, 
				  int clickCount, boolean popupTrigger, int button) {
	    super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
	}
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseClicked(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }
    
    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseEntered(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseExited(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mousePressed(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseReleased(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseDragged(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseMoved(MouseEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }
    
    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     * <br><br>
     * Only used in the non-embedded swing case.
     */
    @InternalAPI
    public void mouseWheelMoved(MouseWheelEvent e) {
	inputPicker.pickMouseEventNonSwing(e);
    }
    
    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     */
    @InternalAPI
    public void keyPressed(KeyEvent e) {
	inputPicker.pickKeyEvent(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     */
    @InternalAPI
    public void keyReleased(KeyEvent e) {
	inputPicker.pickKeyEvent(e);
    }

    /**
     * INTERNAL ONLY
     * <br><br>
     * <@inheritDoc>
     */
    @InternalAPI
    public void keyTyped(KeyEvent e) {
	inputPicker.pickKeyEvent(e);
    }

    /**
     * Add an event listener to be tried once per event. This global listener can be added only once.
     * Subsequent attempts to add it will be ignored.
     *
     * Note: It is not a good idea to call this from inside EventListener.computeEvent function.
     * However, it is okay to call this from inside EventListener.commitEvent function if necessary.
     *
     * @param listener The global event listener to be added.
     */
    public void addGlobalEventListener (EventListener listener) {
	eventDistributor.addGlobalEventListener(listener);
    }

    /**
     * Remove this global event listener.
     *
     * Note: It is not a good idea to call this from inside EventListener.computeEvent function.
     * However, it is okay to call this from inside EventListener.commitEvent function if necessary.
     *
     * @param listener The entity to which to attach this event listener.
     */
    public void removeGlobalEventListener (EventListener listener) {
	eventDistributor.removeGlobalEventListener(listener);
    }

    /** 
     * Specify the camera component to be used for picking.
     *
     * @param cameraComp The mtgame camera component to use for picking operations.
     */
    public void setCameraComponent (CameraComponent cameraComp) {
	inputPicker.setCameraComponent(cameraComp);
    }

    /** 
     * Returns the camera component that is used for picking.
     */
    public CameraComponent getCameraComponent () {
	return inputPicker.getCameraComponent();
    }

    /**
     * The base changeFocus method. Atomically changes the focus sets. The other focus utility methods call
     * this method. This method is for use by the Wonderland client focus manager.
     * @param changes An array of the changes to make to the focus sets.
     */
    public void changeFocus (FocusChange[] changes) {
	eventDistributor.enqueueEvent(new FocusChangeEvent(changes));
    }

    /**
     * A utility method. Atomically add the given entities to the focus sets of the given event classes.
     * This method is for use by the Wonderland client focus manager.
     * @param eventClasses The event classes whose focus sets are to be modified.
     * @param entities The entities to add to the focus sets.
     * @throws IllegalArgumentException If any class in <code>eventClasses</code> are not the Wonderland 
     * base event class or one of its subclasses.
     */
    public void addFocus (Class[] eventClasses, Entity[] entities) {
	changeFocus(new FocusChange[] { new FocusChange(FocusChangeAction.ADD, eventClasses, entities) });
    }

    /**
     * A utility method. Atomically remove the given entities from the focus sets of the given event classes.
     * This method is for use by the Wonderland client focus manager.
     * @param eventClasses The event classes whose focus sets are to be modified.
     * @param entities The entities to add to the focus sets.
     * @throws IllegalArgumentException If any class in <code>eventClasses</code> are not the Wonderland 
     * base event class or one of its subclasses.
     */
    public void removeFocus (Class[] eventClasses, Entity[] entities) {
	changeFocus(new FocusChange[] { new FocusChange(FocusChangeAction.REMOVE, eventClasses, entities) });
    }

    /**
     * A utility method. Atomically replace the focus sets of the given event classes with the given entities.
     * This method is for use by the Wonderland client focus manager.
     * @param eventClasses The event classes whose focus sets are to be modified.
     * @param entities The entities to add to the focus sets.
     * @throws IllegalArgumentException If any class in <code>eventClasses</code> are not the Wonderland 
     * base event class or one of its subclasses.
     */
    public void replaceFocus (Class[] eventClasses, Entity[] entities) {
	changeFocus(new FocusChange[] { new FocusChange(FocusChangeAction.REPLACE, eventClasses, entities) });
    }

    /**
     * A utility method. Atomically add the given entities to the focus sets of the KeyEvent3D
     * and MouseEvent3D event classes. This method is for use by the Wonderland client focus manager.
     * @param entities The entities to add to the focus sets.
     */
    public void addKeyMouseFocus (Entity[] entities) {
	addFocus(new Class[] { KeyEvent3D.class, MouseEvent3D.class}, entities);
    }

    /**
     * A utility method. Atomically remove the given entities from the focus sets of the KeyEvent3D
     * and MouseEvent3D event classes. This method is for use by the Wonderland client focus manager.
     * @param entities The entities to add to the focus sets.
     */
    public void removeKeyMouseFocus (Entity[] entities) {
	removeFocus(new Class[] { KeyEvent3D.class, MouseEvent3D.class}, entities);
    }

    /**
     * A utility method. Atomically replace the focus sets of the KeyEvent3D and MouseEvent3D 
     * event classes with the given entities. This method is for use by the Wonderland client focus manager.
     * @param entities The entities to add to the focus sets.
     */
    public void replaceKeyMouseFocus (Entity[] entities) {
	replaceFocus(new Class[] { KeyEvent3D.class, MouseEvent3D.class}, entities);
    }

    /**
     * A utility method. Atomically add the given entity to the focus sets of the KeyEvent3D
     * and MouseEvent3D event classes. This method is for use by the Wonderland client focus manager.
     * @param entity The entity to add to the focus sets.
     */
    public void addKeyMouseFocus (Entity entity) {
	addKeyMouseFocus(new Entity[] { entity });
    }

    /**
     * A utility method. Atomically remove the given entity from the focus sets of the KeyEvent3D
     * and MouseEvent3D event classes. This method is for use by the Wonderland client focus manager.
     * @param entity The entity to add to the focus sets.
     */
    public void removeKeyMouseFocus (Entity entity) {
	removeKeyMouseFocus(new Entity[] { entity });
    }

    /**
     * A utility method. Atomically replace the focus sets of the KeyEvent3D and MouseEvent3D 
     * event classes with the given entity. This method is for use by the Wonderland client focus manager.
     * @param entity The entity to add to the focus sets.
     */
    public void replaceKeyMouseFocus (Entity entity) {
	replaceKeyMouseFocus(new Entity[] { entity });
    }
}
