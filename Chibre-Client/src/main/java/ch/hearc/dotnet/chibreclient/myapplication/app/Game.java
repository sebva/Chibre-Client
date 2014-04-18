package ch.hearc.dotnet.chibreclient.myapplication.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sébastien on 03.04.2014.
 */
public class Game {
    List<Card> cardList;

    public Game() {
        this.cardList = new ArrayList<Card>(9);
        cm = ConnectionManager.getInstance();
    }
    private ConnectionManager cm;

    public void playCard(int cardId) {
        cm.playCard(cardList.get(cardId));
    }

    public void chooseAtout(Color atout) {
        cm.chooseAtout(atout);
    }

    public void chibre() {
        cm.chibre();
    }

}
