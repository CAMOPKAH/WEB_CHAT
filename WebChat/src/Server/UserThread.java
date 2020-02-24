package Server;



import Client.CommandProtocol;

import java.io.*;
import java.net.Socket;


public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
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

    public void run() {
        try {

            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();
            System.out.println("D1");
            String response = ReaderCmd(reader);
            System.out.println(response);
            System.out.println("D2");
            CommandProtocol cmd=new CommandProtocol(null);
            System.out.println("D3");
            cmd.Parse(response);
            System.out.println("D4");
            String Token = null;
            System.out.println("D5");
            String userName = null;
            System.out.println("D6");



            String sCMD=cmd.ReadParam("CMD").trim();
            System.out.println(sCMD);
            if ( sCMD.equals("AUTH") )
            {
                System.out.println("s1");
                    if (!Auth(cmd)) {
                    String Res = "Login or password not correct";
                    System.out.println(Res);

                    writer.println(GenCMDResponse("AUTH", Res));
                    writer.println("buy");
                    return;
                }
                else
                {
                    System.out.println("AUTH OK");
                    Token = String.valueOf(reader.hashCode());
                    userName=cmd.ReadParam("LOGIN").trim();
                    writer.println(GenCMDAuthResponse(cmd.ReadParam("CMD"), Token,"AUTH:OK"));
                    System.out.println("Login OK");

                }

            }


                // String userName = reader.readLine();


                server.addUserName(userName, Token);

                String serverMessage = "New user connected: " + userName;
                server.broadcast(serverMessage, this);

                String clientMessage;

                do {
                    clientMessage = ReaderCmd(reader);//reader.readLine();
                    if (clientMessage.equals("bye")) { break; }
                    cmd.Parse(clientMessage);
                    System.out.println(cmd.GenMessage(cmd.Text));
                    if (IfCMDName(cmd,"RENAME")) {
                        String newName = cmd.ReadParam("PARAM").trim();
                        if (newName.length()>0)
                        {
                            System.out.println("Rename:"+userName + "->" + newName);
                            server.removeUser(userName, this);
                            userName = newName;
                            server.addUserName(userName, Token);

                        }
                    }
                    serverMessage = "[" + userName + "]: " + cmd.Text;
                    System.out.println(serverMessage);
                    server.broadcast(GenCMDMessage(serverMessage), this);

                } while (!clientMessage.equals("bye") );

                server.removeUser(userName, this);
                socket.close();

                serverMessage = userName + " has quitted.";
                server.broadcast(GenCMDMessage(serverMessage), this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
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
        writer.println(message);
    }
}
