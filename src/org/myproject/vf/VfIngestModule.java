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
import org.sleuthkit.datamodel.TskCoreException;

public class VfIngestModule implements FileIngestModule {

    //declare variables
    private IngestJobContext context = null;
    private static final String VOLATILITY_DIRECTORY = "volatility-master";
    private static final String VOLATILITY_EXECUTABLE = "vol.py";
    private File executableFile;
    private Path rootOutputDirPath;
    private static final Logger logger = Logger.getLogger(VfIngestModule.class.getName());
    private static final IngestModuleReferenceCounter refCounter = new IngestModuleReferenceCounter();
    private final VfModuleSettings settings;
    private long jobId;

    VfIngestModule(VfModuleSettings settings) {
        this.settings = settings;
    }

    @Override
    public void startUp(IngestJobContext context) throws IngestModuleException {
        this.context = context;

        //locate vol.py in release folder
        Path execName = Paths.get(VOLATILITY_DIRECTORY, VOLATILITY_EXECUTABLE);
        executableFile = locateExecutable(execName.toString());

        this.rootOutputDirPath = VfIngestModule.createModuleOutputDirectoryForCase();
        jobId = context.getJobId();

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
        SimpleDateFormat format = new SimpleDateFormat("_yyyy-MM-dd_kk-mm-ss");
        //apply format
        String dateString = format.format(date);

        //String volPath = "C:\\Documents and Settings\\Vic\\My Documents
        //                    \\NetBeansProjects\\VF\\build\\cluster\\volatility-2.4\\vol.py";
        // variables for VF
        String volProfile = "--profile=" + settings.getVolProfile();
        String volInFile = "--filename=";
        String volOutFile = "--output-file=";
        String pathToImage = af.getLocalPath();

        //create image name and image name without extension
        String imageName = af.getName();
        //String imageNameWOExt = FilenameUtils.removeExtension(imageName);
        String imageNameWOExt = FilenameUtils.removeExtension(imageName);
        //create output directory
        String folder = imageNameWOExt;
        Path outputDirPath = Paths.get(this.rootOutputDirPath.toAbsolutePath().toString(), folder);
        try {
            Files.createDirectories(outputDirPath);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        /*
         create new ProcessBuilder object
        
         doule quoutes very important for running Python from
         command line in folders with white spces
         "\"" +executableFile + "\""
        
         tets: volFilename+"\""+imageLocation+"\""
         "\"" +executableFile+ "\""
       
         also: volFile+pathToFile may require "\"" volFile+pathToFile "\"" but it works with my compilation every time.
        
         This command: python "C:\Documents and Settings\Vic\My Documents\NetBeansProjects\VF\build\cluster\volatility-2.4\vol.py" --profile=LinuxEvo4GARM --filename=C:\Documents and Settings\Vic\My Documents\Evo4GRodeo.lime linux_pstree
         Will not wotk from command line but it works from ProcessBuilder
         Theoreticaly this command should have double quotes around pathToFile - image file
        
         In this ProcessBuilder the following values are passed to Volatility Framework:
        
         "python " - to invoke python interpreter
         executableFile - to find path to vol.py must be surrunded with "\""executableFile"\"" to build path that python can read - workaround white spaces in directories names
         volProfile - profile of the memory image that VF analysis in this job
         volInFile - Volatility prameter to indicate location of image to analyse
         pathToImage - path to memory image
         volOutFile - Volatility parameter to indicate location of output directory
         ouputDirPath - path to output directory
         imageNameWOExt - name of memory image with out extension
         dateString - string with date and time      
         */
        //get the number of plugins to run
        int numberOfPlugins = settings.getVolPlugins().size();

        //for loop to run all the selected plugins
        for (int i = 0; i < numberOfPlugins; i++) {
            
            //if job is cancelled by user
            if (this.context.isJobCancelled() == true) {

                //System.out.println("CANCELED");
                return IngestModule.ProcessResult.OK;

            }

            //get current plugin to run
            String volPlugin = settings.getVolPlugins().get(i).toString();
            System.out.println("VOL PLUGIN " + volPlugin + i);

            ProcessBuilder pb = new ProcessBuilder("python", "\"" + executableFile + "\"",
                    volProfile, volInFile + pathToImage, volOutFile + outputDirPath + "\\" + imageNameWOExt + "_" + volPlugin + dateString + ".txt", volPlugin);

            // write error logfile to .txt file in the 
            File log = new File(outputDirPath.toString(), "VFprocessErrorLog_" + volPlugin + ".txt");
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(log));

            //write a log file
            String localAbs = af.getLocalAbsPath();
            String local = af.getLocalPath();
            String parent = af.getParentPath();

            //log file for curremt run
            File logCurrent = new File(this.rootOutputDirPath.toString(), "LogFileForThisRun.txt");
            File logAll = new File(this.rootOutputDirPath.toString(), "LogFileAll.txt");

            try (PrintWriter write = new PrintWriter(logCurrent)) {
                write.println("TIME " + dateString);
                write.println("LOCAL ABS " + localAbs);
                write.println("LOCAL " + local);
                write.println("PARENT DIR " + parent);
                write.println("EXECUTABLE FILE LOCATION " + executableFile);
                write.println("PROCESS BUILDER COMMAND STRING " + pb.command());
                write.println("IMAGE NAME " + imageName);
                write.println("NO EXTENSION NAME " + imageNameWOExt);
                write.println("OUTPUT PATH " + outputDirPath.toString());
                write.println("OPERATING SYSTEM TO ANALYZE  " + settings.opSystem());
                write.println("VOLATILITY PROFILE " + settings.getVolProfile());
                write.println("ARRAY LIST OF PLUGINS: " + settings.getVolPlugins().toString());

            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                FileWriter writeFile = new FileWriter(logAll, true);
                BufferedWriter bufferWrite = new BufferedWriter(writeFile);
                try (PrintWriter print = new PrintWriter(bufferWrite)) {
                    print.println("TIME " + dateString);
                    print.println("LOCAL ABS " + localAbs);
                    print.println("LOCAL " + local);
                    print.println("PARENT DIR " + parent);
                    print.println("EXECUTABLE FILE LOCATION " + executableFile);
                    print.println("PROCESS BUILDER COMMAND STRING " + pb.command());
                    print.println("IMAGE NAME " + imageName);
                    print.println("NO EXTENSION NAME " + imageNameWOExt);
                    print.println(outputDirPath.toString());
                    print.println("OPERATING SYSTEM TO ANALYZE  " + settings.opSystem());
                    print.println("VOLATILITY PROFILE " + settings.getVolProfile());
                    print.println("ARRAY LIST OF PLUGINS: " + settings.getVolPlugins().toString());
                    print.println("\n\n");
                }
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

            //run porocess
            // int exitValue = ExecUtil.execute(pb, new FileIngestModuleProcessTerminator(this.context));
            try {
                Process p = pb.start();
                p.waitFor();

            } catch (IOException | InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            

            //Produce the report in Autopsy
            try {
                Case.getCurrentCase().addReport(outputDirPath.toString() + "\\" + imageNameWOExt + "_" + volPlugin + dateString + ".txt", VfIngestFactoryAdapter.getModuleName(), volPlugin + " analysed " + imageName);
            } catch (TskCoreException ex) {
                Exceptions.printStackTrace(ex);
            }

            return IngestModule.ProcessResult.OK;

        } //end of for loop

        return IngestModule.ProcessResult.OK;

    } //end of public ProcessResult process(AbstractFile af)

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

    /*
     * Creates the output directory for this module for the current case, if it
     * does not already exist.
     *
     * @return The absolute path of the output directory.
     * @throws org.sleuthkit.autopsy.ingest.IngestModule.IngestModuleException
     */
    synchronized static Path createModuleOutputDirectoryForCase() throws IngestModule.IngestModuleException {
        Path path = Paths.get(Case.getCurrentCase().getModulesOutputDirAbsPath(), VfIngestFactoryAdapter.getModuleName());
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
