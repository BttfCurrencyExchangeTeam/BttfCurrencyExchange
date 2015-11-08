package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mediaPlayer = MediaPlayer.create(this, R.raw.main);

        mediaPlayer.start();

        TextView new_game = (TextView) findViewById(R.id.new_game);
        Typeface face1 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        new_game.setTypeface(face1);

        TextView _continue_ = (TextView) findViewById(R.id.continue_);
        Typeface face2 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        _continue_.setTypeface(face2);

        TextView _options_ = (TextView) findViewById(R.id.options_);
        Typeface face3 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        _options_.setTypeface(face3);

        TextView _exit_ = (TextView) findViewById(R.id.exit_);
        Typeface face4 = Typeface.createFromAsset(getAssets(), "ds_digit_font.ttf");
        _exit_.setTypeface(face4);

        new_game.setOnClickListener(this);
        _exit_.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;

            case R.id.exit_:
                System.exit(0);


        }

    }
}
