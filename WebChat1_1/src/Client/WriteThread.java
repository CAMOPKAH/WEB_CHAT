package Client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class WriteThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;
    public boolean Run=true;

    public WriteThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void SendCommand(CommandProtocol cmd, String Text)
    {
        writer.println(cmd.GenMessage(Text));
    }

    public void SendText(String Text)
    {
        writer.println(Text);
    }
    public boolean IsClose()
    {

        return client.isClose();
    }
    public void Stop()
    {
        System.out.println("CLOSE " + this.getClass());
        this.Run=false;
    }

    public void run() {

        String text="1";

        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (!text.equals("bye") && this.Run && !IsClose());

        writer.println("bye");
        writer.close();
    }
}