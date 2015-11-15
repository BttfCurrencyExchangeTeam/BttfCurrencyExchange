package com.tixon.backtothefutureexchange;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
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
import android.widget.TextView;

import com.tixon.backtothefutureexchange.ui.ControlPanelItem;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        OnMoneyChangedListener,
        OnItemCheckedListener, OnAddDepositItemClickListener, OnDepositAddListener, OnMoneyWithdrawListener {

    TextView tvCurrentYear, tvMoney;
    Button bTravel, bExchange;
    FrameLayout container;
    RelativeLayout purseHeader;
    ControlPanelItem mainPresentTimePanel; //панель времени, показывает, когда мы в данный момент

    RecyclerView purseRecyclerView, depositsRecyclerView;
    LinearLayoutManager purseLayoutManager, depositsLayoutManager;
    PurseItemsRecyclerAdapter purseAdapter;
    DepositRecyclerAdapter depositsAdapter;

    private FragmentChange fragmentChange;
    private AddDepositFragment addDepositFragment;

    private Calendar calendar, calendarPresent, calendarLast;

    private OnAddPlutoniumListener onAddPlutoniumListener;

    private Purse purse;
    private Delorean delorean;
    private Bank bank;

    private boolean isPurseExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPurseExpanded = true;

        calendar = Calendar.getInstance();
        calendarPresent = Calendar.getInstance();
        calendarLast = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendarPresent.setTimeInMillis(calendar.getTimeInMillis());

        delorean = Delorean.getDelorean();

        bank = Bank.getInstance(getResources().getStringArray(R.array.dollars),
                getResources().getStringArray(R.array.pounds));
        bank.setYearIndex(calendar.get(Calendar.YEAR));

        purse = Purse.getInstance();
        purse.init();

        initViews();

        bank.setCurrency(Bank.CURRENCY_DOLLARS);
        tvMoney.setText(bank.getCurrencySymbol() + String.valueOf(purse.getMoney(bank.getCurrency(),
                calendarPresent.get(Calendar.YEAR))));

        fragmentChange = FragmentChange.newInstance(bank, purse, calendarPresent);
        addDepositFragment = AddDepositFragment.newInstance(bank, purse, calendarPresent.getTimeInMillis());
        //addDepositFragment.setOnDepositAddListener(this); //слушатель на кнопку создания депозита

        addDepositFragment.addOnDepositAddListener(this); //слушатель на кнопку создания депозита
        addDepositFragment.addOnDepositAddListener(purse);

        //addDepositFragment.setOnMoneyChangedListener(this);

        addDepositFragment.setOnMoneyChangedListener(this); //слушатель изменения значения денег
    }

    public void setOnAddPlutoniumListener(OnAddPlutoniumListener onAddPlutoniumListener) {
        this.onAddPlutoniumListener = onAddPlutoniumListener;
    }

    private void initViews() {
        container = (FrameLayout) findViewById(R.id.main_container);
        purseHeader = (RelativeLayout) findViewById(R.id.purse_header_frame);

        mainPresentTimePanel = (ControlPanelItem) findViewById(R.id.main_activity_present_panel);
        mainPresentTimePanel.setPanelType(ControlPanelItem.PRESENT_TIME);
        mainPresentTimePanel.setDate(calendarPresent);
        mainPresentTimePanel.startTimeRoll();

        purseRecyclerView = (RecyclerView) findViewById(R.id.purse_recycler_view);
        purseLayoutManager = new LinearLayoutManager(this);
        purseAdapter = new PurseItemsRecyclerAdapter(this, purse.getPurse());
        purseRecyclerView.setLayoutManager(purseLayoutManager);
        purseRecyclerView.setAdapter(purseAdapter);
        int recyclerViewHeight = getResources().getDimensionPixelSize(R.dimen.purse_item_height) * purse.getPurse().length;
        purseRecyclerView.getLayoutParams().height = recyclerViewHeight;
        purseRecyclerView.setHasFixedSize(true);

        depositsRecyclerView = (RecyclerView) findViewById(R.id.deposits_recycler_view);
        depositsLayoutManager = new LinearLayoutManager(this);
        depositsAdapter = new DepositRecyclerAdapter(bank.getDeposits(), calendarPresent.getTimeInMillis());
        depositsRecyclerView.setLayoutManager(depositsLayoutManager);
        depositsRecyclerView.setAdapter(depositsAdapter);
        depositsRecyclerView.getLayoutParams().height = getResources()
                .getDimensionPixelSize(R.dimen.deposit_item_height) * bank.getDeposits().size()
                + getResources().getDimensionPixelSize(R.dimen.deposit_add_item_height);
        depositsRecyclerView.setHasFixedSize(true);
        depositsAdapter.setOnAddDepositListener(this);
        depositsAdapter.setOnMoneyWithdrawListener(this);


        bTravel = (Button) findViewById(R.id.main_activity_button_travel);
        bExchange = (Button) findViewById(R.id.main_activity_button_exchange);

        bTravel.setOnClickListener(this);
        bExchange.setOnClickListener(this);

        tvCurrentYear = (TextView) findViewById(R.id.main_activity_tv_year);
        tvMoney = (TextView) findViewById(R.id.main_activity_tv_money);
        tvCurrentYear.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                Constants.TYPEFACE_DIGITS));
        tvMoney.setTypeface(Typeface.createFromAsset(getResources().getAssets(),
                Constants.TYPEFACE_DIGITS));

        tvCurrentYear.setText(String.valueOf(calendarPresent.get(Calendar.YEAR)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_CODE_TRAVEL:
                    calendarPresent.setTimeInMillis(data.getLongExtra(Constants.KEY_TIME_DESTINATION, System.currentTimeMillis()));
                    bank.setYearIndex(calendarPresent.get(Calendar.YEAR));
                    tvCurrentYear.setText(String.valueOf(calendarPresent.get(Calendar.YEAR)));
                    Log.d("myLogs", "currentYear = " + calendarPresent.get(Calendar.YEAR) +
                            ", lastYear = " + calendarLast.get(Calendar.YEAR));
                    depositsAdapter.updateCurrentTime(calendarPresent.getTimeInMillis());
                    depositsAdapter.notifyDataSetChanged();
                    addDepositFragment.updateCurrentTime(calendarPresent.getTimeInMillis());
                    break;
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
            default:
                break;
        }
    }

    public void showExchangeDialog() {
        fragmentChange.setOnMoneyChangedListener(this);

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    fragmentChange)
                    .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack("CHANGE_FRAGMENT")
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
        tvMoney.setText(bank.getCurrencySymbol() + String.valueOf((int) purse
                .getMoney(bank.getCurrency(), calendarPresent.get(Calendar.YEAR))));
        purseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChange(int currencyTo) {

    }

    @Override
    public void onDepositClick() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, addDepositFragment)
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack("ADD_DEPOSIT_FRAGMENT")
                .commit();
    }

    @Override
    public void onDepositAdd(double howMuch, int currencyIndex, int year) {
        depositsRecyclerView.getLayoutParams().height = getResources()
                .getDimensionPixelSize(R.dimen.deposit_item_height) * bank.getDeposits().size()
                + getResources().getDimensionPixelSize(R.dimen.deposit_add_item_height);
        depositsRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onMoneyWithdraw(int money, int currencyTo, int year) {
        //снятие денег с депозита
        purse.add(money, currencyTo, year);
        purseAdapter.notifyDataSetChanged();
    }
    //вычесть из баланса долларовый эквивалент цены за количество плутония
    //cash -= count * bank.change(Bank.CURRENCY_DOLLARS, bank.getCurrency(), 10000);
}
