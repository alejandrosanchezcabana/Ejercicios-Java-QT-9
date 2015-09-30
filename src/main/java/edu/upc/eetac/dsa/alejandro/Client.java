package edu.upc.eetac.dsa.alejandro;

/**
 * Hello world!
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client
{
    private class ReaderThread implements Runnable {
        BufferedReader reader = null;

        public ReaderThread(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            String msg = null;
            try {
                while ((msg = reader.readLine()) != null)
                    System.out.println(msg);
            } catch (IOException e) {
                if (!socket.isClosed())
                    e.printStackTrace();
            }
        }
    }

    private String server;
    private int port;
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    public Client(String server) {
        this(server, Server.DEFAULT_PORT);
    }

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter your username: ");
            String username = reader.readLine();
            join(username);

            System.out.println("start chat");
            String msg = null;
            while ((msg = reader.readLine()).length() != 0) {
                send(msg);
            }
            leave();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void join(String username) throws IOException {
        socket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        (new ServerThread((new ReaderThread(reader)))).start();
        writer = new PrintWriter(socket.getOutputStream());

        writer.println("JOIN " + username);
        writer.flush();
    }

    private void leave() throws IOException {
        writer.println("LEAVE");
        writer.flush();

        socket.close();
    }

    private void send(String msg) {
        writer.println("MESSAGE " + msg);
        writer.flush();
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("You have to pass the server name and the server port");
            System.exit(-1);
        }
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        Client client = new Client(server, port);
        (new ServerThread(client)).start();
    }
}
