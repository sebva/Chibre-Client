package ch.hearc.dotnet.chibreclient.myapplication.app;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Sébastien on 03.04.2014.
 */
public class ConnectionManager {
    private static final int kPort = 24273;
    private Reader input;
    private Writer output;

    public static interface ConnectionListener {
        public void onConnectionResult(boolean connectionResult);
    }

    private static ConnectionManager instance = null;
    private Socket socket = null;

    public static ConnectionManager getInstance() {
        if(instance == null)
            instance = new ConnectionManager();

        return instance;
    }

    private ConnectionManager() {

    }

    public void connectToServer(InetAddress serverAddress, ConnectionListener listener) {
        if(socket != null) {
            closeConnection();
        }
        socket = new Socket();
        SocketAddress address = new InetSocketAddress(serverAddress, kPort);
        try {
            socket.connect(address, 3000);
            this.input = new InputStreamReader(socket.getInputStream());
            this.output = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    public void sendPacket(Packet packet) {
        try {
            output.write(packet.getPayload());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
