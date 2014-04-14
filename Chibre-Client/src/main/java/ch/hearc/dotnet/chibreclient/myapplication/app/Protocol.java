package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sébastien on 13.04.2014.
 */
public class Protocol {
    private static final String TAG = "Protocol";
    private GameListener listener;

    public Protocol(GameListener listener) {
        this.listener = listener;
    }

    void onPacketReceived(Packet packet) {
        try {
            JSONObject json = new JSONObject(packet.getPayload());
            String action = json.getString("action");
            if("hello_reply".equals(action)) {
                int playerId = json.getInt("player_no");
                listener.onReceiveHello(playerId);
            }
            else if("refusal".equals(action)) {
                listener.onReceiveRefusal();
            }
            else if("distribution".equals(action)) {
                JSONArray cardsArray = json.getJSONArray("cards");
                boolean atout = json.getBoolean("atout");

                List<Card> cardList = new ArrayList<Card>(9);

                for(int i = 0; i < cardsArray.length(); i++) {
                    JSONObject card = cardsArray.getJSONObject(i);
                    Value value = Value.valueOf(card.getString("value"));
                    Color color = Color.valueOf(card.getString("color"));
                    Card cardObject = Card.getCard(color, value);
                    cardList.add(cardObject);
                }

                listener.onReceiveCards(cardList, atout);
            }
            else if("time_to_play".equals(action)) {
                List<Card> cardList = new ArrayList<Card>(9);
                JSONArray cardsArray = json.getJSONArray("possible_cards");
                for(int i = 0; i < cardsArray.length(); i++) {
                    JSONObject cardJson = cardsArray.getJSONObject(i);

                    Color color = Color.valueOf(cardJson.getString("color"));
                    Value value = Value.valueOf(cardJson.getString("value"));
                    cardList.add(Card.getCard(color, value));
                }
                listener.onReceiveTimeToPlay(cardList);
            }
            else
                Log.w(TAG, "Unknown action received");
        } catch (JSONException e) {
            Log.w(TAG, "Non-JSON payload received");
        }
    }
}
