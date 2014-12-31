/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myproject.vf;

import javax.swing.JOptionPane;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;

/**
 *
 * @author stone
 */
public class VfModuleSettingsPanel extends IngestModuleIngestJobSettingsPanel {

    private final VfModuleSettings settings;

    VfModuleSettingsPanel(VfModuleSettings settings) {
        this.settings = settings;
        initComponents();
        customizeComponents();
    }

    private void customizeComponents() {

    }

    @Override
    public IngestModuleIngestJobSettings getSettings() {

        return settings;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        selectOs = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        configurePlugins = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(VfModuleSettingsPanel.class, "VfModuleSettingsPanel.jPanel1.border.title_1"))); // NOI18N

        selectOs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Android", "Linux", "Windows", "MacOS" }));
        selectOs.setBorder(null);
        selectOs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectOsActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(VfModuleSettingsPanel.class, "VfModuleSettingsPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(configurePlugins, org.openide.util.NbBundle.getMessage(VfModuleSettingsPanel.class, "VfModuleSettingsPanel.configurePlugins.text")); // NOI18N
        configurePlugins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configurePluginsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(selectOs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(configurePlugins)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(12, 12, 12)
                .addComponent(selectOs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(configurePlugins)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectOsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectOsActionPerformed
        settings.setOpSystem(selectOs.getSelectedItem().toString());
    }//GEN-LAST:event_selectOsActionPerformed

    private void configurePluginsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configurePluginsActionPerformed

        //initial state operating system may not be selected
        if (settings.opSystem() == null) {

            JOptionPane.showMessageDialog(null, "No operating sytem selected for analysis."
                    + "\nOperating system must be selected to initialize valid plugins");
        } //open window with Android plugins
        else if ("Android".equals(settings.opSystem())) {

            AndroidPluginSetup aps = new AndroidPluginSetup();

            aps.setVisible(true);
            
            //Set Volatility Framework profile
            settings.setVolProfile(aps.volProfile);

            //Set Plugins
            settings.setVolPlugins(aps.plugins);
            //System.out.println("SETTINGS PANEL "+aps.plugins.toString());
    }//GEN-LAST:event_configurePluginsActionPerformed
            //open window for Windows plugins
        else if ("Windows".equals(settings.opSystem())) {
            
            WindowsPluginSetup wps = new WindowsPluginSetup();
            
            wps.setVisible(true);
           
                
        }
        //open window for Linux plugins
        else if ("Linux".equals(settings.opSystem())) {
            
            LinuxPluginSetup lps = new LinuxPluginSetup();
            
            lps.setVisible(true);
           
            
        }
        //open window for MacOS plugins
        else if ("MacOS".equals(settings.opSystem())) {
            
            MacPluginSetup mps = new MacPluginSetup();
            
            mps.setVisible(true);
           
            
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton configurePlugins;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JComboBox selectOs;
    // End of variables declaration//GEN-END:variables

}
