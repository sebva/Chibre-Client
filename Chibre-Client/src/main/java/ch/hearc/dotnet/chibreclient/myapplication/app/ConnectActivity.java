package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Text;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class ConnectActivity extends ActionBarActivity implements ConnectionManager.ConnectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_connect:
                try {
                    connectToServer();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void connectToServer() throws UnknownHostException {
        TextView serverIp = (TextView) findViewById(R.id.server_ip);
        final InetAddress ip = InetAddress.getByName(serverIp.getText().toString());
        final ConnectionManager connectionManager = ConnectionManager.getInstance();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                connectionManager.connectToServer(ip, ConnectActivity.this);
                return null;
            }
        }.execute();
    }

    @Override
    public void onConnectionResult(boolean connectionResult) {
        Toast.makeText(this, String.valueOf(connectionResult), Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ConnectionManager connectionManager = ConnectionManager.getInstance();
                Packet packet = new Packet("Useful message");
                for(int i = 0; i < 100; i++)
                {
                    connectionManager.sendPacket(packet);
                }
                return null;
            }
        }.execute();
    }
}