package ChatForm;

import Client.ChatClient;
import Client.CommandProtocol;

public class Player {
    String Name;
    String ServerIP="127.0.0.1";
    public String Token;
    int ServerPort=8080;
    public ChatClient chat;
    public Player (){
        Name="none";
    }

    public void setEventRead(Object objController, String EventRead) {

    }
    public void Connect(Object objController, String EventRead) {
        chat = new ChatClient(Name);
        chat.SetEventRead(objController, EventRead);
        chat.execute();
    }
    public boolean SendMessage(String Text)
    {
        CommandProtocol cmd= new CommandProtocol(Text);
        cmd.SetParam("TOKEN", Token);
        cmd.SetParam("CMD", "SEND");
        chat.SendCommand(cmd, Text);

        System.out.println("Отправка:"+Name + ":" + Text);
        //chat.SendText(Text);
        return true;
    };

    public void SendCommand(CommandProtocol cmd, String Text){
        System.out.println("Отправка:"+Name + ":" + Text);
        chat.SendCommand(cmd, Text);

    }
    public String GetAuthToken(String Login,String Password) {
        CommandProtocol CP = new CommandProtocol("");
        CP.SetParam("CMD","AUTH");
        CP.SetParam("LOGIN", Login);
        CP.SetParam("PASSWORD", Password);
        SendCommand(CP,"");
        return "";
    }
    public boolean Auth(String Login,String Password)
    {

        GetAuthToken(Login, Password);
       // boolean Res = Auth.Auth(Login, Password);
        this.Name = Login;
        return true;//Res;
    }

    public void Rename(String text) {
        CommandProtocol CP = new CommandProtocol("");
        CP.SetParam("CMD","RENAME");
        CP.SetParam("PARAM", text);

        SendCommand(CP,"Rename:" + this.Name + "->"+text);
        this.Name=text;

    }
}
