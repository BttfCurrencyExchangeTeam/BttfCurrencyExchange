package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer mediaPlayer;

    ImageView top, bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //mediaPlayer = MediaPlayer.create(this, R.raw.main);

        //mediaPlayer.start();

        top = (ImageView) findViewById(R.id.iv_name);
        top.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        bottom = (ImageView) findViewById(R.id.iv_car);
        bottom.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        TextView new_game = (TextView) findViewById(R.id.new_game);
        Typeface face1 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        new_game.setTypeface(face1);

        TextView _continue_ = (TextView) findViewById(R.id.continue_);
        Typeface face2 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        _continue_.setTypeface(face2);

        TextView _options_ = (TextView) findViewById(R.id.options_);
        Typeface face3 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        _options_.setTypeface(face3);

        TextView _exit_ = (TextView) findViewById(R.id.exit_);
        Typeface face4 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        _exit_.setTypeface(face4);

        new_game.setOnClickListener(this);
        _exit_.setOnClickListener(this);
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
