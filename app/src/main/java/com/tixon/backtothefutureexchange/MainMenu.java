package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity implements View.OnTouchListener {

    MediaPlayer mediaPlayer;

    ImageView top, bottom;

    TextView tvNew_game, tvContinue, tvOptions, tvExit;

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

        tvNew_game = (TextView) findViewById(R.id.new_game);
        Typeface face1 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        tvNew_game.setTypeface(face1);

        tvContinue = (TextView) findViewById(R.id.continue_);
        Typeface face2 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        tvContinue.setTypeface(face2);

        tvOptions = (TextView) findViewById(R.id.options_);
        Typeface face3 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        tvOptions.setTypeface(face3);

        tvExit = (TextView) findViewById(R.id.exit_);
        Typeface face4 = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        tvExit.setTypeface(face4);

        tvNew_game.setOnTouchListener(this);
        tvContinue.setOnTouchListener(this);
        tvOptions.setOnTouchListener(this);
        tvExit.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.new_game:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    tvNew_game.setTextColor(Color.parseColor("#67abff"));
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    tvNew_game.setTextColor(Color.parseColor("#ffffff"));
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.continue_:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    tvContinue.setTextColor(Color.parseColor("#67abff"));
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    tvContinue.setTextColor(Color.parseColor("#ffffff"));
                }
                break;
            case R.id.options_:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    tvOptions.setTextColor(Color.parseColor("#67abff"));
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    tvOptions.setTextColor(Color.parseColor("#ffffff"));
                }
                break;
            case R.id.exit_:
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    tvExit.setTextColor(Color.parseColor("#67abff"));
                } else if(event.getAction() == MotionEvent.ACTION_UP) {
                    tvExit.setTextColor(Color.parseColor("#ffffff"));
                }
        }
        return true;
    }
}
