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
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
 @ServiceProvider(service = IngestModuleFactory.class)
    
public class VfIngestFatoryAdapter extends org.sleuthkit.autopsy.ingest.IngestModuleFactoryAdapter{
   
     
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
public boolean isFileIngestModuleFactory() {
 return true;
}

@Override
public FileIngestModule createFileIngestModule(IngestModuleIngestJobSettings ingestOptions) {
    
 return new VfIngestModule();
}
    
}
