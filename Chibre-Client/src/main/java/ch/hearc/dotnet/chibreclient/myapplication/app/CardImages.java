package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * Created by Sébastien on 14.04.2014.
 */
public class CardImages {
    public static int getImageIdForCard(Context context, Card card) {
        String filename = card.getColor().toString().toLowerCase() + "_" + card.getValue().toString().toLowerCase();
        return context.getResources().getIdentifier(filename, "drawable", CardImages.class.getPackage().getName());
    }

    public static Drawable getImageForCard(Context context, Card card) {
        return context.getResources().getDrawable(getImageIdForCard(context, card));
    }
}
