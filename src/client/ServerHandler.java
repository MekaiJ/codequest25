package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ServerHandler implements Runnable {
    private Socket serverConnection;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Rocket mainRocket, otherRocket;

    ServerHandler(Socket socket, Rocket mainRocket) throws IOException {
        this.serverConnection = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.mainRocket = otherRocket;
    }

    @Override
    public void run() {
        try {
            while(true) {
                Object fromServer = this.in.readObject();
                if(fromServer instanceof Rocket) {
                    otherRocket = (Rocket) fromServer;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void writeToServer(Object toWrite) throws IOException {
        this.out.reset();
        this.out.writeObject(toWrite);
        this.out.flush();
    }
}
