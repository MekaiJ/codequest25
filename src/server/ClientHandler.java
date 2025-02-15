package server;

import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Object update = null;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object fromClient = this.in.readObject();
                //System.out.println("Recieving From CLient");
                update = fromClient;
            }
        }catch (Exception e) {}
    }

    void writeToClient(Object toWrite) throws IOException {
        this.out.reset();
        this.out.writeObject(toWrite);
        this.out.flush();
    }

    Object getUpdate() {
        return update;
    }

    void wipeUpdate() {
        update = null;
    }
}
