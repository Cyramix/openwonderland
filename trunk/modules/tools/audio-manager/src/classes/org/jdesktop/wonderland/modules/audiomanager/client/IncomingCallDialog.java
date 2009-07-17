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
package org.jdesktop.wonderland.modules.audiomanager.client;

import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent.ComponentEventType;
import org.jdesktop.wonderland.client.hud.HUDComponentListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatBusyMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinAcceptedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.awt.Point;

/**
 *
 * @author  jp
 */
public class IncomingCallDialog extends javax.swing.JFrame {

    private static final Logger logger =
            Logger.getLogger(IncomingCallDialog.class.getName());
    private ChatType chatType = ChatType.PRIVATE;
    private AudioManagerClient client;
    private WonderlandSession session;
    private CellID cellID;
    private String group;
    private PresenceInfo caller;
    private PresenceInfo myPresenceInfo;

    /** Creates new form IncomingCallDialog */
    public IncomingCallDialog() {
        initComponents();
    }

    public IncomingCallDialog(AudioManagerClient client, WonderlandSession session,
            CellID cellID, VoiceChatJoinRequestMessage message) {

        this.client = client;
        this.cellID = cellID;
        this.session = session;

        initComponents();

        group = message.getGroup();

        caller = message.getCaller();

        callerText.setText(caller.usernameAlias);

        PresenceManager pm = PresenceManagerFactory.getPresenceManager(session);

        myPresenceInfo = pm.getPresenceInfo(cellID);

	System.out.println("NEW INCOMING FOR " + myPresenceInfo);

        setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        titleLabel = new javax.swing.JLabel();
        callerText = new javax.swing.JLabel();
        privacyLabel = new javax.swing.JLabel();
        secretRadioButton = new javax.swing.JRadioButton();
        privateRadioButton = new javax.swing.JRadioButton();
        publicRadioButton = new javax.swing.JRadioButton();
        answerButton = new javax.swing.JButton();
        ignoreButton = new javax.swing.JButton();
        busyButton = new javax.swing.JButton();

        titleLabel.setFont(titleLabel.getFont().deriveFont(titleLabel.getFont().getStyle() | java.awt.Font.BOLD));
        titleLabel.setText("Incoming call from:");

        callerText.setText("                           ");

        privacyLabel.setFont(privacyLabel.getFont());
        privacyLabel.setText("Privacy:");

        buttonGroup1.add(secretRadioButton);
        secretRadioButton.setFont(secretRadioButton.getFont());
        secretRadioButton.setText("Secret");
        secretRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secretRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(privateRadioButton);
        privateRadioButton.setFont(privateRadioButton.getFont());
        privateRadioButton.setSelected(true);
        privateRadioButton.setText("Private");
        privateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(publicRadioButton);
        publicRadioButton.setFont(publicRadioButton.getFont());
        publicRadioButton.setText("Public");
        publicRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publicRadioButtonActionPerformed(evt);
            }
        });

        answerButton.setText("Answer");
        answerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                answerButtonActionPerformed(evt);
            }
        });

        ignoreButton.setText("Ignore");
        ignoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreButtonActionPerformed(evt);
            }
        });

        busyButton.setText("Busy");
        busyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busyButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(privacyLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(secretRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(privateRadioButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(publicRadioButton))
                            .add(layout.createSequentialGroup()
                                .add(titleLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(callerText, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(45, 45, 45)
                        .add(ignoreButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(busyButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(answerButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(titleLabel)
                    .add(callerText))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(privacyLabel)
                    .add(secretRadioButton)
                    .add(privateRadioButton)
                    .add(publicRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ignoreButton)
                    .add(busyButton)
                    .add(answerButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private HUDComponent inCallHUDComponent;

private void answerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_answerButtonActionPerformed
    logger.info("Sent join message");

    InCallHUDPanel inCallHUDPanel = InCallHUDPanel.getInCallHUDPanel(group);

    if (inCallHUDPanel == null) {
        inCallHUDPanel = new InCallHUDPanel(client, session, myPresenceInfo, caller, group);

        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        inCallHUDComponent = mainHUD.createComponent(inCallHUDPanel);

	inCallHUDPanel.setHUDComponent(inCallHUDComponent);

        inCallHUDComponent.setPreferredLocation(Layout.NORTHWEST);

        mainHUD.addComponent(inCallHUDComponent);

        inCallHUDComponent.addComponentListener(new HUDComponentListener() {
            public void HUDComponentChanged(HUDComponentEvent e) {
                if (e.getEventType().equals(ComponentEventType.DISAPPEARED)) {
                }
            }
        });
    }

    inCallHUDComponent.setVisible(true);

    session.send(client, new VoiceChatJoinAcceptedMessage(group, myPresenceInfo, chatType));
    setVisible(false);
}//GEN-LAST:event_answerButtonActionPerformed

private void busyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busyButtonActionPerformed
    session.send(client, new VoiceChatBusyMessage(group, caller, myPresenceInfo, chatType));
    setVisible(false);
}//GEN-LAST:event_busyButtonActionPerformed

private void ignoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreButtonActionPerformed
    setVisible(false);
}//GEN-LAST:event_ignoreButtonActionPerformed

private void publicRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publicRadioButtonActionPerformed
    chatType = ChatType.PUBLIC;
}//GEN-LAST:event_publicRadioButtonActionPerformed

private void secretRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secretRadioButtonActionPerformed
    chatType = ChatType.SECRET;
}//GEN-LAST:event_secretRadioButtonActionPerformed

private void privateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateRadioButtonActionPerformed
    chatType = ChatType.PRIVATE;
}//GEN-LAST:event_privateRadioButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new IncomingCallDialog().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton answerButton;
    private javax.swing.JButton busyButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel callerText;
    private javax.swing.JButton ignoreButton;
    private javax.swing.JLabel privacyLabel;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JRadioButton publicRadioButton;
    private javax.swing.JRadioButton secretRadioButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
