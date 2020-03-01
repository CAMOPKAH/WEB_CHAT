package Server;

import ChatForm.Logger.Log;
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
    Log log;
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
                log.WriteSys("AddMessage:" + sText);
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

    public ChatServer(int port, Log log) {
        this.port = port;
        this.log = log;
    }

    public boolean Auth(String Login, String Password){
        log.WriteSys("...AUTH" + Login+" "+Password);

        String pass= null;
        try {
            pass = dbConn.GetPasswordUser(Login.trim());
            log.WriteSys("Pass:" + pass);
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
            dbConn = new Conn(log);
            dbConn.CreateDB();
            log.WriteSys(dbConn.GetPasswordUser("oleg"));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try (ServerSocket serverSocket = new ServerSocket(port)) {

            log.WriteSys("Chat Server is listening on port " + port);
            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
           // ExecutorService executor = Executors.newFixedThreadPool(1);

            log.WriteSys("Start executer service");
            new Thread(()->{
                int Cnt = executor.getActiveCount();
                while (executor!=null) {
/*
                    if (Cnt!=executor.getActiveCount())
                    {
                        log.WriteSys("_____________________________________________________");
                        log.WriteSys("<<<<<<<" +  executor.getActiveCount() + ">>>>>>>>>>>>" +  executor.getPoolSize());
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
                log.WriteSys("--------");
                Socket socket = serverSocket.accept();
                log.WriteSys("New user connected");

                UserThread newUser = new UserThread(socket, this);
                //userThreads.add(newUser);
                executor.execute(newUser);
                log.WriteSys("NEW______" );
                log.WriteSys("END");

                //newUser.start();

            }

        } catch (IOException ex) {
            log.WriteSys("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    /**
     * Delivers a message from one user to others (broadcasting)
     */
    void broadcast(String message, UserThread aUser) {
        log.WriteSys("broadcast:");
        log.WriteSys(message);
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
            log.WriteSys( "RenameUserName:" + TC.UserName + "->" + TC.UserName );
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

            log.WriteSys("The user " + userName + " quitted");
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