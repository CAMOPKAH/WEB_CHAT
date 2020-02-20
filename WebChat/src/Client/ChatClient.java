package Client;

import config.ReadConfig;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class ChatClient {
    private String hostname;
    private int port;
    private String userName;

    WriteThread wrtThread;
    ReadThread readThread;
    private Object ReadObject;
    private String EventRead;

    public void Close()
    {
        if (wrtThread!=null) {wrtThread.Run=false;};
        if (readThread!=null) {readThread.Run=false;};

    }
    public void SetEventRead(Object ControllerObj, String eventRead){
        ReadObject = ControllerObj;
        EventRead = eventRead;
    }

    public ChatClient(String hostname, int port, String UserName) {
        this.hostname = hostname;
        this.port = port;
        this.userName = UserName;
    }

    public ChatClient(String UserName) {
        ReadConfig readConfig = new ReadConfig();
        this.userName = UserName;

        this.hostname= readConfig.ReadParam("TcpServerIp");
        this.port = readConfig.ReadParamInt("TcpPort");
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

            System.out.println("Connected to the chat server");

            readThread = new ReadThread(socket, this, ReadObject, EventRead);
            readThread.start();
            //new ReadThread(socket, this).start();
            wrtThread = new WriteThread(socket, this);
            wrtThread.start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


}
