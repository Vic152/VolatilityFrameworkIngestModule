/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.myproject.vf;
      
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
 
// import org.python.util.PythonInterpreter

public class VfProcess {

    public static void runVf() throws IOException{
        
       /*
        --profile=LinuxEvo4GARM -f C:\vf\image\Evo4GRodeo.lime linux_pslist

        */
        String volPath = "C:\\vf\\volatility-2.3.1\\vol.py";
        String volProfile = "--profile=LinuxEvo4GARM";
        String volFile = "--filename=C:\\vf\\image\\Evo4GRodeo.lime";
        String volPlugin = "linux_pstree";
        
           //create the date
        Date date = new Date();
        //format  
        SimpleDateFormat format = 
        new SimpleDateFormat (" E yyyy-MM-dd 'at' hh-mm-ss a zzz");
        //apply format
        String dateString = format.format(date);
          
        

//create new ProcessBuilder object
ProcessBuilder pb = new ProcessBuilder("python", volPath, volProfile, volFile, volPlugin);

 
 pb.directory(new File("C:\\vf\\volatility-2.3.1\\"));
 
 
 
File log = new File("C:\\" + volPlugin + dateString + ".txt");

pb.redirectErrorStream(true);
pb.redirectOutput(Redirect.appendTo(log));
 
 
 Process p = pb.start();
 
 System.out.println("Output "+log);
 
 
}


public static void getVol(){
    
    Paths.get("volatility-2.3.1", "vol.py");
    
    

}



}//end of class
 













 /*scrap book*/
    
 /* jython version that doesnt work
    public static void runPython(){
        
       PythonInterpreter python = new PythonInterpreter();
       
       python.execfile("C:\\vf\\volatility-2.3.1\\vol.py");
        
        
    }
}
    */ 