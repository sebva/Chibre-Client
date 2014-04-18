package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.UUID;

/**
 * Created by Sébastien on 03.04.2014.
 */
public class ConnectionManager {
    private static final int kPort = 24273;
    private static final String TAG = "ConnectionManager";
    private static final UUID uuid = UUID.randomUUID();
    private InputStream input;
    private OutputStream output;
    private boolean receiving = false;
    private Protocol protocol = null;

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

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void connectToServerAsync(String serverAddress, final ConnectionListener listener) {
        if(socket != null) {
            receiving = false;
            closeConnection();
        }
        socket = new Socket();
        final SocketAddress address = new InetSocketAddress(serverAddress, kPort);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    socket.connect(address, 3000);
                    input = socket.getInputStream();
                    output = socket.getOutputStream();

                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                listener.onConnectionResult(result);
            }
        }.execute();

    }

    public void setReceiving(boolean accept) {
        if(accept ^ receiving) {
            if(!receiving)
                startReceiving();
            else
                receiving = accept;
        }
    }

    private void startReceiving()
    {
        if (receiving)
            return;

        receiving = true;

        new Thread("TCP Reception")
        {
            byte[] lengthBuffer = new byte[4];

            @Override
            public void run() {
                sendHello();

                while (receiving) {
                    try {
                        if(input.available() < 4)
                            continue;

                        int length = input.read(lengthBuffer);

                        while(input.available() < length) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        length = Packet.byteArrayToInt(lengthBuffer);

                        byte[] buffer = new byte[length];
                        input.read(buffer);

                        Packet packet = new Packet(buffer, length);

                        processData(packet);

                    } catch (IOException e) {
                        Log.e(TAG, "Connection lost!");
                        break;
                    }
                }
            }
        }.start();
    }

    private void processData(Packet packet) {
        Log.d(TAG, "Received: " + packet);
        if(protocol != null)
            protocol.onPacketReceived(packet);
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
            output.write(packet.getPayloadLengthBytes());
            output.write(packet.getPayloadBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendHello() {
        try {
            JSONObject json = new JSONObject();
            json.put("action", "hello");
            json.put("uuid", uuid.toString());
            Packet packet = new Packet(json.toString());
            sendPacket(packet);
        }
        catch (Exception e) {}
    }

    public void playCard(Card card) {
        try {
            JSONObject json = new JSONObject();
            json.put("action", "play_card");
            JSONObject cardJson = new JSONObject();
            cardJson.put("value", card.getValue().toString());
            cardJson.put("color", card.getColor().toString());
            json.put("card", cardJson);
            Packet packet = new Packet(json.toString());
            sendPacket(packet);
        }
        catch (Exception e) {}
    }

    public void chooseAtout(Color atout) {
        try {
            JSONObject json = new JSONObject();
            json.put("action", "choose_atout");
            json.put("color", atout.toString());
            sendPacket(new Packet(json.toString()));
        }
        catch (Exception e) {}
    }

    public void chibre() {
        try {
            JSONObject json = new JSONObject();
            json.put("action", "choose_atout");
            json.put("color", "chibre");
            sendPacket(new Packet(json.toString()));
        }
        catch (Exception e) {}
    }
}
