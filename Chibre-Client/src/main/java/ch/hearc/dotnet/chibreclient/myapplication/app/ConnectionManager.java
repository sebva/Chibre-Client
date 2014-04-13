package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG = "ConnectionManager";
    private InputStream input;
    private OutputStream output;
    private boolean receiving = false;

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

    public void connectToServerAsync(InetAddress serverAddress, final ConnectionListener listener) {
        if(socket != null) {
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
            if(receiving == false)
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

    private void processData(Packet string) {
        Log.d(TAG, "Received: " + string);
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
}
