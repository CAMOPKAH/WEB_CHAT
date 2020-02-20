package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<String> userToken = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    public Conn dbConn;

    public ChatServer(int port) {
        this.port = port;
    }

    public boolean Auth(String Login, String Password){
        System.out.println("...AUTH" + Login+" "+Password);

        String pass= null;
        try {
            pass = dbConn.GetPasswordUser(Login.trim());
            System.out.println("Pass:" + pass);
            if (Password.equals(pass)  ) {
                return true;
            }
            else
            {return false;}
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void execute() {

        try {
            dbConn = new Conn();
            dbConn.CreateDB();
            System.out.println(dbConn.GetPasswordUser("oleg"));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat Server is listening on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread excludeUser) {
        System.out.println("broadcast:");
        System.out.println(message);
        for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName, String Token) {
        userNames.add(userName);
        userToken.add(Token);
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " quitted");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }


    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }

}