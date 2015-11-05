package com.tixon.backtothefutureexchange.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.tixon.backtothefutureexchange.Constants;
import com.tixon.backtothefutureexchange.R;

public class MainMenu extends AppCompatActivity {



    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        TextView tv = (TextView) findViewById(R.id.ng1);
        Typeface face = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv.setTypeface(face);

        TextView tv1 = (TextView) findViewById(R.id.ct2);
        Typeface face1 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv1.setTypeface(face1);

        TextView tv2 = (TextView) findViewById(R.id.op3);
        Typeface face2 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv2.setTypeface(face2);

        TextView tv3 = (TextView) findViewById(R.id.ex4);
        Typeface face3 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv3.setTypeface(face3);
    }
}
