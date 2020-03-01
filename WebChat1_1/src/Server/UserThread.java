package Server;



import ChatForm.Logger.Log;
import Client.CommandProtocol;
import com.sun.deploy.util.SessionState;

import java.io.*;
import java.net.Socket;


public class UserThread implements Runnable {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    private boolean isAuth=false;
    private boolean isDead=false;
    private boolean Active=false;
    private InputStream input;
    private BufferedReader reader;
    private OutputStream output;
    private Object MON= new Object();
    private Log log;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        this.log = server.log;
    }

    public boolean IsDead(){
      boolean Res;
        synchronized (MON) {
            Res = isDead;
        }
        return Res;
    }

    public void SetDead(){
        synchronized (MON) {
            isDead=true;
        }
    }

    public boolean GetActive(){
        boolean Res;
        synchronized (MON) {
            Res = this.Active;
        }
        return Res;
    }

    public void SetActive(boolean active){
        synchronized (MON) {
           this.Active=active;
        }
    }

    public boolean Auth(CommandProtocol CP)
    {
       String Login = CP.ReadParam("LOGIN").trim();
       String Password = CP.ReadParam("PASSWORD").trim();
       return server.Auth(Login, Password);

    }

    public String GenCMDAuthResponse(String CMD,String Token, String Response) {
        CommandProtocol CP = new CommandProtocol("");
        CP.SetParam("CMD", CMD);
        CP.SetParam("TOKEN", Token);
        CP.SetParam("RESPONSE", Response);

        return CP.GenMessage("");
    }


    public String GenCMDResponse(String CMD,String Response) {
        CommandProtocol CP = new CommandProtocol("");
        CP.SetParam("CMD", CMD);
        CP.SetParam("RESPONSE", Response);

        return CP.GenMessage("");
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
            e.printStackTrace();
        }

        return SB.toString();
    }

    public String GetUserName()
    {
        return server.FindUser(this).UserName;
    }
    public void run() {
        this.SetActive(true);
        String serverMessage;
        CommandProtocol cmd = new CommandProtocol(null);
        ChatServer.ThreadClient CurClient;
        try {

            if (!isAuth) {
                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));

                 output = socket.getOutputStream();
                writer = new PrintWriter(output, true);

                printUsers();
               
                String response = ReaderCmd(reader);
                log.WriteSys(response);
               
                cmd = new CommandProtocol(null);
               
                cmd.Parse(response);
              
                String Token = null;
               
                String userName = null;
               


                String sCMD = cmd.ReadParam("CMD").trim();
                log.WriteSys(sCMD);
                if (sCMD.equals("AUTH")) {
                    
                    if (!Auth(cmd)) {
                        String Res = "Login or password not correct";
                        log.WriteSys(Res);

                        writer.println(GenCMDResponse("AUTH", Res));
                        writer.println("buy");
                        return;
                    } else {
                        log.WriteSys("AUTH OK");
                        Token = String.valueOf(reader.hashCode());
                        userName = cmd.ReadParam("LOGIN").trim();
                        writer.println(GenCMDAuthResponse(cmd.ReadParam("CMD"), Token, "AUTH:OK"));
                        log.WriteSys("Login OK");

                    }

                }


                // String userName = reader.readLine();


                server.addUserName(userName, Token, this);
                 CurClient = server.FindUser(this);

                serverMessage = "New user connected: " + userName;
                server.broadcast(serverMessage, this);

                //Переключаем в авторизованного
                isAuth = true;
            }

            else if (isAuth) {
                //log.WriteSys("DOUBLE START");
                 CurClient = server.FindUser(this);
                String clientMessage = null;

                do {
                    if (reader.ready()) {
                        clientMessage = ReaderCmd(reader);//reader.readLine();
                        if (isEventBye(clientMessage)) {
                            SetDead();
                            break;
                        }
                        cmd.Parse(clientMessage);
                        log.WriteSys(cmd.GenMessage(cmd.Text));
                        if (IfCMDName(cmd, "RENAME")) {
                            String newName = cmd.ReadParam("PARAM").trim();
                            if (newName.length() > 0) {


                                //log.WriteSys("Rename:" + userName + "->" + newName);
                                String LastName = CurClient.UserName;
                                server.RenameUserName(newName, this);

                                //Оповещаем всех о переименовании пользователя
                                server.broadcast(GenCMDMessage("Rename user:" + LastName + " -> " + CurClient.UserName), this);


                            /*
                            server.removeUser(userName, this);
                            userName = newName;
                            server.addUserName(userName, Token);
*/
                            }
                        }
                        serverMessage = "[" + CurClient.UserName + "]: " + cmd.Text;
                        log.WriteSys("broadcast:" + serverMessage);
                        server.broadcast(GenCMDMessage(serverMessage), this);

                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    //Отправялем сообщения если есть
                    WriteMessages();

                    if (isEventBye(clientMessage)) {
                        SetDead();
                        break;
                    }

                } while (false);//(!isEventBye(clientMessage));

                if (isDead) {
                    server.removeUser(CurClient.UserName, this);
                    socket.close();

                    serverMessage = CurClient.UserName + " has quitted.";
                    server.broadcast(GenCMDMessage(serverMessage), this);
                }
                }

        } catch (IOException ex) {
            log.WriteSys("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
        this.SetActive(false);
    }
    //Проверка на отключение пользователя
    public boolean isEventBye(String sMessage)
    {
        if  ( sMessage!=null && sMessage.equals("bye")) return true;
       return false;
    }

    //Отправляем буффер с сообщениями
    public void WriteMessages()
    {
        ChatServer.ThreadClient client = server.FindUser(this);
        synchronized (client.Messages) {
            if (client.Messages.size() > 0) {
                for (String o : client.Messages) {
                    writer.println(o);
                }
                client.Messages.clear();

            }
        }
    }

    public String GenCMDMessage(String Text)
    {
        CommandProtocol cmd= new CommandProtocol(Text);
        cmd.SetParam("TOKEN", "SERVER");
        cmd.SetParam("CMD", "SEND");
        return cmd.GenMessage(Text);

    };

    private boolean IfCMDName(CommandProtocol cmd, String rename) {

        String sCMD=cmd.ReadParam("CMD").trim();

        if ( sCMD.equals(rename) ){
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Sends a list of online users to the newly connected user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Sends a message to the client.
     */
    void sendMessage(String message) {
        log.WriteSys("WRITELN:" +message);
        writer.println(message);
    }
}
