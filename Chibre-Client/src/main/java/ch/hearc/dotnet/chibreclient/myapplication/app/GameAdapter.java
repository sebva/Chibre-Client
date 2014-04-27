package ch.hearc.dotnet.chibreclient.myapplication.app;

import java.util.List;

/**
 * Created by Sébastien on 13.04.2014.
 */
public class GameAdapter implements GameListener {

    @Override
    public void onReceiveHello(int playerId) {

    }

    @Override
    public void onReceiveCards(List<Card> cards, boolean isAtout) {

    }

    @Override
    public void onReceiveRefusal() {

    }

    @Override
    public void onReceiveTimeToPlay(List<Card> possibleCards) {

    }

    @Override
    public void onReceiveGoodBye() {
        
    }
}
