package server;

import client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static ServerSocket serverSocket;
    private static ArrayList<ClientHandler> clients;

    public static void main(String[] args) throws IOException {
        clients = new ArrayList<>();
        serverSocket = new ServerSocket(8888);
        System.out.println("Server started on port: " + serverSocket.getLocalPort());

        AcceptIncomingClients clientListener = new AcceptIncomingClients(clients);
        Thread incomingClients = new Thread(clientListener);
        incomingClients.start();

    }

    static class AcceptIncomingClients implements Runnable {
        ArrayList<ClientHandler> clients;
        AcceptIncomingClients(ArrayList<ClientHandler> clients) {
            this.clients = clients;
        }

        @Override
        public void run(){
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    Thread newclient = new Thread(handler);

                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
