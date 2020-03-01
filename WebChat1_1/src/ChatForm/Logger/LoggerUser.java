package ChatForm.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class LoggerUser extends Logger{
    private int DefCountLastMessage=100;
    public LoggerUser(String aliasFileName) {
        super(aliasFileName);
    }
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
                return name.endsWith("["+AliasFileName+"].log");
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
                //RandomAccessFile rA = new RandomAccessFile(file, "r");
                //
               // File bFile = new File(file);

                //InputStream inputStream = new InputStreamReader(new FileInputStream(file));
                File bFile = new File (file);

                Reader rA = new
                        InputStreamReader(new FileInputStream(file), "Windows-1251");

                BufferedReader reader =
                        new BufferedReader(rA);

                String line;
                while ((line=reader.readLine())!=null) {
                    fileArr.add(line);

                }
            }

            catch (IOException e)
            {
                System.out.println(e.getMessage());

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
}
