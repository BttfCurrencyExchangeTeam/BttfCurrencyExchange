package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.tixon.backtothefutureexchange.ui.ControlPanelItem;
import com.tixon.backtothefutureexchange.ui.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMoneyChangedListener,
        OnItemCheckedListener,
        OnAddDepositItemClickListener,
        OnDepositAddListener,
        OnMoneyWithdrawListener {

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

    private List<OnTimeTravelListener> onTimeTravelListenersList = new ArrayList<>();

    public void addOnTimeTravelListener(OnTimeTravelListener listener) {
        onTimeTravelListenersList.add(listener);
    }
    public void notifyTimeTravelled() {
        for(OnTimeTravelListener listener: onTimeTravelListenersList) {
            listener.onTimeTraveled();
        }
    }

    private FragmentChange fragmentChange;
    private AddDepositFragment addDepositFragment;
    private FragmentResources fragmentResources;

    private Calendar calendar, calendarPresent, calendarLast, calendarDestination;

    private OnAddResourcesListener onAddResourcesListener;

    private Purse purse;
    private Delorean delorean;
    private Bank bank;

    private boolean isPurseExpanded;

    private DataBaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);
        db = dbHelper.getWritableDatabase();

        isPurseExpanded = true;

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
        bank.setYearIndex(calendar.get(Calendar.YEAR));
        bank.setCurrency(Bank.CURRENCY_DOLLARS);
                /*tvMoney.setText(bank.getCurrencySymbol() + String.valueOf(purse.getMoney(bank.getCurrency(),
                calendarPresent.get(Calendar.YEAR))));*/
        purse = Purse.getInstance();
        purse.init(); //set 1000 dollars for start



        initViews();

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

        purseAdapter.selectCurrency(bank.getCurrency(), presentTime);

        purseRecyclerView.setAdapter(purseAdapter);
        purseRecyclerView.getLayoutParams().height = getResources()
                .getDimensionPixelSize(R.dimen.purse_item_height) * purse.getPurse().length;;
        purseRecyclerView.setHasFixedSize(true);


        depositsAdapter = new DepositRecyclerAdapter(bank.getDeposits(presentTime), presentTime);
        depositsRecyclerView.setAdapter(depositsAdapter);
        updateDepositsRecyclerHeight(presentTime);
        depositsRecyclerView.setHasFixedSize(true);
        depositsAdapter.setOnAddDepositListener(this);
        depositsAdapter.setOnMoneyWithdrawListener(this);

        //создание или продолжение игры

        Intent fromMainMenu = getIntent();
        switch (fromMainMenu.getIntExtra(Constants.KEY_NEW_OR_CONTINUE, Constants.KEY_NEW)) {
            case Constants.KEY_NEW:
                long currentSystemTime;
                currentSystemTime = System.currentTimeMillis();
                String savedGameName = "my_new_game " + currentSystemTime;
                saveTime(currentSystemTime);
                bank.clearDeposits();

                //формирование записей базы данных для сохранения игры
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
                break;
            case Constants.KEY_CONTINUE:
                long savedTime = getSavedTime();
                Bundle generalInfoBundle;
                //считать вклады
                bank.setDepositsFromDatabase(dbHelper.readDeposits(db, savedTime));
                //считать сбережения
                dbHelper.readPurse(db, purse, savedTime);
                //считать общую информацию
                generalInfoBundle = dbHelper.readGeneralInfo(db, savedTime);
                //установить игровое время
                calendarDestination.setTimeInMillis(generalInfoBundle.getLong(Constants.BUNDLE_DB_TIME_DESTINATION));
                calendarPresent.setTimeInMillis(generalInfoBundle.getLong(Constants.BUNDLE_DB_TIME_PRESENT));
                calendarLast.setTimeInMillis(generalInfoBundle.getLong(Constants.BUNDLE_DB_TIME_LAST_DEPARTED));
                //установить выбранную валюту
                bank.setCurrency(generalInfoBundle.getInt(Constants.BUNDLE_DB_CURRENCY_SELECTED));
                purseAdapter.selectCurrency(bank.getCurrency(), calendarPresent.getTimeInMillis());

                updateDepositsRecyclerHeight(savedTime);

                //update purse adapter time
                purseAdapter.updateTime(calendarPresent.getTimeInMillis());
                break;
        }

        mainPresentTimePanel.setDate(calendarPresent);
        mainPresentTimePanel.startTimeRoll();

        //создание фрагментов
        fragmentChange = FragmentChange.newInstance(bank, purse, calendarPresent);
        addDepositFragment = AddDepositFragment.newInstance(bank, purse, calendarPresent.getTimeInMillis());
        fragmentResources = FragmentResources.newInstance();

        //слушатели на нажатие кнопки создания депозита
        addDepositFragment.addOnDepositAddListener(this);
        addDepositFragment.addOnDepositAddListener(purse);

        //слушатель изменения значения денег
        addDepositFragment.setOnMoneyChangedListener(this);
    }

    //сохранение игры в onStop
    @Override
    protected void onStop() {
        super.onStop();
        long gameSavedTime = getSavedTime();
        //перезаписать вклады
        dbHelper.deleteDeposits(db);
        dbHelper.addDeposits(db, bank.getDeposits(mainPresentTimePanel.getDate().getTimeInMillis()),
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
    }

    public void setOnAddResourcesListener(OnAddResourcesListener onAddResourcesListener) {
        this.onAddResourcesListener = onAddResourcesListener;
    }

    private void initViews() {
        container = (FrameLayout) findViewById(R.id.main_container);
        purseHeader = (RelativeLayout) findViewById(R.id.purse_header_frame);
        //mainToolbar = (LinearLayout) findViewById(R.id.main_toolbar);
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

        /*mainToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddResourcesFragment();
            }
        });*/

        resourcesToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddResourcesFragment();
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

                    //onTimeTravel
                    notifyTimeTravelled();

                    bank.setYearIndex(calendarPresent.get(Calendar.YEAR));
                    //tvCurrentYear.setText(String.valueOf(calendarPresent.get(Calendar.YEAR)));
                    Log.d("myLogs", "currentYear = " + calendarPresent.get(Calendar.YEAR) +
                            ", lastYear = " + calendarLast.get(Calendar.YEAR));
                    //update deposits adapter time
                    depositsAdapter.updateCurrentTime(calendarPresent.getTimeInMillis());
                    depositsAdapter.updateDeposits(bank.getDeposits(calendarPresent.getTimeInMillis()));
                    updateDepositsRecyclerHeight(calendarPresent.getTimeInMillis());
                    //update purse adapter time
                    purseAdapter.updateTime(calendarPresent.getTimeInMillis());
                    //update time for add deposit fragment
                    addDepositFragment.updateCurrentTime(calendarPresent.getTimeInMillis());
                    break;
                default: break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_button_travel:
                Intent startTravelActivity = new Intent(MainActivity.this, ControlPanelActivity.class);
                startTravelActivity.putExtra(Constants.KEY_TIME_PRESENT, calendarPresent.getTimeInMillis());
                startTravelActivity.putExtra(Constants.KEY_TIME_LAST, calendarLast.getTimeInMillis());
                calendarLast.setTimeInMillis(calendarPresent.getTimeInMillis());

                startActivityForResult(startTravelActivity,
                        Constants.REQUEST_CODE_TRAVEL);
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

    int dpToPx(int dp) {
        Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    @Override
    public void onMoneyChanged() {
        /*tvMoney.setText(bank.getCurrencySymbol() + String.valueOf((int) purse
                .getMoney(bank.getCurrency(), calendarPresent.get(Calendar.YEAR))));*/
        purseAdapter.selectCurrency(bank.getCurrency(), calendarPresent.getTimeInMillis());
    }

    @Override
    public void onChange(int currencyTo) {

    }

    @Override
    public void onDepositClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, addDepositFragment)
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(Constants.BACK_STACK_ADD_DEPOSIT)
                .commit();
    }

    @Override
    public void onDepositAdd(double howMuch, int currencyIndex, int year) {
        depositsAdapter.updateDeposits(bank.getDeposits(calendarPresent.getTimeInMillis()));
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
    public void onMoneyWithdraw(int money, int currencyTo, int year) {
        //снятие денег с вклада
        updateDepositsRecyclerHeight(mainPresentTimePanel.getDate().getTimeInMillis());
        purse.add(money, currencyTo, year);
        purseAdapter.notifyDataSetChanged();
    }
    //вычесть из баланса долларовый эквивалент цены за количество плутония
    //cash -= count * bank.change(Bank.CURRENCY_DOLLARS, bank.getCurrency(), 10000);

    private void showAddResourcesFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, fragmentResources)
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(Constants.BACK_STACK_ADD_RESOURCES)
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
        return sp.getLong(Constants.PREFERENCE_CURRENT_TIME, 0);
    }
}
