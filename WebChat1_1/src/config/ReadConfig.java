package config;

import ChatForm.Logger.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ReadConfig {
   public Properties Prop = new Properties();
   String ConfigFile = "Config.properties";
    private Log log;

   public String FileExists(String Path, String FileName){
       StringBuilder SB = new StringBuilder();
       SB.append(Path);
       SB.append('/');
       SB.append(FileName);
       File eFile= new File(SB.toString());
       System.out.println(SB.toString());
       if ((eFile.exists()))
       {
           return SB.toString();
       }

       SB = new StringBuilder();
       SB.append(Path);
       SB.append("\\");
       SB.append(FileName);
        eFile= new File(SB.toString());
       System.out.println(SB.toString());
       if ((eFile.exists()))
       {
           return SB.toString();
       }

       return null;
   }
    public ReadConfig(Log log) {
       this.log=log;

        try {
           String Path =  new File( "." ).getCanonicalPath();
           String FileCfg = FileExists(Path, ConfigFile);
           // log.WriteSys("Чтение параметров:"+ConfigFile);
           Prop.load(new FileInputStream( FileCfg));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.WriteSys("Чтение параметров завершно");
    }

    public String ReadParam(String Key) {
        return Prop.getProperty(Key);

    }
    public int ReadParamInt(String Key) {
        return Integer.parseInt(Prop.getProperty(Key));

    }
}
