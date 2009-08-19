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
package org.jdesktop.wonderland.modules.affordances.client;

import java.util.Hashtable;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.scenemanager.SceneManager;
import org.jdesktop.wonderland.client.scenemanager.event.SelectionEvent;
import org.jdesktop.wonderland.modules.affordances.client.cell.AffordanceCellComponent;
import org.jdesktop.wonderland.modules.affordances.client.cell.ResizeAffordanceCellComponent;
import org.jdesktop.wonderland.modules.affordances.client.cell.RotateAffordanceCellComponent;
import org.jdesktop.wonderland.modules.affordances.client.cell.TranslateAffordanceCellComponent;

/**
 * A panel to display affordance items on the HUD.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AffordanceHUDPanel extends javax.swing.JPanel {

    /* The detailed "position" panel to set the values by hand */
    private PositionHUDPanel positionHUDPanel = null;
    private static HUDComponent positionHUD = null;

    /** Creates new form AffordanceHUDPanel */
    public AffordanceHUDPanel() {
        initComponents();

        // Paint the labels on the ticks properly from 1.0 to 5.0
        Hashtable<Integer, JComponent> labels = new Hashtable();
        labels.put(0, new JLabel("1.0"));
        labels.put(100, new JLabel("2.0"));
        labels.put(200, new JLabel("3.0"));
        labels.put(300, new JLabel("4.0"));
        labels.put(400, new JLabel("5.0"));
        sizeSlider.setLabelTable(labels);

        // Create the Details frame for later use.
        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        positionHUDPanel = new PositionHUDPanel();
        positionHUD = mainHUD.createComponent(positionHUDPanel);
        positionHUD.setName("Details");
        positionHUD.setPreferredLocation(Layout.SOUTHEAST);

        // add affordances HUD panel to main HUD
        mainHUD.addComponent(positionHUD);

        // Listen for selections to update the HUD panel
        InputManager.inputManager().addGlobalEventListener(new SelectionListener());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        translateToggleButton = new javax.swing.JToggleButton();
        rotateToggleButton = new javax.swing.JToggleButton();
        resizeToggleButton = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        sizeSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        detailsButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        translateToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/affordances/client/resources/WorldEditorTranslate32x32.png"))); // NOI18N
        translateToggleButton.setToolTipText("Move");
        translateToggleButton.setBorderPainted(false);
        translateToggleButton.setContentAreaFilled(false);
        translateToggleButton.setFocusPainted(false);
        translateToggleButton.setIconTextGap(0);
        translateToggleButton.setMargin(new java.awt.Insets(0, -4, 0, -4));
        translateToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        translateToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/affordances/client/resources/WorldEditorTranslateOn32x32.png"))); // NOI18N
        translateToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateToggleButtonActionPerformed(evt);
            }
        });

        rotateToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/affordances/client/resources/WorldEditorRotate32x32.png"))); // NOI18N
        rotateToggleButton.setToolTipText("Rotate");
        rotateToggleButton.setBorderPainted(false);
        rotateToggleButton.setContentAreaFilled(false);
        rotateToggleButton.setFocusPainted(false);
        rotateToggleButton.setMargin(new java.awt.Insets(0, -4, 0, -4));
        rotateToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        rotateToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/affordances/client/resources/WorldEditorRotateOn32x32.png"))); // NOI18N
        rotateToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateToggleButtonActionPerformed(evt);
            }
        });

        resizeToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/affordances/client/resources/WorldEditorScale32x32.png"))); // NOI18N
        resizeToggleButton.setToolTipText("Resize");
        resizeToggleButton.setBorderPainted(false);
        resizeToggleButton.setContentAreaFilled(false);
        resizeToggleButton.setFocusPainted(false);
        resizeToggleButton.setMargin(new java.awt.Insets(0, -4, 0, -4));
        resizeToggleButton.setOpaque(true);
        resizeToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        resizeToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/affordances/client/resources/WorldEditorScaleOn32x32.png"))); // NOI18N
        resizeToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeToggleButtonActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("1x");
        jLabel3.setIconTextGap(0);

        sizeSlider.setMajorTickSpacing(100);
        sizeSlider.setMaximum(400);
        sizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSliderStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Affordance Size");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("5x");
        jLabel5.setIconTextGap(0);

        detailsButton.setText("Details...");
        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(translateToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rotateToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(resizeToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel3)
                        .add(0, 0, 0)
                        .add(sizeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 184, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, 0)
                        .add(jLabel5))
                    .add(layout.createSequentialGroup()
                        .add(75, 75, 75)
                        .add(jLabel1)))
                .add(18, 18, 18)
                .add(detailsButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(translateToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(rotateToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                .add(layout.createSequentialGroup()
                    .add(0, 0, 0)
                    .add(jLabel1)
                    .add(0, 0, 0)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                        .add(jLabel3)
                        .add(sizeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel5))
                    .add(0, 0, 0))
                .add(org.jdesktop.layout.GroupLayout.LEADING, resizeToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(detailsButton))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Indicates that this HUD panel has been closed
     */
    public void closed() {
        // Also close the Details position HUD panel
        positionHUD.setVisible(false);
    }

    /**
     * Updates the GUI items in this panel for the currently selected cell. If
     * there is nothing selected, do nothing.
     *
     * NOTE: This method assumes it is being called in the AWT Event Thread.
     */
    public void updateGUI() {
        // Update the GUI of the Position HUD Panel
        positionHUDPanel.updateGUI();

        // Fetch the currently selected Cell. If none, then do nothing
        Cell cell = getSelectedCell();
        if (cell == null) {
            //frame.setTitle("Edit Cell: <none selected>");
            translateToggleButton.setSelected(false);
            translateToggleButton.setEnabled(false);
            rotateToggleButton.setSelected(false);
            rotateToggleButton.setEnabled(false);
            resizeToggleButton.setSelected(false);
            resizeToggleButton.setEnabled(false);
            sizeSlider.setValue(50);
            sizeSlider.setEnabled(false);
            return;
        }

        // Set the name of the Cell label
        //frame.setTitle("Edit Cell: " + cell.getName());
        translateToggleButton.setEnabled(true);
        rotateToggleButton.setEnabled(true);
        resizeToggleButton.setEnabled(true);
        sizeSlider.setEnabled(true);

        // See if there is a translate component on the Cell. If so, then set
        // the toggle button state.
        CellComponent component = cell.getComponent(TranslateAffordanceCellComponent.class);
        translateToggleButton.setSelected(component != null);
        translateToggleButton.repaint();

        // In theory each affordance component can hold a different size value.
        // In practice this can never happen since the GUI enforces all of the
        // affordances to have the same size. So we just use the size from the
        // translate affordance to initialize the slider value
        if (component != null) {
            float size = ((AffordanceCellComponent) component).getSize();
            sizeSlider.setValue((int) ((size - 1.0f) * 100.0f));
        }
        else {
            sizeSlider.setValue(50);
        }

        // See if there is a rotate component on the Cell. If so, then set the
        // toggle button state.
        component = cell.getComponent(RotateAffordanceCellComponent.class);
        rotateToggleButton.setSelected(component != null);
        rotateToggleButton.repaint();

        // See if there is a resize component on the Cell. If so, then set the
        // toggle button state.
        component = cell.getComponent(ResizeAffordanceCellComponent.class);
        resizeToggleButton.setSelected(component != null);
        resizeToggleButton.repaint();
    }

    private void translateToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateToggleButtonActionPerformed
        setTranslationVisible(translateToggleButton.isSelected());
    }//GEN-LAST:event_translateToggleButtonActionPerformed

    private void rotateToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateToggleButtonActionPerformed
        setRotationVisible(rotateToggleButton.isSelected());
    }//GEN-LAST:event_rotateToggleButtonActionPerformed

    private void resizeToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeToggleButtonActionPerformed
        setResizingVisible(resizeToggleButton.isSelected());
    }//GEN-LAST:event_resizeToggleButtonActionPerformed

    private void sizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeSliderStateChanged
        updateAffordanceSize();
}//GEN-LAST:event_sizeSliderStateChanged

    private void detailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
        positionHUD.setVisible(true);
    }//GEN-LAST:event_detailsButtonActionPerformed

    /**
     * Manually set whether the translation affordance is visible (true) or
     * not (false).
     *
     * @param visible True if the translation affordance should be visible
     */
    public void setTranslationVisible(boolean visible) {
        // Fetch the currently selected Cell. If none, then do nothing
        Cell cell = getSelectedCell();
        if (cell == null) {
            return;
        }

        // Make sure the translate toggle button is in the same state. We need
        // to check to make sure the toggle button isn't already in this
        // state to prevent generated a spurious event.
        if (translateToggleButton.isSelected() != visible) {
            translateToggleButton.setSelected(visible);
        }

        // See if there is already a translate component on the Cell.
        CellComponent component = cell.getComponent(TranslateAffordanceCellComponent.class);
        
        // If we are selecting the translate toggle button, then add the translate
        // component if it is not already on there. Also, set its size.
        if (visible == true) {
            if (component == null) {
                component = new TranslateAffordanceCellComponent(cell);
                cell.addComponent(component);
            }
            ((AffordanceCellComponent)component).setSize(getSliderSize());
        }
        else {
            // Otherwise if the remove exists, then remove it from the Cell
            if (component != null) {
                ((AffordanceCellComponent)component).remove();
            }
        }
    }

    /**
     * Manually set whether the rotation affordance is visible (true) or
     * not (false).
     *
     * @param visible True if the rotation affordance should be visible
     */
    public void setRotationVisible(boolean visible) {
        // Fetch the currently selected Cell. If none, then do nothing
        Cell cell = getSelectedCell();
        if (cell == null) {
            return;
        }

        // Make sure the rotation toggle button is in the same state. We need
        // to check to make sure the toggle button isn't already in this
        // state to prevent generated a spurious event.
        if (rotateToggleButton.isSelected() != visible) {
            rotateToggleButton.setSelected(visible);
        }

        // See if there is already a rotate component on the Cell.
        CellComponent component = cell.getComponent(RotateAffordanceCellComponent.class);

        // If we are selecting the rotation toggle button, then add the rotate
        // component if it is not already on there. Also, set its size.
        if (visible == true) {
            if (component == null) {
                component = new RotateAffordanceCellComponent(cell);
                cell.addComponent(component);
            }
            ((AffordanceCellComponent)component).setSize(getSliderSize());
        }
        else {
            // Otherwise if the remove exists, then remove it from the Cell
            if (component != null) {
                ((AffordanceCellComponent)component).remove();
            }
        }
    }

    /**
     * Manually set whether the resizing affordance is visible (true) or
     * not (false).
     *
     * @param visible True if the resizing affordance should be visible
     */
    public void setResizingVisible(boolean visible) {
        // Fetch the currently selected Cell. If none, then do nothing
        Cell cell = getSelectedCell();
        if (cell == null) {
            return;
        }

        // Make sure the resize toggle button is in the same state. We need
        // to check to make sure the toggle button isn't already in this
        // state to prevent generated a spurious event.
        if (resizeToggleButton.isSelected() != visible) {
            resizeToggleButton.setSelected(visible);
        }

        // See if there is already a rotate component on the Cell.
        CellComponent component = cell.getComponent(ResizeAffordanceCellComponent.class);

        // If we are selecting the resize toggle button, then add the resize
        // component if it is not already on there. Also, set its size.
        if (visible == true) {
            if (component == null) {
                component = new ResizeAffordanceCellComponent(cell);
                cell.addComponent(component);
            }
            ((AffordanceCellComponent)component).setSize(getSliderSize());
        }
        else {
            // Otherwise if the remove exists, then remove it from the Cell
            if (component != null) {
                ((AffordanceCellComponent)component).remove();
            }
        }
    }

    /**
     * Returns the currently selected cell, null if no cell is currently
     * selected.
     */
    private Cell getSelectedCell() {
        SceneManager manager = SceneManager.getSceneManager();
        List<Entity> entityList = manager.getSelectedEntities();
        if (entityList != null && entityList.size() > 0) {
            return SceneManager.getCellForEntity(entityList.get(0));
        }
        return null;
    }

    /**
     * Returns the value of the size, as a floating point between 1.0 and 5.0
     */
    private float getSliderSize() {
        return ((float) sizeSlider.getValue() / 100.0f) + 1.0f;
    }

    /**
     * Updates the size of the affordances using the current value of the size
     * slider
     */
    private void updateAffordanceSize() {
        // Fetch the current value of the slide and the currently selected
        // cell
        float newSize = getSliderSize();
        Cell cell = getSelectedCell();
        if (cell == null) {
            return;
        }

        // Set the size on the translate affordance
        TranslateAffordanceCellComponent translateComponent = cell.getComponent(TranslateAffordanceCellComponent.class);
        if (translateComponent != null) {
            translateComponent.setSize(newSize);
        }

        // Set the size on the rotate affordance
        RotateAffordanceCellComponent rotateComponent = cell.getComponent(RotateAffordanceCellComponent.class);
        if (rotateComponent != null) {
            rotateComponent.setSize(newSize);
        }

        // Set the size on the resize affordance
        ResizeAffordanceCellComponent resizeComponent = cell.getComponent(ResizeAffordanceCellComponent.class);
        if (resizeComponent != null) {
            resizeComponent.setSize(newSize);
        }
    }

    /**
     * Inner class that listens for changes to the selection and upates the
     * state of the dialog appropriately
     */
    class SelectionListener extends EventClassListener {

        public SelectionListener() {
        }
        
        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] { SelectionEvent.class };
        }

        @Override
        public void commitEvent(Event event) {
            // Update the GUI based upon the newly selected Entity and Cell. We
            // must do this in the AWT Event Thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateGUI();
                }
            });
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton detailsButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JToggleButton resizeToggleButton;
    private javax.swing.JToggleButton rotateToggleButton;
    private javax.swing.JSlider sizeSlider;
    private javax.swing.JToggleButton translateToggleButton;
    // End of variables declaration//GEN-END:variables
}
