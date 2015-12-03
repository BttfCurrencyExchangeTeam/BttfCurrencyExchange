package com.tixon.backtothefutureexchange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tixon.backtothefutureexchange.fragments.FragmentAddResources;
import com.tixon.backtothefutureexchange.ui.ControlPanelItem;
import com.tixon.backtothefutureexchange.ui.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMoneyChangedListener,
        OnItemCheckedListener,
        OnAddDepositItemClickListener,
        OnDepositAddListener,
        OnMoneyWithdrawListener,
        OnAddResourcesListener,
        OnLevelIncreasedListener {

    private static final String LOG_TAG = "myLogs";
    SharedPreferences sp;

    Button bTravel, bExchange;
    FrameLayout container;
    RelativeLayout purseHeader;
    Toolbar resourcesToolbar;
    ControlPanelItem mainPresentTimePanel; //панель времени, показывает, когда мы в данный момент
    RecyclerView purseRecyclerView, depositsRecyclerView;

    LinearLayoutManager purseLayoutManager, depositsLayoutManager;
    PurseItemsRecyclerAdapter purseAdapter;
    DepositRecyclerAdapter depositsAdapter;

    private boolean level1done = false, level2done = false, level3done = false;
    private boolean isGameStarted = false;

    private List<OnTimeTravelListener> onTimeTravelListenersList = new ArrayList<>();

    public void addOnTimeTravelListener(OnTimeTravelListener listener) {
        onTimeTravelListenersList.add(listener);
    }
    public void notifyTimeTravelled() {
        for(OnTimeTravelListener listener: onTimeTravelListenersList) {
            listener.onTimeTraveled();
        }
    }

    //onLevelIncreased listener
    private OnLevelIncreasedListener onLevelIncreasedListener;

    public void setOnLevelIncreasedListener(OnLevelIncreasedListener listener) {
        this.onLevelIncreasedListener = listener;
    }

    private FragmentChange fragmentChange;
    private AddDepositFragment addDepositFragment;

    private Calendar calendarPresent, calendarLast, calendarDestination;
    Calendar calendar;

    private Purse purse;
    private Delorean delorean;
    private Bank bank;

    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            isGameStarted = savedInstanceState.getBoolean("isGameStarted");
        } catch (Exception e) {
            Log.e(LOG_TAG, "restore instance state: isGameStarted error: " + e.getMessage());
            e.printStackTrace();
        }

        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();

        long presentTime = System.currentTimeMillis();

        calendar = Calendar.getInstance();
        calendarDestination = Calendar.getInstance();
        calendarPresent = Calendar.getInstance();
        calendarLast = Calendar.getInstance();

        calendar.setTimeInMillis(presentTime);
        calendarPresent.setTimeInMillis(presentTime);
        calendarDestination.setTimeInMillis(presentTime);
        calendarLast.setTimeInMillis(presentTime);

        delorean = Delorean.getInstance();
        bank = Bank.getInstance(getResources().getStringArray(R.array.dollars),
                getResources().getStringArray(R.array.pounds));
        bank.setCurrency(Bank.CURRENCY_DOLLARS);
        purse = Purse.getInstance();

        //установить стартовую сумму в $1000
        purse.init();

        //инициализация элементов разметки
        initViews();

        //добавления слушателя на повышение уровня
        setOnLevelIncreasedListener(this);

        //добавление слушателей на перемещение во времени
        addOnTimeTravelListener(delorean);
        addOnTimeTravelListener(resourcesToolbar);

        resourcesToolbar.setPlutoniumNumber(delorean.getPlutonium());
        resourcesToolbar.setFuelNumber(delorean.getFuel());

        // установка и настройка адаптеров

        purseAdapter = new PurseItemsRecyclerAdapter(this, purse.getPurse(), presentTime);
        //добавление слушателей об изменении валюты в MainActivity -> purseAdapter
        purseAdapter.addOnCurrencyChangedListener(bank);
        purseAdapter.addOnCurrencyChangedListener(fragmentChange);

        //установка валюты для класса личных сбережений
        purseAdapter.selectCurrency(bank.getCurrency(), presentTime);

        purseRecyclerView.setAdapter(purseAdapter);
        purseRecyclerView.getLayoutParams().height = getResources()
                .getDimensionPixelSize(R.dimen.purse_item_height) * purse.getPurse().length;
        purseRecyclerView.setHasFixedSize(true);


        depositsAdapter = new DepositRecyclerAdapter(bank, presentTime);
        depositsRecyclerView.setAdapter(depositsAdapter);
        updateDepositsRecyclerHeight(presentTime);
        depositsRecyclerView.setHasFixedSize(true);
        depositsAdapter.setOnAddDepositListener(this);
        depositsAdapter.setOnMoneyWithdrawListener(this);

        //создание или продолжение игры

        Intent fromMainMenu = getIntent();
        if(!isGameStarted) {
            switch (fromMainMenu.getIntExtra(Constants.KEY_NEW_OR_CONTINUE, Constants.KEY_NEW)) {
                case Constants.KEY_NEW:
                    long currentSystemTime;
                    currentSystemTime = System.currentTimeMillis();
                    //создание новой игры
                    //формирование записей базы данных для сохранения игры
                    TaskStartNewGame taskStartNewGame = new TaskStartNewGame();
                    taskStartNewGame.execute(currentSystemTime);
                    break;
                case Constants.KEY_CONTINUE:
                    long savedTime = getSavedTime();
                    Log.d(LOG_TAG, "savedTime = " + savedTime);
                    if(savedTime != Constants.SAVED_TIME_DEFAULT) {
                        //продолжение игры со считанными параметрами
                        TaskReadGameData taskReadGameData = new TaskReadGameData();
                        taskReadGameData.execute(savedTime);
                    } else {
                        //если параметров нет, начать новую игру
                        // и сформировать таблицы
                        currentSystemTime = System.currentTimeMillis();
                        TaskStartNewGame taskAnywayStartNewGame = new TaskStartNewGame();
                        taskAnywayStartNewGame.execute(currentSystemTime);
                    }
                    break;
            }
            isGameStarted = true;
        } else {
            long savedTime = getSavedTime();
            Log.d(LOG_TAG, "savedTime = " + savedTime);
            if(savedTime != Constants.SAVED_TIME_DEFAULT) {
                //продолжение игры со считанными параметрами
                TaskReadGameData taskReadGameData = new TaskReadGameData();
                taskReadGameData.execute(savedTime);
            } else {
                //если параметров нет, начать новую игру
                // и сформировать таблицы
                long currentSystemTime = System.currentTimeMillis();
                TaskStartNewGame taskAnywayStartNewGame = new TaskStartNewGame();
                taskAnywayStartNewGame.execute(currentSystemTime);
            }
        }

        updateDepositsRecyclerHeight(presentTime);

        mainPresentTimePanel.setDate(calendarPresent);
        mainPresentTimePanel.startTimeRoll();

        //создание фрагментов
        fragmentChange = FragmentChange.newInstance(bank, purse, calendarPresent);
        addDepositFragment = AddDepositFragment.newInstance(bank, purse,
                calendarPresent.getTimeInMillis(), generateRandomInterest());

        //слушатели на нажатие кнопки создания вклада
        addDepositFragment.addOnDepositAddListener(this);
        addDepositFragment.addOnDepositAddListener(purse);

        //слушатель изменения значения денег
        addDepositFragment.setOnMoneyChangedListener(this);
    }

    //todo: сохранение данных
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isGameStarted", isGameStarted);
    }

    //сохранение игры в onStop
    @Override
    protected void onStop() {
        super.onStop();
        long gameSavedTime = getSavedTime();
        //обновление данных игры
        TaskUpdateGameData taskUpdateGameData = new TaskUpdateGameData();
        taskUpdateGameData.execute(gameSavedTime, mainPresentTimePanel.getDate().getTimeInMillis());
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if(count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void initViews() {
        container = (FrameLayout) findViewById(R.id.main_container);
        purseHeader = (RelativeLayout) findViewById(R.id.purse_header_frame);
        resourcesToolbar = (Toolbar) findViewById(R.id.resources_toolbar);

        bTravel = (Button) findViewById(R.id.main_activity_button_travel);
        bExchange = (Button) findViewById(R.id.main_activity_button_exchange);

        mainPresentTimePanel = (ControlPanelItem) findViewById(R.id.main_activity_present_panel);
        mainPresentTimePanel.setPanelType(ControlPanelItem.PRESENT_TIME);

        purseRecyclerView = (RecyclerView) findViewById(R.id.purse_recycler_view);
        purseLayoutManager = new LinearLayoutManager(this);
        purseRecyclerView.setLayoutManager(purseLayoutManager);

        depositsRecyclerView = (RecyclerView) findViewById(R.id.deposits_recycler_view);
        depositsLayoutManager = new LinearLayoutManager(this);
        depositsRecyclerView.setLayoutManager(depositsLayoutManager);

        bTravel.setOnClickListener(this);
        bExchange.setOnClickListener(this);

        resourcesToolbar.setOnPlutoniumClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "all cash in dollars = " + purse.getAllCash(bank, calendarPresent.getTimeInMillis()));
                showAddResourcesFragment(Constants.RESOURCE_TYPE_PLUTONIUM);
            }
        });

        resourcesToolbar.setOnFuelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddResourcesFragment(Constants.RESOURCE_TYPE_FUEL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_TRAVEL:
                    calendarPresent.setTimeInMillis(data.getLongExtra(Constants.KEY_TIME_DESTINATION, System.currentTimeMillis()));
                    calendarDestination.setTimeInMillis(data.getLongExtra(Constants.KEY_TIME_DESTINATION, System.currentTimeMillis()));
                    calendarLast.setTimeInMillis(data.getLongExtra(Constants.KEY_TIME_LAST, System.currentTimeMillis()));

                    //уведомить о перемещении во времени
                    notifyTimeTravelled();

                    Log.d("myLogs", "currentYear = " + calendarPresent.get(Calendar.YEAR) +
                            ", lastYear = " + calendarLast.get(Calendar.YEAR));
                    //обновить время в адаптере вкладов
                    depositsAdapter.updateCurrentTime(calendarPresent.getTimeInMillis());
                    //depositsAdapter.updateDeposits(bank.getDeposits(calendarPresent.getTimeInMillis()));
                    updateDepositsRecyclerHeight(calendarPresent.getTimeInMillis());
                    //обновить время в адаптере сбережений
                    purseAdapter.updateTime(calendarPresent.getTimeInMillis());
                    //обновить время в фрагменте добавления вклада
                    addDepositFragment.updateCurrentTime(calendarPresent.getTimeInMillis());
                    break;
                default: break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //кнопка путешествия во времени
            case R.id.main_activity_button_travel:
                int plutoniumCount = delorean.getPlutonium();
                int fuelCount = delorean.getFuel();
                Intent startTravelActivity = new Intent(MainActivity.this, ControlPanelActivity.class);
                //проверка плутония
                if((plutoniumCount > 0) && (fuelCount > 0)) {
                    startTravelActivity.putExtra(Constants.KEY_TIME_PRESENT, calendarPresent.getTimeInMillis());
                    startTravelActivity.putExtra(Constants.KEY_TIME_LAST, calendarLast.getTimeInMillis());
                    calendarLast.setTimeInMillis(calendarPresent.getTimeInMillis());

                    startActivityForResult(startTravelActivity,
                            Constants.REQUEST_CODE_TRAVEL);
                } else {
                    showLoseDialog();
                }
                break;
            case R.id.main_activity_button_exchange:
                showExchangeDialog();
                break;
            default: break;
        }
    }

    public void showExchangeDialog() {
        fragmentChange.setOnMoneyChangedListener(this);

        try {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    fragmentChange)
                    .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(Constants.BACK_STACK_CHANGE)
                    .commit();
        } catch (Exception e) {
            Log.e("myLogs", "FragmentChange adding error: " + e.toString() +
                    "; see stackTrace below");
            e.printStackTrace();
        }
    }

    @Override
    public void onMoneyChanged() {
        purseAdapter.selectCurrency(bank.getCurrency(), calendarPresent.getTimeInMillis());
        //увеличиваем уровень в зависимости от количества денег
        levelUp(calendarPresent.getTimeInMillis());
    }

    @Override
    public void onChange(int currencyTo) {

    }

    @Override
    public void onDepositClick() {
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, addDepositFragment)
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(Constants.BACK_STACK_ADD_DEPOSIT)
                .commit();
    }

    @Override
    public void onDepositAdd(double howMuch, int currencyIndex, long timeInMillis) {
        depositsAdapter.updateDeposits(bank.getDeposits(timeInMillis));
        depositsAdapter.notifyDataSetChanged();
        updateDepositsRecyclerHeight(mainPresentTimePanel.getDate().getTimeInMillis());

    }

    //обновление высоты recyclerView для вкладов
    private void updateDepositsRecyclerHeight(long time) {
        depositsRecyclerView.getLayoutParams().height = getResources()
                .getDimensionPixelSize(R.dimen.deposit_item_height) *
                bank.getDeposits(time).size()
                + getResources().getDimensionPixelSize(R.dimen.deposit_add_item_height);
        depositsRecyclerView.setHasFixedSize(true);
        depositsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMoneyWithdraw(int money, int currencyTo, long timeInMillis) {
        //снятие денег с вклада
        updateDepositsRecyclerHeight(mainPresentTimePanel.getDate().getTimeInMillis());
        purse.add(money, currencyTo, timeInMillis);
        //увеличиваем уровень в зависимости от количества денег
        levelUp(timeInMillis);
        purseAdapter.notifyDataSetChanged();
    }

    private void levelUp(long timeInMillis) {
        double rubles;
        if(timeInMillis >= Constants.JAN_1998_1) {
            rubles = purse.getMoney(Bank.CURRENCY_RUBLES, timeInMillis);
            if(((rubles >= Constants.MONEY_LEVEL_1) && (rubles < Constants.MONEY_LEVEL_2)) && !level1done) {
                onLevelIncreasedListener.onLevelIncreased();
            } else if(((rubles >= Constants.MONEY_LEVEL_2) && (rubles < Constants.MONEY_LEVEL_3)) && !level2done) {
                onLevelIncreasedListener.onLevelIncreased();
            } else if((rubles >= Constants.MONEY_LEVEL_3) && !level3done) {
                onLevelIncreasedListener.onLevelIncreased();
            }
        }
    }

    private double generateRandomInterest() {
        int min = 0, max = 2;
        Random random = new Random();
        double interest = Constants.interestValues[random.nextInt((max - min) + 1) + min];
        Log.d(LOG_TAG, "random interest (7, 10, 12) = " + interest);
        return interest;
    }

    private void showAddResourcesFragment(int resourceType) {
        String backStackEntryName = "";
        if(resourceType == Constants.RESOURCE_TYPE_PLUTONIUM) {
            backStackEntryName = Constants.BACK_STACK_ADD_PLUTONIUM;
        } else if(resourceType == Constants.RESOURCE_TYPE_FUEL) {
            backStackEntryName = Constants.BACK_STACK_ADD_FUEL;
        }
        getSupportFragmentManager().popBackStack();
        FragmentAddResources fragmentAddResources = FragmentAddResources.newInstance(resourceType, calendarPresent.getTimeInMillis());
        fragmentAddResources.addOnAddResourcesListener(this); //добавить это activity как слушателя
        //fragmentAddResources.addOnAddResourcesListener(delorean); //добавить слушателя delorean

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragmentAddResources)
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(backStackEntryName)
                .commit();
    }

    //функции сохранения и восстановления системного времени для сохранения и продолжения игры

    void saveTime(long time) {
        sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(Constants.PREFERENCE_CURRENT_TIME, time);
        editor.apply();
    }

    long getSavedTime() {
        sp = getPreferences(MODE_PRIVATE);
        return sp.getLong(Constants.PREFERENCE_CURRENT_TIME, Constants.SAVED_TIME_DEFAULT);
    }

    /**
     * Докупить плутоний
     * @param count: количество добавляемого плутония (ед.)
     * @param price: цена плутония ($)
     */
    @Override
    public void onAddPlutonium(int count, double price) {
        purse.giveCash(price, bank, calendarPresent.getTimeInMillis());
        purseAdapter.notifyDataSetChanged();
        delorean.addPlutonium(count);
        resourcesToolbar.setPlutoniumNumber(delorean.getPlutonium());
    }

    /**
     * Добавить топливо
     * @param count: количество добавляемого бензина (мл)
     * @param price: цена бензина ($)
     */
    @Override
    public void onAddFuel(int count, double price) {
        purse.giveCash(price, bank, calendarPresent.getTimeInMillis());
        purseAdapter.notifyDataSetChanged();
        delorean.addFuel(count);
        resourcesToolbar.setFuelNumber(delorean.getFuel());
    }

    public void showLevelIncreasedDialog(int level) {
        String message = "";
        switch (level) {
            case 1:
                message = getString(R.string.message_level_1);
                level1done = false;
                level2done = false;
                level3done = false;
                break;
            case 2:
                message = getString(R.string.message_level_2);
                level1done = true;
                break;
            case 3:
                message = getString(R.string.message_level_3);
                level2done = true;
                break;
            case 4:
                message = getString(R.string.message_win);
                level3done = true;
                break;
            default: break;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.dialog_level_up_title_text) + " " + level)
                .setMessage(message)
                .setNegativeButton(R.string.dialog_continue_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        if(level3done) {
            dialogBuilder.setPositiveButton(R.string.dialog_start_new_game, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TaskStartNewGame taskStartNewGame = new TaskStartNewGame();
                    taskStartNewGame.execute(System.currentTimeMillis());
                    dialog.dismiss();
                }
            });
        }
        dialogBuilder.create().show();
    }

    public void showLoseDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.title_lose))
                //.setMessage(message)
                .setNegativeButton(R.string.dialog_lose_main_menu_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //todo сделать невозможным продолжение игры
                        onBackPressed();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onLevelIncreased() {
        int level = delorean.getLevel();
        showLevelIncreasedDialog(level + 1);
        delorean.increaseLevel();
    }

    //asyncTasks

    private class TaskStartNewGame extends AsyncTask<Long, Void, Void> {

        private long timeTaskStarted;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timeTaskStarted = System.currentTimeMillis();
        }

        /**
         * Асинхронная запись данных игры в БД
         * @param params текущее системное время смартфона
         */
        @Override
        protected Void doInBackground(Long... params) {
            long currentSystemTime = params[0];

            saveTime(currentSystemTime);
            bank.clearDeposits();
            delorean.init();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resourcesToolbar.setPlutoniumNumber(delorean.getPlutonium());
                    resourcesToolbar.setFuelNumber(delorean.getFuel());
                    //показывается диалог с первым уровнем
                    showLevelIncreasedDialog(Constants.GAME_START);
                }
            });


            String savedGameName = "my_new_game " + currentSystemTime;
            //сохранение системного времени как ключа
            dbHelper.addSavedGame(db, currentSystemTime, savedGameName);
            //удалить вклады
            dbHelper.deleteDeposits(db);
            //добавить вклады
            dbHelper.addDeposits(db, bank.getDeposits(currentSystemTime), currentSystemTime);
            //добавить личные сбережения
            dbHelper.addPurse(db, purse.getPurse(), currentSystemTime);
            //добавить общую информацию
            dbHelper.addGeneralData(db, calendarPresent.getTimeInMillis(),
                    calendarDestination.getTimeInMillis(), calendarLast.getTimeInMillis(),
                    bank.getCurrency(), currentSystemTime);
            //добавить данные Delorean
            dbHelper.addDelorean(db, delorean, currentSystemTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long timeSpent = System.currentTimeMillis() - timeTaskStarted;
            Log.d(LOG_TAG, "Запись данных заняла " + timeSpent/1000d + " секунд");
        }
    }

    private class TaskReadGameData extends AsyncTask<Long, Void, Void> {

        private long timeTaskStarted;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timeTaskStarted = System.currentTimeMillis();
        }

        /**
         * Асинхронное чтение данных игры из БД
         * @param params сохранённое время в SharedPreferences
         */
        @Override
        protected Void doInBackground(Long... params) {
            long savedTime = params[0];
            Bundle generalInfoBundle;
            //считать вклады
            bank.setDeposits(dbHelper.readDeposits(db, savedTime));
            //считать сбережения
            dbHelper.readPurse(db, purse, savedTime);
            //считать общую информацию
            generalInfoBundle = dbHelper.readGeneralInfo(db, savedTime);
            //считать данные Delorean
            dbHelper.readDelorean(db, delorean, savedTime);
            //установить игровое время
            calendarDestination.setTimeInMillis(generalInfoBundle.getLong(Constants.BUNDLE_DB_TIME_DESTINATION));
            calendarPresent.setTimeInMillis(generalInfoBundle.getLong(Constants.BUNDLE_DB_TIME_PRESENT));
            calendarLast.setTimeInMillis(generalInfoBundle.getLong(Constants.BUNDLE_DB_TIME_LAST_DEPARTED));
            //установить выбранную валюту
            bank.setCurrency(generalInfoBundle.getInt(Constants.BUNDLE_DB_CURRENCY_SELECTED));
            purseAdapter.selectCurrency(bank.getCurrency(), calendarPresent.getTimeInMillis());

            updateDepositsRecyclerHeight(savedTime);

            //обновить время в адаптере сбережений
            purseAdapter.updateTime(calendarPresent.getTimeInMillis());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long timeSpent = System.currentTimeMillis() - timeTaskStarted;
            Log.d(LOG_TAG, "Чтение данных заняло " + timeSpent/1000d + " секунд");
        }
    }

    private class TaskUpdateGameData extends AsyncTask<Long, Void, Void> {

        private long timeTaskStarted;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            timeTaskStarted = System.currentTimeMillis();
        }

        /**
         * Асинхронное обновление данных игры в БД
         * @param params сюда подаётся:
         *               первым параметром - сохранённое время в SharedPreferences
         *               вторым параметром - текущее время по календарю игры
         */
        @Override
        protected Void doInBackground(Long... params) {
            long gameSavedTime = params[0];
            long presentTime = params[1];
            //перезаписать вклады
            dbHelper.deleteDeposits(db);
            dbHelper.addDeposits(db, bank.getDeposits(presentTime),
                    gameSavedTime);
            //если не получилось обновить сбережения по gameSavedTime
            if(dbHelper.updatePurse(db, purse.getPurse(), gameSavedTime) == 0) {
                //то добавить их
                dbHelper.addPurse(db, purse.getPurse(), gameSavedTime);
            }

            //если не получилось обновить время и валюту
            if(dbHelper.updateGeneralInfo(db, calendarPresent.getTimeInMillis(),
                    calendarDestination.getTimeInMillis(),
                    calendarLast.getTimeInMillis(), bank.getCurrency(), gameSavedTime) == 0) {
                //то добавляем их
                dbHelper.addGeneralData(db, calendarPresent.getTimeInMillis(),
                        calendarDestination.getTimeInMillis(),
                        calendarLast.getTimeInMillis(), bank.getCurrency(), gameSavedTime);
            }

            //если не получилось обновить данные Delorean
            if(dbHelper.updateDelorean(db, delorean, gameSavedTime) == 0) {
                //то добавляем их
                dbHelper.addDelorean(db, delorean, gameSavedTime);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long timeSpent = System.currentTimeMillis() - timeTaskStarted;
            Log.d(LOG_TAG, "Обновление данных заняло " + timeSpent/1000d + " секунд");
        }
    }


}
