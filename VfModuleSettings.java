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
    private boolean isAndroid = false;
    private boolean isLinux = false;
    private boolean isWindows = false;
    private boolean isMac = false;
    private String opSystem;
    

    public VfModuleSettings() {
    }
    
    public VfModuleSettings(boolean isAndroid, boolean isLinux, boolean isWindows, boolean isMac) {
        this.isWindows = isWindows;
        this.isMac = isMac;
        this.isAndroid = isAndroid;
        this.isLinux = isLinux;
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

    void setIsAndroid(boolean enabled) {
        isAndroid = enabled;
    }

    boolean isAndroid() {
        return isAndroid;
    }
    
    void setIsLinux(boolean enabled) {
        isLinux = enabled;
    }

    boolean isLinux() {
        return isLinux;
    }
    
    void setIsWindows(boolean enabled) {
        isWindows = enabled;
    }

    boolean isWindows() {
        return isWindows;
    }
    
    void setIsMac(boolean enabled) {
        isMac = enabled;
    }

    boolean isMac() {
        return isMac;
    }

}
