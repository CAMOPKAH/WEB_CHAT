package Server;

import com.sun.deploy.util.SessionState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class ChatServer {
    private int port;
    //private Set<String> userNames = new HashSet<>();
    //private Set<String> userToken = new HashSet<>();
    //private Set<UserThread> userThreads = new HashSet<>();
    private Set<ThreadClient> Clients = new HashSet<>();
    public Conn dbConn;
    public ThreadPoolExecutor executor;

    public class ThreadClient {
        String UserName;
        String userToken;
        ArrayList<String> Messages = new ArrayList<>();
        UserThread userThread;

        public void AddMessage(String sText)
        {
            synchronized (Messages)
            {
                System.out.println("AddMessage:" + sText);
                Messages.add(sText);
            }
        }
        public ThreadClient(UserThread userThread, String userName, String userToken)
        {
            this.UserName=userName;
            this.userToken=userToken;
            this.userThread=userThread;
        }
    }

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
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
           // ExecutorService executor = Executors.newFixedThreadPool(1);

            System.out.println("Start executer service");
            new Thread(()->{
                int Cnt = executor.getActiveCount();
                while (executor!=null) {
/*
                    if (Cnt!=executor.getActiveCount())
                    {
                        System.out.println("_____________________________________________________");
                        System.out.println("<<<<<<<" +  executor.getActiveCount() + ">>>>>>>>>>>>" +  executor.getPoolSize());
                        Cnt=executor.getActiveCount();
                    }*/
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (Clients.size()>-1 &&executor.getActiveCount()<executor.getPoolSize() ) {
                        for (ThreadClient client : Clients) {
                            if (!client.userThread.IsDead() && !client.userThread.GetActive()) {
                                if (executor.getActiveCount()<executor.getPoolSize())
                                executor.execute(client.userThread);
                                else break;

                            }
                        }


                    }
                }
            }).start();

            //executor.

            while (true) {
                System.out.println("--------");
                Socket socket = serverSocket.accept();
                System.out.println("New user connected");

                UserThread newUser = new UserThread(socket, this);
                //userThreads.add(newUser);
                executor.execute(newUser);
                System.out.println("NEW______" );
                System.out.println("END");

                //newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread aUser) {
        System.out.println("broadcast:");
        System.out.println(message);
       /* for (UserThread aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }

        */
        for (ThreadClient client : Clients) {
            if (client.userThread != aUser) {
                client.AddMessage(message);
                //aUser.sendMessage(message);
            }
        }

    }

    /**
     * Stores username of the newly connected client.
     */

    void addUserName(String userName, String Token, UserThread aUser) {
        Clients.add(new ThreadClient(aUser,userName,Token));
        //userNames.add(userName);
        //userToken.add(Token);
    }

    void RenameUserName(String userName,  UserThread aUser) {

        ThreadClient TC = FindUser(aUser);
        if (TC!=null)
        {
            System.out.println( "RenameUserName:" + TC.UserName + "->" + TC.UserName );
            TC.UserName=userName;

        }

        //userNames.add(userName);
        //userToken.add(Token);
    }


    ThreadClient FindUser(UserThread aUser) {
        ThreadClient FindClient = null;
        for (ThreadClient client : Clients) {
            if (client.userThread == aUser) {
                FindClient=client;

            }

        }
        return FindClient;
    }

    /**
     * When a client is disconneted, removes the associated username and UserThread
     */
    void removeUser(String userName, UserThread aUser) {
        ThreadClient delClient = null;
        for (ThreadClient client : Clients) {
            if (client.userThread==aUser)
            {
                    delClient = client;
                    break;
            }

        }

        boolean removed;
        if (delClient!=null) {
            removed = Clients.remove(delClient);
        }
        else
        {
            removed=false;
        }

        if (removed) {

            System.out.println("The user " + userName + " quitted");
       }

    }

    ArrayList<String> getUserNames() {
        ArrayList<String> AL = new ArrayList<>();
        for (ThreadClient client :  Clients) {
            AL.add(client.UserName);
        }

        return AL;
    }


    boolean hasUsers() {
        return !this.Clients.isEmpty();
    }

}