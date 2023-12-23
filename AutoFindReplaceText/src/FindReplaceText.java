import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

import static java.lang.System.exit;

class FindReplaceConfig {
    private final String findStr;
    private final String replaceStr;
    private final String patternChar;

    FindReplaceConfig (String f, String r, String p){
        findStr = f;
        replaceStr = r;
        patternChar = p;
    }

    public String toString() {
        return findStr + "\n" + replaceStr + "\n" + patternChar;
    }

    public String getFindStr(){ return findStr;}
    public String getReplaceStr(){ return replaceStr;}
}
public class FindReplaceText {
    public static String utilityEnvConfigFile = "";
    public static String findReplaceConfigFile = "";
    public static String findReplaceConfigFileDelimiter = "";
    public static String inFilesFolder = "";
    public static String outFilesFolder = "";

    public static String sourceSchema="";
    public static String targetSchema="";

    public static String actionScopeFileType="";


    public static void main(String[] args) throws IOException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        String findWhatStr;
        String replaceWithStr;
        String inFilePathStr;
        String outFilePathStr;
        Path inPath;
        Path outPath;
        String fileContent;
        String[] sourceFileNameArr;


        Charset charset = StandardCharsets.UTF_8;
        BufferedReader findReplaceConfigFileBR = null;

        if (args.length == 0) {
          System.out.println("This utility expects config file path as the first parameter.");
          System.out.println("Execute your program with arguments, e.g : java FindReplaceText C:\\user\\dev\\env" );
          exit(0);
        }
        setEnvVariables(args[0]);
        LinkedList<FindReplaceConfig> frl = new LinkedList<>();
        try{

            String[] strArray;
            String configLineContent;
            File folder;
            File[] folderListing;

//            System.out.println("findReplaceConfigFile:"+findReplaceConfigFile);
            findReplaceConfigFileBR = new BufferedReader(new FileReader(findReplaceConfigFile));

            configLineContent= findReplaceConfigFileBR.readLine();

//            System.out.println("configLineContent:"+configLineContent);


            while (configLineContent != null){
                strArray = configLineContent.split(findReplaceConfigFileDelimiter);
                frl.add(new FindReplaceConfig(strArray[0],strArray[1],strArray[2]));
                configLineContent= findReplaceConfigFileBR.readLine();
            }

            folder = new File(inFilesFolder);
            folderListing = folder.listFiles();

            if (folderListing != null){
                for (File child : folderListing){
                    inFilePathStr = inFilesFolder+child.getName();
                    if(inFilePathStr.contains(actionScopeFileType)) {
                        System.out.println(".sql file :" + inFilePathStr );
                        sourceFileNameArr = inFilePathStr.split("\\.");
                        inPath = Path.of(inFilePathStr);

                        outFilePathStr = outFilesFolder + targetSchema+"." + sourceFileNameArr[1] + actionScopeFileType;
                        System.out.println("outFilePathStr: " + outFilePathStr);
                        outPath = Path.of(outFilePathStr);
                        fileContent = Files.readString(inPath, charset);
                        System.out.println("****************************************");
                        System.out.println("file name: " + inFilePathStr);
                        System.out.println("file content String length before replacing: " + fileContent.length());
                        //System.out.println("file content before replacing: \n"+fileContent);

                        for (FindReplaceConfig findReplaceConfig : frl) {
                            findWhatStr = findReplaceConfig.getFindStr();
                            replaceWithStr = findReplaceConfig.getReplaceStr();
                            System.out.println("Replacing '" + findWhatStr + "' with '" + replaceWithStr + "'");
                            fileContent = fileContent.replaceAll(findWhatStr, replaceWithStr);
                        }
                        //System.out.println("fileContent after String replace :");
                        System.out.println(fileContent);
                        Files.writeString(outPath, fileContent, charset);
                    }else {
                        System.out.println("Not a .sql file, skipping the file"+inFilePathStr);
                    }
                }
                System.out.println("****************************************");
            }else{
                System.out.println("No files in the input directory to find and replace. Input Dir:"+ inFilesFolder);
            }

        } catch (FileNotFoundException e) {
            System.out.println("no file found... please keep a file with the name OraProc_1.txt");
            throw new RuntimeException(e);
        }
        finally {
                   if (findReplaceConfigFileBR != null) findReplaceConfigFileBR.close();
               }

    }
    static void setEnvVariables (String pConfigFile) throws IOException {

        BufferedReader envConfigFileBR = new BufferedReader(new FileReader(pConfigFile));

        //System.out.println("Environment File:"+pConfigFile);;
        utilityEnvConfigFile = pConfigFile;

        String envConfigLineContent;

        //inFilesFolder
        //outFilesFolder
        envConfigLineContent= envConfigFileBR.readLine();

        String[] lineStr;

        while (envConfigLineContent != null){
            lineStr = envConfigLineContent.split("=");
            switch (lineStr[0]) {
                case "findReplaceConfigFile" : findReplaceConfigFile = lineStr[1];
                    break;

                case "findReplaceConfigFileDelimiter" : findReplaceConfigFileDelimiter = lineStr[1];
                    break;

                case "inFilesFolder" : inFilesFolder = lineStr[1];
                    break;

                case "outFilesFolder" : outFilesFolder = lineStr[1];
                    break;

                case "sourceSchema" : sourceSchema = lineStr[1];
                    break;

                case "targetSchema" : targetSchema = lineStr[1];
                    break;

                case "actionScopeFileType" : actionScopeFileType = lineStr[1];
                    break;
            }
            envConfigLineContent= envConfigFileBR.readLine();

        }
    }
}