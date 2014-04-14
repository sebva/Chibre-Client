package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
        final ConnectionManager connectionManager = ConnectionManager.getInstance();
        Protocol protocol = new Protocol(new GameAdapter() {
            @Override
            public void onReceiveHello(int playerId) {
                Intent intent = new Intent(ConnectActivity.this, GameActivity.class);
                intent.putExtra("playerId", playerId);
                startActivity(intent);
            }

            @Override
            public void onReceiveRefusal() {
                Toast.makeText(ConnectActivity.this, R.string.too_much_players, Toast.LENGTH_SHORT).show();
            }
        });
        connectionManager.setProtocol(protocol);

        connectionManager.connectToServerAsync(serverIp.getText().toString(), ConnectActivity.this);
    }

    @Override
    public void onConnectionResult(boolean connectionResult) {
        final ConnectionManager connectionManager = ConnectionManager.getInstance();
        connectionManager.setReceiving(connectionResult);
    }
}
