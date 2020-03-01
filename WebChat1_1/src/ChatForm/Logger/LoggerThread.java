package ChatForm.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class LoggerThread implements Runnable {
    private ConcurrentLinkedDeque<String> LogQueue = new ConcurrentLinkedDeque<>();
    volatile boolean CloseFlag=false;
    private Object MON=new Object();


    public void WriteLog(String sMsg) {
        //System.out.println("QUEUE:" + sMsg);
        LogQueue.add(GetDate() + sMsg);

    }
    private String GetDate(){
        String pattern = "MM/dd/yyyy HH:mm:ss";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
       return "[" + df.format(today) + "] ";
    }

    void WriteToFile(String sMsg){
        //System.out.println(sMsg);
    }

    public void FlushQueue(){
      int Count = LogQueue.size();

        if (Count>0) {
            for (int i = 0; i < Count; i++) {
                WriteToFile(LogQueue.getFirst());

                LogQueue.removeFirst();
            }
            Flush();
        }
    }

    public void Flush(){
        //Сдесь делаем Flush
    }


    public void Close()
    {
        synchronized (MON) {
            CloseFlag = true;
        }
    }

    public boolean GetClose() {
      boolean Res;
           synchronized (MON) {
               Res = CloseFlag;
              // System.out.println(Res);
           }
       return Res;
    }

    @Override
    public void run() {


        while (!GetClose())
            {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            FlushQueue();//Скидываем файл

        }
    }
}
