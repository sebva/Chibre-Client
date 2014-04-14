package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sébastien on 03.04.2014.
 */
public class Card {
    private static Map<Pair<Color, Value>, Card> cards;
    private Color color;
    private Value value;

    static {
        cards = new HashMap<Pair<Color, Value>, Card>(36);
    }

    private Card(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    public static Card getCard(Color color, Value value) {
        Pair<Color, Value> key = new Pair<Color, Value>(color, value);
        if(!cards.containsKey(key)) {
            Card card = new Card(color, value);
            cards.put(key, card);
        }
        return cards.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof Card)
        {
            Card card = (Card) o;
            return value.equals(card.value) && color.equals(card.color);
        }
        else
            return false;
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }
}
