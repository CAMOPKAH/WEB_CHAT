package Client;

import ChatForm.Logger.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.lang.reflect.Method;


public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;
    private Object ControllerObj;
    private String EventRead;
    private Log log;
    public boolean Run=true;


    public ReadThread(Socket socket, ChatClient client, Object ObjControler, String OnMethod) {
        this.socket = socket;
        this.client = client;
        this.ControllerObj = ObjControler;
        this.EventRead = OnMethod;
        this.log=client.log;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            log.WriteSys("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void Stop()
    {
        log.WriteSys("CLOSE " + this.getClass());
        this.Run=false;
    }

    public boolean IsClose()
    {

      return client.isClose();
    }
    public String ReaderCmd(BufferedReader reader ) {
        StringBuilder SB = new StringBuilder();
        String l;


        try {
            String st= reader.readLine();
            if (st.length()>0) {SB.append(st);}
            if (!reader.ready() && SB.length()==0) {
                return "";
            }
            int C=0;
            while (reader.ready()) {
                // C=reader.read();
                // char c = (char)C;
                // SB.append(c);
                l = reader.readLine();
                SB.append(l+"\t\n");
            }

        } catch (IOException e) {
            //e.printStackTrace();
            Run=false;
        }

        return SB.toString();
    }

    public void run() {
        while (this.Run && !IsClose()) {
            //  try {


                String response = ReaderCmd(reader);
                log.WriteSys(response);
                if (response.length() > 0) {

                    CommandProtocol cmd = new CommandProtocol("");
                    cmd.Parse(response);

                    if (cmd.ReadParam("CMD") == "AUTH") {

                        log.WriteSys(cmd.ReadParam("RESPONSE"));
                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    java.lang.reflect.Method method;
                    try {
                        log.WriteSys("BEG INVOKE__________");
                        try {
                            method = ControllerObj.getClass().getMethod(EventRead, CommandProtocol.class);


                            method.invoke(ControllerObj, cmd);

                        } catch (InvocationTargetException e) {

                        }
                        log.WriteSys("END INVOKE___________");
                    } catch (SecurityException e) {
                    } catch (NoSuchMethodException e) {
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Run = false;

                    }
                    // prints the username after displaying the server's message
             /*   if (client.getUserName() != null) {
                    System.out.print("[" + client.getUserName() + "]: ");
                }*/
                    // } catch (IOException ex) {
                    //     log.WriteSys("Error reading from server: " + ex.getMessage());
                    //    ex.printStackTrace();
                    //     break;
                    // }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }

}