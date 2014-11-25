/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myproject.vf;

import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;

/**
 *
 * @author stone
 */
public class VfModuleSettings implements IngestModuleIngestJobSettings {

    private static final long serialVersionUID = 1L;
    private String opSystem;
    private String volProfile;
    

    public VfModuleSettings() {
    } 
    
   
    @Override
    public long getVersionNumber() {
        return serialVersionUID;
    }
    
    void setOpSystem(String os){
        
        opSystem = os;
    }
    
    String opSystem(){
        return opSystem;
    }

    
    void setVolProfile(String vp){
        
        volProfile = vp;
    }
    
    String volProfile(){
        return volProfile;
    }
   
}
