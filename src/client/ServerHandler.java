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
        this.mainRocket = mainRocket;
        this.otherRocket = new Rocket(RocketLevel.LEVEL_1);
    }

    @Override
    public void run() {
        try {
            while(true) {
                Object fromServer = this.in.readObject();
                if(fromServer instanceof RocketData) {
                    System.out.println("recieving from server: " + ((RocketData) fromServer).x + " " + ((RocketData) fromServer).y + " " + ((RocketData) fromServer).level);
                    otherRocket.setX(((RocketData) fromServer).x);
                    otherRocket.setY(((RocketData) fromServer).y);
                    otherRocket.setLevel(((RocketData) fromServer).level);
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

    Rocket getOtherRocket() {
        return this.otherRocket;
    }
}
