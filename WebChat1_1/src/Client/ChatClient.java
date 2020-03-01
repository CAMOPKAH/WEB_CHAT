package Client;

import ChatForm.Controller;
import ChatForm.Logger.Log;
import config.ReadConfig;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ChatClient {
    private String hostname;
    private int port;
    private String userName;
    private Controller controller;
    Log log;

    WriteThread wrtThread;
    ReadThread readThread;
    private Object ReadObject;
    private String EventRead;

    public boolean isClose() {
        //System.out.println(controller==null);

        return controller==null;
    }
    public void Close()
    {
        System.out.println("ThreadClose");
        if (wrtThread!=null) {wrtThread.Run=false;};
        if (readThread!=null) {readThread.Run=false;};

    }
    public void SetEventRead(Object ControllerObj, String eventRead){
        ReadObject = ControllerObj;
        EventRead = eventRead;
    }

    public ChatClient(String hostname, int port, String UserName, Log log) {
        this.hostname = hostname;
        this.port = port;
        this.userName = UserName;
        this.log = log;
    }

    public ChatClient(String UserName, Controller controller) {
        this.log=controller.log;
        ReadConfig readConfig = new ReadConfig(log);
        this.userName = UserName;

        this.hostname= readConfig.ReadParam("TcpServerIp");
        this.port = readConfig.ReadParamInt("TcpPort");
        this.controller=controller;

    }

    public void SendText(String Text){
        if (wrtThread!=null) {
            wrtThread.SendText(Text);
        }
    }

    public void SendCommand(CommandProtocol cmd, String Text){
        SendText(cmd.GenMessage(Text));
    }

    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            log.WriteSys("Connected to the chat server");

            readThread = new ReadThread(socket, this, ReadObject, EventRead);
            readThread.start();
            //new ReadThread(socket, this).start();
            wrtThread = new WriteThread(socket, this);
            wrtThread.start();

        } catch (UnknownHostException ex) {
            log.WriteSys("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            log.WriteSys("I/O Error: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


}
