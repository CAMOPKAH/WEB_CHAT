package ChatForm.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerSys extends Logger {

    public LoggerSys(String aliasFileName) {
        super(aliasFileName);
    }

    //Генерация текущего файла с логами
    @Override
    public String GetCurFile(){
        Date dt = new Date();
        SimpleDateFormat sDF = new SimpleDateFormat("YYYY-MM-dd");
        return sDF.format(dt).toString()+"[" + AliasFileName+ "]_APP.log";
    }


}
