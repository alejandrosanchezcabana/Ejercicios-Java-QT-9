package edu.upc.eetac.dsa.alejandro;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * Created by Alex on 30/9/15.
 */
public class Server {
    private int port;
    private ServerThread thread;

    public final static int DEFAULT_PORT = 3333;

    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        if (thread == null)
            (thread = new ServerThread(this, "Server main thread")).start();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Chat server up, listening at " + port);
            while (true) {
                Socket socket = ss.accept();
                (new ServerThread(new ServerThread(socket))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = (args.length == 1) ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        Server server = new Server(port);
        server.startServer();
    }
}
