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

/*
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
package org.jdesktop.wonderland.modules.assetmeter.client;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.AssetManager.AssetProgressListener;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AssetMeterJPanel extends javax.swing.JPanel {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/assetmeter/client/resources/Bundle");
    /**
     * A synchronized ordered map of Assets to their downloading status objects
     */
    private final Map<String, DownloadingAsset> downloadingAssetMap;

    /* The default list model */
    private final AssetIndicatorListModel listModel = new AssetIndicatorListModel();

    /* The progress listener */
    private final MeterProgressListener progressListener;

    /* The ClientPlugin */
    private final AssetMeterClientPlugin plugin;

    /* The fade timer in progress */
    private Timer dismissTimer;

    /* Whether this component is enabled */
    private boolean enabled = true;

    /** Creates new form AssetMeterJFrame */
    public AssetMeterJPanel(AssetMeterClientPlugin plugin) {
        this.plugin = plugin;

        initComponents();
        downloadingAssetMap = Collections.synchronizedMap(new LinkedHashMap());

        // Have the JList renderer the asset indicators
        loadingAssetList.setModel(listModel);
        loadingAssetList.setCellRenderer(new AssetIndicatorCellRenderer());

        // Add a listener to the asset manager
        progressListener = new MeterProgressListener();
        
        // Select the right card
        ((CardLayout) getLayout()).show(this, "combined");
    }

    /**
     * Register the progress listener with the asset manager
     */
    public void register() {
        AssetManager manager = AssetManager.getAssetManager();
        manager.addProgressListener(progressListener);
    }
    
    /**
     * Stop listener for events
     */
    public void deactivate() {
        AssetManager manager = AssetManager.getAssetManager();
        manager.removeProgressListener(progressListener);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        individualPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        loadingAssetList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        downButton = new javax.swing.JButton();
        combinedPanel = new javax.swing.JPanel();
        combinedProgressBar = new javax.swing.JProgressBar();
        combinedLabel = new javax.swing.JLabel();
        upButton = new javax.swing.JButton();

        setMaximumSize(new java.awt.Dimension(300, 300));
        setPreferredSize(new java.awt.Dimension(300, 40));
        setLayout(new java.awt.CardLayout());

        individualPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(loadingAssetList);

        individualPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setPreferredSize(new java.awt.Dimension(300, 10));

        downButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/assetmeter/client/resources/downArrow23x10.png"))); // NOI18N
        downButton.setBorder(null);
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(320, Short.MAX_VALUE)
                .add(downButton))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(downButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        individualPanel.add(jPanel1, java.awt.BorderLayout.NORTH);

        add(individualPanel, "individual");

        combinedPanel.setMaximumSize(new java.awt.Dimension(300, 400));
        combinedPanel.setMinimumSize(new java.awt.Dimension(300, 25));
        combinedPanel.setPreferredSize(new java.awt.Dimension(300, 40));

        combinedLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        combinedLabel.setText("100 files");

        upButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/assetmeter/client/resources/upArrow23x10.png"))); // NOI18N
        upButton.setBorder(null);
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout combinedPanelLayout = new org.jdesktop.layout.GroupLayout(combinedPanel);
        combinedPanel.setLayout(combinedPanelLayout);
        combinedPanelLayout.setHorizontalGroup(
            combinedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, combinedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(combinedProgressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(combinedLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(upButton)
                .addContainerGap())
        );
        combinedPanelLayout.setVerticalGroup(
            combinedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(combinedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(combinedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(combinedProgressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(combinedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(upButton)
                        .add(combinedLabel)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        add(combinedPanel, "combined");
    }// </editor-fold>//GEN-END:initComponents

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        ((CardLayout) getLayout()).show(this, "individual");
        setSize(getMaximumSize());
        invalidate();
        individualPanel.invalidate();
        plugin.resize();
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        ((CardLayout) getLayout()).show(this, "combined");
        setSize(getPreferredSize());
        invalidate();
        plugin.resize();
    }//GEN-LAST:event_downButtonActionPerformed

    private void fadeIn() {
        plugin.getHUDComponent().setVisible(true);
//        if (fadeTimer != null) {
//            fadeTimer.stop();
//        }
//
//        if (!plugin.getHUDComponent().isVisible()) {
//           plugin.getHUDComponent().setVisible(true);
//        }
//
//        transparency = 0;
//        if (plugin.getHUDComponent().getTransparency() != 0) {
//            plugin.getHUDComponent().setTransparency(0f);
//        }
    }

    private void fadeOut() {
        // wait 10 seconds before dismissing meter to prevent meter from
        // popping in and out of view
        if (dismissTimer == null) {
            dismissTimer = new Timer(10000, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (downloadingAssetMap.size() == 0) {
                        plugin.getHUDComponent().setVisible(false);
                        dismissTimer = null;
                    } else {
                        dismissTimer.restart();
                    }
                }
            });
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        }
//        if (fadeTimer != null && fadeTimer.isRunning()) {
//            fadeTimer.stop();
//        }
//
//        // see if we are already faded out
//        if (transparency == 100) {
//            return;
//        }
//
//        fadeTimer = new Timer(100, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (transparency < 100) {
//                    transparency += 2;
//                    plugin.getHUDComponent().setTransparency(transparency / 100f);
//                } else {
//                    transparency = 100;
//                    ((Timer) e.getSource()).stop();
//
//                    if (plugin.getHUDComponent().isVisible()) {
//                        plugin.getHUDComponent().setVisible(false);
//                    }
//                }
//            }
//        });
//        fadeTimer.start();
    }

    void setUpdateEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled && downloadingAssetMap.size() > 0) {
            fadeIn();
        } else if (!enabled) {
            fadeOut();
        }
    }

    /**
     * Updates the given downloading asset
     */
    private void updateDownloadingAsset(
            Asset asset, final int readBytes, final int percent) {
        // Update the values in the downloading asset indicator using a swing
        // worker for thread safeness.
        final Asset[] assetArray = new Asset[]{asset};
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                DownloadingAsset da = downloadingAssetMap.get(
                        assetArray[0].getAssetURI().toExternalForm());
                if (da != null) {
                    da.readBytes = readBytes;
                    da.percentage = percent;
                    da.indicator.setAssetProgressValue(da.percentage);
                    listModel.updateElement(da.indicator);
                }
            }
        });

        updateCombined();
    }

    /**
     * Adds a new asset to the list. This updates the UI as necessary
     */
    private void addDownloadingAsset(final Asset asset) {
        // Adds a downloading asset to the list using a swing worker for thread
        // safeness
        final DownloadingAsset da = new DownloadingAsset();
        da.asset = asset;
        da.readBytes = 0;
        da.percentage = 0;
        final String assetURIString = asset.getAssetURI().toExternalForm();
        downloadingAssetMap.put(assetURIString, da);

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // set up the indicator in the AWT event thread
                da.indicator = new AssetIndicator();
                da.indicator.setAssetProgressValue(0);
                da.indicator.setAssetLabel(assetURIString);

                listModel.addElementToTail(da.indicator);
            }
        });

        updateCombined();
    }

    /**
     * Removes a downloading asset from the list and updates the UI as
     * necessary.
     */
    private void removeDownloadingAsset(Asset asset) {
        // Removes a downloading asset from the list using a swing worker for
        // thread safeness
        final DownloadingAsset da = downloadingAssetMap.remove(
                asset.getAssetURI().toExternalForm());

        new Timer(750, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                listModel.removeElement(da.indicator);
                ((Timer) e.getSource()).stop();
            }
        }).start();

        updateCombined();
    }

    /**
     * Update the global statistics
     */
    private void updateCombined() {
        int count = 0;
        int totalPercent = 0;

        synchronized (downloadingAssetMap) {
            for (DownloadingAsset da : downloadingAssetMap.values()) {
                count++;
                totalPercent += da.percentage;
            }
        }

        if (count == 0) {
            fadeOut();
        } else if (enabled) {
            fadeIn();
        }

        final int fCount = count;
        final int fPercent;
        if (fCount == 0) {
            fPercent = 0;
        } else {
            fPercent = (int) ((double) totalPercent / (double) count);
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                String text = BUNDLE.getString("Files");
                text = MessageFormat.format(text, fCount);
                combinedLabel.setText(text);
                combinedProgressBar.setValue(fPercent);
            }
        });
    }

    /**
     * A class that represents an asset currently being downloaded and its
     * status
     */
    class DownloadingAsset {

        public Asset asset = null;
        public int readBytes = 0;
        public int percentage = -1;
        private AssetIndicator indicator = null; // initialize later in AWT
        // event thread
    }

    /**
     * A progress listener for assets being downloaded by the Asset Manager
     */
    class MeterProgressListener implements AssetProgressListener {

        public void downloadProgress(
                Asset asset, int readBytes, int percentage) {
            // Check to see if the asset is already in the list, otherwise
            // add it. We need to synchronized according to the asset to make
            // sure we do not add it twice
            synchronized (this) {
                if (downloadingAssetMap.containsKey(
                        asset.getAssetURI().toExternalForm()) == false) {
                    addDownloadingAsset(asset);
                } else {
                    updateDownloadingAsset(asset, readBytes, percentage);
                }
            }
        }

        public void downloadFailed(Asset asset) {
            // Simply try to remove the asset from the list, synchronized
            // on the asset
            synchronized (this) {
                removeDownloadingAsset(asset);
            }
        }

        public void downloadCompleted(Asset asset) {
            // Simply try to remove the asset from the list, synchrnoized
            // on the asset
            synchronized (this) {
                removeDownloadingAsset(asset);
            }
        }
    }

    /**
     * The list model to hold all of the asset indicators
     */
    class AssetIndicatorListModel extends AbstractListModel {

        /* An ordered list of downloading indicators */
        private List<AssetIndicator> indicatorList = new LinkedList();

        public int getSize() {
            return indicatorList.size();
        }

        public Object getElementAt(int index) {
            return indicatorList.get(index);
        }

        public synchronized void addElementToTail(AssetIndicator ai) {
            indicatorList.add(ai);
            int index = indicatorList.size() - 1;
            fireIntervalAdded(ai, index, index);
        }

        public synchronized void removeElement(AssetIndicator ai) {
            int index = indicatorList.indexOf(ai);
            if (index != -1) {
                indicatorList.remove(index);
                fireIntervalRemoved(this, index, index);
            }
        }

        public synchronized void updateElement(AssetIndicator ai) {
            int index = indicatorList.indexOf(ai);
            if (index != -1) {
                fireContentsChanged(this, index, index);
            }
        }
    }

    /**
     * A list cell rendered for the Asset Indicator
     */
    class AssetIndicatorCellRenderer implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            return ((AssetIndicator) value);
        }
    }

    /**
     * A JPanel that holds a label and a progress bar.
     */
    class AssetIndicator extends JPanel {

        private JProgressBar progressBar;
        private JLabel assetLabel;

        /**
         * Constructor, assembles the components of the panel
         */
        public AssetIndicator() {
            super();

            // Make the layout a vertical box layout
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(4, 10, 2, 10));
            setAlignmentX(0.0f);
            setAlignmentY(0.0f);

            // Create the label, make the font a bit smaller and add it to
            // the panel
            assetLabel = new JLabel("");
            assetLabel.setAlignmentX(0.0f);
            assetLabel.setAlignmentY(0.0f);
            Font font = assetLabel.getFont();
            assetLabel.setFont(font.deriveFont(Font.PLAIN, 11));
//            assetLabel.setPreferredSize(getSize());
            add(assetLabel);

            // Create the progress bar and add it to the panel. Make sure it
            // is always of a maximum height
            progressBar = new JProgressBar();
//            progressBar.addComponentListener(new ComponentAdapter() {
//                @Override
//                public void componentResized(ComponentEvent e) {
//                    Dimension size = progressBar.getSize();
//                    size.setSize(size.getWidth(), 15);
//                    progressBar.setSize(size);
//                }
//
//            });
            progressBar.setAlignmentX(0.0f);
            progressBar.setAlignmentY(0.0f);
            progressBar.setValue(0);
//            progressBar.setPreferredSize(getSize());
            add(progressBar);
        }

        /**
         * Sets the value of the asset label.
         */
        public void setAssetLabel(String label) {
            assetLabel.setText(label);
        }

        /**
         * Sets the value of the progress bar.
         */
        public void setAssetProgressValue(int value) {
            progressBar.setValue(value);
        }

        /**
         * Increments the value of the progress bar by one
         */
        public void incrementAssetProgressValue() {
            progressBar.setValue(progressBar.getValue() + 1);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel combinedLabel;
    private javax.swing.JPanel combinedPanel;
    private javax.swing.JProgressBar combinedProgressBar;
    private javax.swing.JButton downButton;
    private javax.swing.JPanel individualPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList loadingAssetList;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
}
