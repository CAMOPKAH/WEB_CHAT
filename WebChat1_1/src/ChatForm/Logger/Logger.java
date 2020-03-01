package ChatForm.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class Logger extends LoggerThread {
    String AliasFileName;
    String CurFileName;
    String PathLog=GetCurPath();
    RandomAccessFile fLog;
    OutputStream BOStream;

    private String LastFileLog;

    //Генерация текущего файла с логами
    public String GetCurFile(){
        Date dt = new Date();
        SimpleDateFormat sDF = new SimpleDateFormat("YYYY-MM-dd");
        return sDF.format(dt).toString()+"[" + AliasFileName + "].log";
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
    public Logger(String aliasFileName)
    {

        renameNikName(aliasFileName);
    }

    public void renameNikName(String nikName)
    {
        AliasFileName = nikName;
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

            //String res = new String(sMsg.getBytes("windows-1251"), "ISO-8859-1");
            fLog.write(sMsg.getBytes("windows-1251"));
            fLog.write((byte)13);
            fLog.write((byte)10);
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    @Override
    public void Flush() {
        try {
            fLog.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
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


    @Override
    public void WriteToFile(String sMsg){
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