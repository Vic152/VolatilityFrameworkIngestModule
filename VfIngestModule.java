package org.myproject.vf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestJobContext;
import org.sleuthkit.autopsy.ingest.IngestModule;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.autopsy.coreutils.PlatformUtil;
import org.sleuthkit.autopsy.ingest.IngestModuleReferenceCounter;

public class VfIngestModule implements FileIngestModule {

    //declare variables
    private IngestJobContext context = null;
    private static final String VOLATILITY_DIRECTORY = "volatility-2.4";
    private static final String VOLATILITY_EXECUTABLE = "vol.py";
    private File executableFile;
    private Path rootOutputDirPath;
    private static final Logger logger = Logger.getLogger(VfIngestModule.class.getName());
    private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();

    @Override
    public void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;

        //locate vol.py in releas folder
        Path execName = Paths.get(VOLATILITY_DIRECTORY, VOLATILITY_EXECUTABLE);
        executableFile = locateExecutable(execName.toString());

        this.rootOutputDirPath = VfIngestModule.createModuleOutputDirectoryForCase();

        //testing line
        //System.out.print("\nROOT OUTPUT DIR PATH " + this.rootOutputDirPath.toString());
    }

    @Override
    public ProcessResult process(AbstractFile af) {

        // run module only on *.lime files
        if (!"lime".equals(af.getNameExtension())) {
            //test line to check extension
            // System.out.println( af.getNameExtension()+ " FILE EXTENSION ");
            return IngestModule.ProcessResult.OK;
        }

        // Verify initialization succeeded.
        if (null == this.executableFile) {
            logger.log(Level.SEVERE, "Volatility Framework called after failed start up");  // NON-NLS
            return IngestModule.ProcessResult.ERROR;
        }

        //create the date for output file
        Date date = new Date();
        //format  
        SimpleDateFormat format = new SimpleDateFormat(" yyyy-MM-dd kk-mm-ss");
        //apply format
        String dateString = format.format(date);

        //String volPath = "C:\\Documents and Settings\\Vic\\My Documents
        //                    \\NetBeansProjects\\VF\\build\\cluster\\volatility-2.4\\vol.py";
        // variables for VF
        String volProfile = "--profile=LinuxEvo4GARM";
        String volFile = "--filename=";
        String pathToFile = af.getLocalPath();
        String volPlugin = "linux_pstree";

        /*
        create new ProcessBuilder object
        
        doule quoutes very important for running Python from
        command line in folders with white spces
        "\"" +executableFile + "\""
        
        tets: volFilename+"\""+imageLocation+"\""
        "\"" +executableFile+ "\""
       
        also: volFile+pathToFile may require "\"" volFile+pathToFile "\"" but it works
        with my compilation every time.
        
        This command: python "C:\Documents and Settings\Vic\My Documents\NetBeansProjects\VF\build\cluster\volatility-2.4\vol.py" --profile=LinuxEvo4GARM --filename=C:\Documents and Settings\Vic\My Documents\Evo4GRodeo.lime linux_pstree
        Will not wotk from command line but it works from ProcessBuilder
        Theoreticaly this command should have double quotes around pathToFile - image file
        
         */
        ProcessBuilder pb = new ProcessBuilder("python", "\"" + executableFile + "\"",
                volProfile, volFile + pathToFile, volPlugin);

        //create image name and name without extension
        String imageName = af.getName();
        String imageNameWOExt = FilenameUtils.removeExtension(imageName);

        // write result to .txt file in the 
        File result = new File(this.rootOutputDirPath.toString(), imageNameWOExt.concat(" ") + volPlugin + dateString + ".txt");

        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(result));

        //write a log file
        String localAbs = af.getLocalAbsPath();
        String local = af.getLocalPath();
        String parent = af.getParentPath();

        //log file for curremt run
        File log = new File(this.rootOutputDirPath.toString(), "LogFileForThisRun.txt");

        try {
            try (PrintWriter write = new PrintWriter(log)) {
                write.println("LOCAL ABS " + localAbs);
                write.println("LOCAL " + local);
                write.println("PARENT DIR " + parent);
                write.println("EXECUTABLE FILE LOCATION " + executableFile);
                write.println("PROCESS BUILDER COMMAND STRING " + pb.command());
                write.println("IMAGE NAME " + imageName);
                write.println("NO EXTENSION NAME " + imageNameWOExt);
            }
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        try {
            Process p = pb.start();
            // p.waitFor();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return IngestModule.ProcessResult.OK;
        /*  try {
             VfProcess.runVf();
             
         } catch (IOException ex) {
             Exceptions.printStackTrace(ex);
         }
         */

    }

    @Override
    public void shutDown() {

    }

    public static File locateExecutable(String executableToFindName) throws IngestModule.IngestModuleException {
        // Must be running under a Windows operating system.
        if (!PlatformUtil.isWindowsOS()) {
            throw new IngestModule.IngestModuleException(NbBundle.getMessage(VfIngestModule.class, "unsupportedOS.message"));
        }

        File exeFile = InstalledFileLocator.getDefault().locate(executableToFindName, VfIngestModule.class.getPackage().getName(), false);
        if (null == exeFile) {
            throw new IngestModule.IngestModuleException(NbBundle.getMessage(VfIngestModule.class, "missingExecutable.message"));
        }

        if (!exeFile.canExecute()) {
            throw new IngestModule.IngestModuleException(NbBundle.getMessage(VfIngestModule.class, "cannotRunExecutable.message"));
        }

        return exeFile;
    }

    /**
     * Creates the output directory for this module for the current case, if it
     * does not already exist.
     *
     * @return The absolute path of the output directory.
     * @throws org.sleuthkit.autopsy.ingest.IngestModule.IngestModuleException
     */
    synchronized static Path createModuleOutputDirectoryForCase() throws IngestModule.IngestModuleException {
        Path path = Paths.get(Case.getCurrentCase().getModulesOutputDirAbsPath(), VfIngestFatoryAdapter.getModuleName());
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException ex) {
            // No worries.
        } catch (IOException | SecurityException | UnsupportedOperationException ex) {
            throw new IngestModule.IngestModuleException(NbBundle.getMessage(VfIngestModule.class, "cannotCreateOutputDir.message", ex.getLocalizedMessage()));
        }
        return path;
    }

}
