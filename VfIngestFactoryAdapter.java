/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myproject.vf;

import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.ingest.IngestModuleFactory;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleFactoryAdapter;
import org.sleuthkit.autopsy.ingest.IngestModuleGlobalSettingsPanel;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;

@ServiceProvider(service = IngestModuleFactory.class)

public class VfIngestFactoryAdapter extends IngestModuleFactoryAdapter {

    static String getModuleName() {
        return NbBundle.getMessage(VfIngestModule.class, "VfIngestModule.moduleName");
    }

    @Override
    public String getModuleDisplayName() {

        return getModuleName();
    }

    @Override
    public String getModuleDescription() {
        return NbBundle.getMessage(VfIngestModule.class, "VfIngestModule.moduleDescription");
    }

    @Override
    public String getModuleVersionNumber() {

        String moduleVersion = "0.0.1";
        return moduleVersion;
    }

    @Override
    public IngestModuleIngestJobSettings getDefaultIngestJobSettings() {
        return new VfModuleSettings();
    }

    @Override
    public boolean hasIngestJobSettingsPanel() {
        return true;
    }

    @Override
    public IngestModuleIngestJobSettingsPanel getIngestJobSettingsPanel(IngestModuleIngestJobSettings settings) {
        assert settings instanceof VfModuleSettings;
        if (!(settings instanceof VfModuleSettings)) {
            throw new IllegalArgumentException(NbBundle.getMessage(this.getClass(),
                    "VfModuleFactory.getIngestJobSettingsPanel.exception.msg"));
        }
        return new VfModuleSettingsPanel((VfModuleSettings) settings);
    }

    /*@Override
    public boolean hasGlobalSettingsPanel() {
    return true;
    }*/

    /*@Override
    public IngestModuleGlobalSettingsPanel getGlobalSettingsPanel() {
    VfGlobalSettingsPanel globalOptionsPanel = new VfGlobalSettingsPanel();
    
    return globalOptionsPanel;
    }*/

    @Override
    public boolean isFileIngestModuleFactory() {
        return true;
    }

    @Override
    public FileIngestModule createFileIngestModule(IngestModuleIngestJobSettings settings) {
        assert settings instanceof VfModuleSettings;
        if (!(settings instanceof VfModuleSettings)) {
            throw new IllegalArgumentException(
                    NbBundle.getMessage(this.getClass(), "VfModuleFactory.createFileIngestModule.exception.msg"));
        }

        return new VfIngestModule((VfModuleSettings) settings);

    }

}
