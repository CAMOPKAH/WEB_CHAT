import Server.ChatServer;
import Server.Conn;
import config.ReadConfig;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        String current = null;
        ReadConfig readConfig = new ReadConfig();

        try {
            Conn con = new Conn();
            con.CreateDB();
           // System.out.println(con.GetPasswordUser("oleg"));
           // con.WriteDB();
            con.ReadDB();
            con.CloseDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        int port = -1;
        if (readConfig.ReadParam("TcpPort")!=null)
        {
            port = Integer.parseInt(readConfig.ReadParam("TcpPort"));
        }

        if (args.length <1 && port==-1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            System.exit(0);
        }
        else if (args.length >0)
        {
            port = Integer.parseInt(args[0]);
        }


        ChatServer server = new ChatServer(port);
        server.execute();
    }
}
