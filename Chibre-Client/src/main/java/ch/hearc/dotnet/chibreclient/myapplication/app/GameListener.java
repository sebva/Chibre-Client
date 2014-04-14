package ch.hearc.dotnet.chibreclient.myapplication.app;

import java.util.List;

/**
 * Created by Sébastien on 13.04.2014.
 */
public interface GameListener {
    public void onReceiveHello(int playerId);
    public void onReceiveCards(List<Card> cards, boolean isAtout);
    public void onReceiveRefusal();
    public void onReceiveTimeToPlay(List<Card> possibleCards);
}
