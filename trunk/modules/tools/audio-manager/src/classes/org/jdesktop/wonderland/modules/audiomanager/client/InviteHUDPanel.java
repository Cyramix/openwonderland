/*
 * InviteHUDPanel.java
 *
 * Created on July 10, 2009, 10:51 AM
 */

package org.jdesktop.wonderland.modules.audiomanager.client;

import java.util.ArrayList;

import java.awt.Point;

import javax.swing.DefaultListModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.client.comms.WonderlandSession;

import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent.ComponentEventType;
import org.jdesktop.wonderland.client.hud.HUDComponentListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

/**
 *
 * @author  jp
 */
public class InviteHUDPanel extends javax.swing.JPanel implements PresenceManagerListener,
	MemberChangeListener, DisconnectListener {

    AudioManagerClient client;
    WonderlandSession session;

    private PresenceManager pm;
    private PresenceInfo myPresenceInfo;
    private ArrayList<PresenceInfo> usersToInvite;

    private DefaultListModel userListModel;

    private PropertyChangeSupport listeners;

    private HUDComponent inviteHUDComponent;

    /** Creates new form InviteHUDPanel */
    public InviteHUDPanel() {
        initComponents();
    }

    public InviteHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, ArrayList<PresenceInfo> usersToInvite) {

	this.client = client;
	this.session = session;
	this.myPresenceInfo = myPresenceInfo;
	this.usersToInvite = usersToInvite;

        initComponents();

        userListModel = new DefaultListModel();
        userList.setModel(userListModel);

	userListModel.addElement(myPresenceInfo.usernameAlias);

	for (PresenceInfo info : usersToInvite) {
	    userListModel.addElement(info.usernameAlias);
	}

	pm = PresenceManagerFactory.getPresenceManager(session);

	setVisible(true);
    }

    public void setHUDComponent(HUDComponent inviteHUDComponent) {
	this.inviteHUDComponent = inviteHUDComponent;
    }

    /**
     * Adds a bound property listener to the dialog
     * @param listener a listener for dialog events
     */
    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a bound property listener from the dialog
     * @param listener the listener to remove
     */
    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners != null) {
            listeners.removePropertyChangeListener(listener);
        }
    }

    private void setUserList() {
        PresenceInfo[] presenceInfoList = pm.getAllUsers();

        ArrayList<String> userList = new ArrayList();

        for (int i = 0; i < presenceInfoList.length; i++) {
            PresenceInfo info = presenceInfoList[i];

	    if (info.equals(myPresenceInfo)) {
		continue;
	    }

            if (info.callID == null) {
                // It's a virtual player, skip it.
                continue;
            }

	    synchronized (userListModel) {
                userListModel.addElement(info.usernameAlias);
	    }
        }
    }

    public void presenceInfoChanged(PresenceInfo presenceInfo, ChangeType type) {
        setUserList();
    }

    public void disconnected() {
        setVisible(false);
    }

    public void memberChange(PresenceInfo member, boolean added) {
    }

    public void setMemberList(PresenceInfo[] members) {
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
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        secretRadioButton = new javax.swing.JRadioButton();
        privateRadioButton = new javax.swing.JRadioButton();
        cancelButton = new javax.swing.JButton();
        InviteButton = new javax.swing.JButton();

        jLabel1.setText("Invite");

        userList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        userList.setEnabled(false);
        userList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                userListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(userList);

        jLabel2.setText("Privacy:");

        buttonGroup1.add(secretRadioButton);
        secretRadioButton.setText("Secret");

        buttonGroup1.add(privateRadioButton);
        privateRadioButton.setSelected(true);
        privateRadioButton.setText("Private");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        InviteButton.setText("Invite");
        InviteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InviteButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(58, 58, 58)
                        .add(jScrollPane1))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(1, 1, 1)
                                .add(secretRadioButton))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(58, 58, 58)
                                .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(privateRadioButton)
                                .add(6, 6, 6))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(InviteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(53, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(124, Short.MAX_VALUE)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 62, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(87, 87, 87))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(37, 37, 37)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(privateRadioButton)
                    .add(jLabel2)
                    .add(secretRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(InviteButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void userListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userListValueChanged
// TODO add your handling code here:
}//GEN-LAST:event_userListValueChanged

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    if (listeners == null) {
	inviteHUDComponent.setVisible(false);
	return;
    }

    listeners.firePropertyChange("cancel", new String(""), null);
}//GEN-LAST:event_cancelButtonActionPerformed

private void InviteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InviteButtonActionPerformed
    InCallHUDPanel inCallHUDPanel = new InCallHUDPanel(client, session, myPresenceInfo, usersToInvite, 
	secretRadioButton.isSelected());

    HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
    HUDComponent inCallHUDComponent = mainHUD.createComponent(inCallHUDPanel);

    inCallHUDPanel.setHUDComponent(inCallHUDComponent);

    //System.out.println("I x,y " + inviteHUDComponent.getX() + ", " + inviteHUDComponent.getY()
    //    + " width " + inviteHUDComponent.getWidth() + " height " + inviteHUDComponent.getHeight()
    //    + " InCall x,y " + (inviteHUDComponent.getX() + inviteHUDComponent.getWidth())
    //    + ", " + (inviteHUDComponent.getY() + inviteHUDComponent.getHeight() - inCallHUDComponent.getHeight()));

    mainHUD.addComponent(inCallHUDComponent);
    inCallHUDComponent.addComponentListener(new HUDComponentListener() {
        public void HUDComponentChanged(HUDComponentEvent e) {
            if (e.getEventType().equals(ComponentEventType.DISAPPEARED)) {
            }
        }
    });

    inCallHUDComponent.setVisible(true);
    inCallHUDComponent.setLocation(inviteHUDComponent.getX() + inviteHUDComponent.getWidth(), 
	inviteHUDComponent.getY() + inviteHUDComponent.getHeight() - inCallHUDComponent.getHeight());
}//GEN-LAST:event_InviteButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton InviteButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JRadioButton secretRadioButton;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables

}
