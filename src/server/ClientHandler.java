package server;

import java.io.*;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Object update;

    ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new ObjectInputStream(socket.getInputStream());
        this.out = new ObjectOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object fromClient = this.in.readObject();
                this.update = fromClient;
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
        this.update = null;
    }
}
