package ch.hearc.dotnet.chibreclient.myapplication.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.widget.Button;
import android.widget.ImageButton;
import ch.hearc.dotnet.chibreclient.myapplication.app.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class GameActivity extends Activity implements View.OnClickListener {

    private List<ImageButton> cardsButtons;
    private Game game;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private int playerId;
    private ConnectionManager connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        final int[] buttons = new int[] { R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.card5, R.id.card6, R.id.card7, R.id.card8, R.id.card9 };

        connectionManager = ConnectionManager.getInstance();
        Protocol protocol = new Protocol(new GameAdapter() {

            @Override
            public void onReceiveCards(final List<Card> cards, final boolean isAtout) {
                game.cardList = cards;
                GameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((Button) findViewById(R.id.atout_button)).setEnabled(isAtout);
                        for(int i = 1; i <= 9; i++) {
                            ImageButton cardButton = ((ImageButton) findViewById(buttons[i - 1]));
                            cardButton.setImageResource(CardImages.getImageIdForCard(GameActivity.this, cards.get(i - 1)));
                            enableDisable(cardButton, false);
                            cardButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            @Override
            public void onReceiveTimeToPlay(final List<Card> possibleCards) {
                GameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 1; i <= 9; i++) {
                            Card card = game.cardList.get(i - 1);
                            ImageButton cardButton = ((ImageButton) findViewById(buttons[i - 1]));
                            boolean isPossible = possibleCards.contains(card);
                            enableDisable(cardButton, isPossible);
                        }
                    }
                });
            }
        });
        connectionManager.setProtocol(protocol);
        connectionManager.setReceiving(true);
        
        playerId = getIntent().getIntExtra("playerId", -2) + 1;

        getActionBar().setSubtitle(getString(R.string.player) + " " + playerId);

        game = new Game();

        cardsButtons = new ArrayList<ImageButton>(9);
        for(int button : buttons)
        {
            ImageButton ib = (ImageButton) findViewById(button);
            ib.setImageResource(CardImages.getImageIdForCard(this, Card.getCard(Color.pique, Value.dame)));
            enableDisable(ib, false);
            ib.setOnClickListener(this);
            cardsButtons.add(ib);
        }

        Button atout_choose = (Button) findViewById(R.id.atout_button);
        atout_choose.setOnClickListener(this);
    }

    private void disableEverything() {
        for(ImageButton btn : cardsButtons) {
            enableDisable(btn, false);
        }
        ((Button) findViewById(R.id.atout_button)).setEnabled(false);
    }

    private void enableDisable(ImageButton btn, boolean enabled) {
        if(enabled)
            btn.setColorFilter(android.graphics.Color.TRANSPARENT);
        else {
            int color = android.graphics.Color.argb(100, 0, 0, 0);
            btn.setColorFilter(color);
        }

        btn.setEnabled(enabled);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.atout_button) {
            final Button atout_button = (Button) v;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            String[] colors = new String[5];
            colors[0] = getString(R.string.chibre);
            int i = 1;
            for(Color color : Color.values()) {
                String colorStr = Character.toUpperCase(color.toString().charAt(0)) + color.toString().substring(1);
                colors[i++] = colorStr;
            }

            dialogBuilder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0)
                        game.chibre();
                    else {
                        Color color = Color.values()[which -1];
                        game.chooseAtout(color);
                    }
                    disableEverything();
                }
            });

            dialogBuilder.create().show();
        }
        else {
            int cardId;
            switch (v.getId()) {
                case R.id.card1:
                    cardId = 0;
                    break;
                case R.id.card2:
                    cardId = 1;
                    break;
                case R.id.card3:
                    cardId = 2;
                    break;
                case R.id.card4:
                    cardId = 3;
                    break;
                case R.id.card5:
                    cardId = 4;
                    break;
                case R.id.card6:
                    cardId = 5;
                    break;
                case R.id.card7:
                    cardId = 6;
                    break;
                case R.id.card8:
                    cardId = 7;
                    break;
                case R.id.card9:
                    cardId = 8;
                    break;
                default:
                    cardId = -1;
                    break;
            }
            if (cardId != -1) {
                v.setVisibility(View.INVISIBLE);
                game.playCard(cardId);
                disableEverything();
            }
        }
    }
}
