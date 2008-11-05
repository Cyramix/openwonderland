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
package org.jdesktop.wonderland.modules.artimport.client.jme;

import java.io.File;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author paulby
 */
public class ModuleManagerUI extends javax.swing.JFrame {

    private File parentDir = null;
    private JFileChooser fc = new JFileChooser();

    /** Creates new form ModuleManagerUI */
    public ModuleManagerUI() {
        initComponents();

        parentDirButtonActionPerformed(null);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parentDirGroup = new javax.swing.ButtonGroup();
        createModulePanel = new javax.swing.JPanel();
        createModuleB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        moduleNameTF = new javax.swing.JTextField();
        parentDirTF = new javax.swing.JTextField();
        chooseDirB = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        commonPackageCB = new javax.swing.JCheckBox();
        serverPackageCB = new javax.swing.JCheckBox();
        clientPackageCB = new javax.swing.JCheckBox();
        artCB = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        toolsDirCB = new javax.swing.JCheckBox();
        worldDirCB = new javax.swing.JCheckBox();
        foundationDirCB = new javax.swing.JCheckBox();
        samplesDirCB = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Module Manager");

        createModulePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Create New Module"));

        createModuleB.setText("Create Module");
        createModuleB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createModuleBActionPerformed(evt);
            }
        });

        jLabel1.setText("Module Name :");

        jLabel2.setText("Parent Directory :");

        moduleNameTF.setText("example");

        parentDirTF.setEnabled(false);
        parentDirTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentDirTFActionPerformed(evt);
            }
        });

        chooseDirB.setText("Choose...");
        chooseDirB.setEnabled(false);
        chooseDirB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseDirBActionPerformed(evt);
            }
        });

        commonPackageCB.setSelected(true);
        commonPackageCB.setText("Common Package");
        commonPackageCB.setEnabled(false);

        serverPackageCB.setSelected(true);
        serverPackageCB.setText("Server Package");
        serverPackageCB.setEnabled(false);

        clientPackageCB.setSelected(true);
        clientPackageCB.setText("Client Package");
        clientPackageCB.setEnabled(false);

        artCB.setText("Include Art");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(artCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 35, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(commonPackageCB)
                    .add(clientPackageCB)
                    .add(serverPackageCB))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(17, 17, 17)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(clientPackageCB)
                    .add(artCB))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(commonPackageCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(serverPackageCB)
                .add(188, 188, 188))
        );

        parentDirGroup.add(toolsDirCB);
        toolsDirCB.setText("Tools");
        toolsDirCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentDirButtonActionPerformed(evt);
            }
        });

        parentDirGroup.add(worldDirCB);
        worldDirCB.setSelected(true);
        worldDirCB.setText("World");
        worldDirCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentDirButtonActionPerformed(evt);
            }
        });

        parentDirGroup.add(foundationDirCB);
        foundationDirCB.setText("Foundation");
        foundationDirCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentDirButtonActionPerformed(evt);
            }
        });

        parentDirGroup.add(samplesDirCB);
        samplesDirCB.setText("Samples");
        samplesDirCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parentDirButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(worldDirCB)
                    .add(foundationDirCB)
                    .add(toolsDirCB)
                    .add(samplesDirCB))
                .addContainerGap(322, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(toolsDirCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(worldDirCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(foundationDirCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(samplesDirCB)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout createModulePanelLayout = new org.jdesktop.layout.GroupLayout(createModulePanel);
        createModulePanel.setLayout(createModulePanelLayout);
        createModulePanelLayout.setHorizontalGroup(
            createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createModulePanelLayout.createSequentialGroup()
                .add(createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createModulePanelLayout.createSequentialGroup()
                        .add(81, 81, 81)
                        .add(createModuleB))
                    .add(createModulePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(moduleNameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 135, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(createModulePanelLayout.createSequentialGroup()
                                .add(parentDirTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 277, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(45, 45, 45)
                                .add(chooseDirB))
                            .add(createModulePanelLayout.createSequentialGroup()
                                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(254, 254, 254)))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        createModulePanelLayout.setVerticalGroup(
            createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createModulePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(moduleNameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createModulePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(chooseDirB)
                    .add(parentDirTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(44, 44, 44)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createModuleB)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(createModulePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 585, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(createModulePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 484, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createModuleBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createModuleBActionPerformed
        ModuleSourceManager mgr = new ModuleSourceManager();

        mgr.createModule(moduleNameTF.getText(), parentDir, artCB.isSelected());
    }//GEN-LAST:event_createModuleBActionPerformed

    private void chooseDirBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseDirBActionPerformed
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            parentDir = fc.getSelectedFile();
            parentDirTF.setText(parentDir.getAbsolutePath());
        }

}//GEN-LAST:event_chooseDirBActionPerformed

    private void parentDirTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parentDirTFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_parentDirTFActionPerformed

    private void parentDirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parentDirButtonActionPerformed

        String dir;

        if (toolsDirCB.isSelected())
            dir = "tools";
        else if (worldDirCB.isSelected())
            dir = "world";
        else if (foundationDirCB.isSelected())
            dir = "foundation";
        else if (samplesDirCB.isSelected())
            dir = "samples";
        else {
            Logger.getAnonymousLogger().severe("Unknown directory selection, assuming world");
            dir = "world";
        }

        File defaultDir=new File(".."+File.separatorChar+"modules"+File.separatorChar+dir);
        if (defaultDir.exists()) {
            fc.setCurrentDirectory(defaultDir);
            parentDirTF.setText(defaultDir.getAbsolutePath());
            parentDir = defaultDir;
        }

    }//GEN-LAST:event_parentDirButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ModuleManagerUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox artCB;
    private javax.swing.JButton chooseDirB;
    private javax.swing.JCheckBox clientPackageCB;
    private javax.swing.JCheckBox commonPackageCB;
    private javax.swing.JButton createModuleB;
    private javax.swing.JPanel createModulePanel;
    private javax.swing.JCheckBox foundationDirCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField moduleNameTF;
    private javax.swing.ButtonGroup parentDirGroup;
    private javax.swing.JTextField parentDirTF;
    private javax.swing.JCheckBox samplesDirCB;
    private javax.swing.JCheckBox serverPackageCB;
    private javax.swing.JCheckBox toolsDirCB;
    private javax.swing.JCheckBox worldDirCB;
    // End of variables declaration//GEN-END:variables

}
