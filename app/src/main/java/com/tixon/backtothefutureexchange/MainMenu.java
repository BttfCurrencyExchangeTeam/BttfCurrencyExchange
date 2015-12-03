package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.content.SharedPreferences;
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

    TextView tvNew_game, tvContinue, tvAbout, tvExit;

    private boolean isGameGoing = false;
    private int colorEnabled, colorDisabled;

    private void setGameGoing() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.KEY_GAME_GOING, true);
        editor.apply();
    }

    private boolean getGameStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constants.KEY_GAME_GOING, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            isGameGoing = getGameStatus();
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(isGameGoing) {
            tvContinue.setTextColor(colorEnabled);
        } else {
            tvContinue.setTextColor(colorDisabled);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //mediaPlayer = MediaPlayer.create(this, R.raw.main_theme);
        //mediaPlayer.start();

        colorEnabled = getResources().getColor(R.color.main_menu_color);
        colorDisabled = getResources().getColor(R.color.main_menu_disabled_color);

        //инициализация ImageView
        top = (ImageView) findViewById(R.id.iv_name);
        bottom = (ImageView) findViewById(R.id.iv_car);

        //antialiasing for ImageView
        top.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        bottom.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        //инициализация TextView
        tvNew_game = (TextView) findViewById(R.id.new_game);
        tvContinue = (TextView) findViewById(R.id.continue_);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvExit = (TextView) findViewById(R.id.exit_);

        //устанавливаем шрифты
        Typeface digitsTypeFace = Typeface.createFromAsset(getAssets(), Constants.TYPEFACE_DIGITS);
        tvNew_game.setTypeface(digitsTypeFace);
        tvContinue.setTypeface(digitsTypeFace);
        tvAbout.setTypeface(digitsTypeFace);
        tvExit.setTypeface(digitsTypeFace);

        //устанавливаем onTouchListener
        tvNew_game.setOnTouchListener(this);
        tvContinue.setOnTouchListener(this);
        tvAbout.setOnTouchListener(this);
        tvExit.setOnTouchListener(this);

        //устанавливаем onClickListener
        tvNew_game.setOnClickListener(this);
        tvContinue.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvExit.setOnClickListener(this);
    }

    //функция установки цвета для TextView по касанию
    private void touch(TextView textView, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            textView.setTextColor(getResources().getColor(R.color.main_menu_pressed_button_color));
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            textView.setTextColor(getResources().getColor(R.color.main_menu_color));
            onClick(textView);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.new_game:
                touch(tvNew_game, event);
                setGameGoing();
                break;
            case R.id.continue_:
                if(isGameGoing) {
                    touch(tvContinue, event);
                }
                break;
            case R.id.tv_about:
                touch(tvAbout, event);
                break;
            case R.id.exit_:
                touch(tvExit, event);
                onBackPressed();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent startMainActivityIntent = new Intent(this, MainActivity.class);
        switch (v.getId()) {
            case R.id.new_game:
                startMainActivityIntent.putExtra(Constants.KEY_NEW_OR_CONTINUE, Constants.KEY_NEW);
                startMainActivityIntent.putExtra(Constants.KEY_FROM_MENU, true);
                startActivity(startMainActivityIntent);
                break;
            case R.id.continue_:
                if(isGameGoing) {
                    startMainActivityIntent.putExtra(Constants.KEY_NEW_OR_CONTINUE, Constants.KEY_CONTINUE);
                    startMainActivityIntent.putExtra(Constants.KEY_FROM_MENU, true);
                    startActivity(startMainActivityIntent);
                }
                break;
            case R.id.tv_about:
                Intent intentStartAboutActivity = new Intent(this, AboutActivity.class);
                startActivity(intentStartAboutActivity);
            default: break;
        }
    }
}
