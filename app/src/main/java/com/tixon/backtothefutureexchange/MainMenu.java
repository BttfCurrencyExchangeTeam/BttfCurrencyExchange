package com.tixon.backtothefutureexchange;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        TextView tv1 = (TextView) findViewById(R.id.new_game);
        Typeface face1 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv1.setTypeface(face1);

        TextView tv2 = (TextView) findViewById(R.id.continue_);
        Typeface face2 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv2.setTypeface(face2);

        TextView tv3 = (TextView) findViewById(R.id.options_);
        Typeface face3 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv3.setTypeface(face3);

        TextView tv4 = (TextView) findViewById(R.id.exit_);
        Typeface face4 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        tv4.setTypeface(face4);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
