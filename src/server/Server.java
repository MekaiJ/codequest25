package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException, InterruptedException {
        ArrayList<ClientHandler> clients = new ArrayList<>();
        serverSocket = new ServerSocket(8888);
        System.out.println("Server started on port: " + serverSocket.getLocalPort());

        AcceptIncomingClients clientListener = new AcceptIncomingClients(clients);
        Thread incomingClients = new Thread(clientListener);
        incomingClients.start();

        while(true) {

            for(int i = 0; i < clients.size(); i++) {
                ClientHandler client = clients.get(i);
                if(client.getUpdate() != null && clients.size() == 2) {

                    clients.get((i == 0) ? 1 : 0).writeToClient(client.getUpdate());
                    client.wipeUpdate();
                }
            }
            Thread.sleep(2);
        }
    }

    static class AcceptIncomingClients implements Runnable {
        ArrayList<ClientHandler> clients;
        AcceptIncomingClients(ArrayList<ClientHandler> clients) {
            this.clients = clients;
        }

        @Override
        public void run(){
            try {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    if(clients.size() > 1) {
                        System.out.println("Refused Connection. 2 clients already connected");
                        clientSocket.close();
                        continue;
                    }
                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    Thread newclient = new Thread(handler);
                    newclient.start();
                    System.out.println("New client connected at " + clientSocket.getRemoteSocketAddress());
                }
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
