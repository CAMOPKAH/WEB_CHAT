package ChatForm.Logger;

import ChatForm.Logger.LoggerSys;
import ChatForm.Logger.LoggerUser;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Log {
    LoggerUser LogMessage = new LoggerUser("");
    LoggerSys LogSys = new LoggerSys("");
    ExecutorService executorService = Executors.newFixedThreadPool(2);

    public void User_Rename(String nickName) {
        LogMessage.renameNikName(nickName);
    }

    public ArrayList<String> GetLastMsg() {
        return LogMessage.GetLastMsg();
    }

    public void WriteLog(String sMsg)
    {
        if (LogMessage!=null) {
            LogMessage.WriteLog(sMsg);
        }
    }

    public void WriteSys(String sMsg)
    {
        System.out.println(sMsg);
        if (LogSys!=null) {
            LogSys.WriteLog(sMsg);
        }
    }

    public Log(String SysAlias )
    {
        LogSys.AliasFileName = SysAlias;
        executorService.submit(LogMessage );
        executorService.submit(LogSys);
    }

    public void Close() {
        LogMessage.Close();
        LogSys.Close();

        executorService.shutdown();
    }


    public void renameNikName(String name) {
        LogMessage.renameNikName(name);
    }
}

