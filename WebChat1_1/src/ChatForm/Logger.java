package ChatForm;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Logger {
    String NikName;
    String CurFileName;
    String PathLog;
    RandomAccessFile fLog;

    private int DefCountLastMessage=100;
    private String LastFileLog;


    public ArrayList<String> GetLastMsg() {
        return GetLastMsg(DefCountLastMessage);
    }

    public ArrayList<String> GetLastMsg(int CountlastMessage)
    {
        ArrayList<String> maskFiles= new ArrayList<>();
        File dir = new File(PathLog);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("["+NikName+"].log");
            }
        });

        for (File logfile : files) {
            maskFiles.add(logfile.toString());
        }
        maskFiles.sort(Collections.reverseOrder());
        int iCount=0;
        ArrayList<String> CurArr = new ArrayList<>();
        for (String file : maskFiles) {

            ArrayList<String> fileArr = new ArrayList<>();
            if (CurArr.size()-1>CountlastMessage) {

                break;
            }

            try {
                RandomAccessFile rA = new RandomAccessFile(file, "r");
                String line;
                while ((line=rA.readLine())!=null) {
                    fileArr.add(line);
                }
            }

            catch (IOException e)
            {
                System.out.println(e.getMessage());
                wrtLog("Error:" + e.getMessage());
            }
            CurArr.addAll(0,fileArr);


        }

        if (CurArr.size()-1>CountlastMessage) {
            ArrayList<String> fileArr = new ArrayList<>();
            for (int i = CurArr.size()-CountlastMessage-1; i < CurArr.size(); i++) {
                fileArr.add(CurArr.get(i));

            }
            return fileArr;
        }


        return CurArr;
    }
    //Генерация текущего файла с логами
    public String GetCurFile(){
        Date dt = new Date();
        SimpleDateFormat sDF = new SimpleDateFormat("YYYY-MM-dd");
        return sDF.format(dt).toString()+"[" + NikName + "].log";
    }
    public String GetCurPath(){

       String path = String.valueOf(Paths.get(".").toAbsolutePath());
       path = path + "Log\\";
       File fl = new File(path);
       if (!fl.exists()) {
            fl.mkdir();
        }

        System.out.println(path);
        return path;
    }

    public String GetLogFile(){
        return PathLog+CurFileName;
    }
    public Logger(String nikName)
    {
        PathLog=GetCurPath();
        renameNikName(nikName);
    }

    public void renameNikName(String nikName)
    {
        NikName = nikName;
        CurFileName = GetCurFile();

    }

    public void FileReOpen(){
        try {
            String CurFileName = GetLogFile();

            fLog =  new RandomAccessFile(CurFileName, "rw");
            LastFileLog=CurFileName;

            if (!(fLog.length()<1)) { //Если длина не равна нулю, ставим курсор в конец
                fLog.seek(fLog.length());
            }

        }
        catch (IOException e)
        {
             e.printStackTrace();
        }
    }

    private boolean wrtLog(String sMsg){
        try {
            fLog.writeChars(sMsg + "\n");

            return true;
        }
        catch (IOException e)
        {
            return false;
        }


    }
    public void changeFile(){
        try {
            if (fLog!=null) {
                //Пишем текущйи файл


                fLog.close();
            }
            FileReOpen();
            wrtLog(LastFileLog);

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public void WriteLog(String sMsg){
       if (fLog==null) {
           FileReOpen();
       };

       if ( !GetLogFile().equals(LastFileLog)) {
           changeFile();
       }

       if (!(fLog!=null && wrtLog(sMsg))) {
           System.out.println("ErrWriteLog:" + sMsg);
       }
        LastFileLog=GetLogFile();

    }
}
