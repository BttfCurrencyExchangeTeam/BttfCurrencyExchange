package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    //MediaPlayer mediaPlayer;

    ImageView top, bottom;

    TextView tvNew_game, tvContinue, tvOptions, tvAbout, tvExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //mediaPlayer = MediaPlayer.create(this, R.raw.main_theme);
        //mediaPlayer.start();

        //инициализация ImageView
        top = (ImageView) findViewById(R.id.iv_name);
        bottom = (ImageView) findViewById(R.id.iv_car);

        //antialiasing for ImageView
        top.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        bottom.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //инициализация TextView
        tvNew_game = (TextView) findViewById(R.id.new_game);
        tvContinue = (TextView) findViewById(R.id.continue_);
        tvOptions = (TextView) findViewById(R.id.options_);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvExit = (TextView) findViewById(R.id.exit_);

        //устанавливаем шрифты
        Typeface digitsTypeFace = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        tvNew_game.setTypeface(digitsTypeFace);
        tvContinue.setTypeface(digitsTypeFace);
        tvOptions.setTypeface(digitsTypeFace);
        tvAbout.setTypeface(digitsTypeFace);
        tvExit.setTypeface(digitsTypeFace);

        //устанавливаем onTouchListener
        tvNew_game.setOnTouchListener(this);
        tvContinue.setOnTouchListener(this);
        tvOptions.setOnTouchListener(this);
        tvAbout.setOnTouchListener(this);
        tvExit.setOnTouchListener(this);

        //устанавливаем onClickListener
        tvNew_game.setOnClickListener(this);
        tvContinue.setOnClickListener(this);
        tvOptions.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvExit.setOnClickListener(this);
    }

    //функция установки цвета для TextView по касанию
    private void touch(TextView textView, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            textView.setTextColor(Color.parseColor("#67abff"));
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            textView.setTextColor(Color.parseColor("#ffffff"));
            onClick(textView);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.new_game:
                touch(tvNew_game, event);
                break;
            case R.id.continue_:
                touch(tvContinue, event);
                break;
            case R.id.options_:
                touch(tvOptions, event);
                break;
            case R.id.tv_about:
                touch(tvAbout, event);
                break;
            case R.id.exit_:
                touch(tvExit, event);
                System.exit(0);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_game:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_about:
                Intent intentStartAboutActivity = new Intent(this, AboutActivity.class);
                startActivity(intentStartAboutActivity);
            default: break;
        }
    }
}
