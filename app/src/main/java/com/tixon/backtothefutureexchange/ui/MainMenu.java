package com.tixon.backtothefutureexchange.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;



import com.tixon.backtothefutureexchange.Constants;

public class MainMenu extends AppCompatActivity
{
    private Button new_game_b1, continue_b2, options_b3, exit_b4;

    private void SetTypeface()
    {
        new_game_b1.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        continue_b2.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        options_b3.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));
        exit_b4.setTypeface(Typeface.createFromAsset(getResources().getAssets(), Constants.TYPEFACE_DIGITS));

    }
    private void SetColor()
    {
        new_game_b1.setTextColor(ControlPanelItem.GREEN);
        continue_b2.setTextColor(ControlPanelItem.GREEN);
        options_b3.setTextColor(ControlPanelItem.GREEN);
        exit_b4.setTextColor(ControlPanelItem.GREEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }
}
