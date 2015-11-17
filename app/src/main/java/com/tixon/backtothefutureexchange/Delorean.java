package com.tixon.backtothefutureexchange;

public class Delorean implements OnAddResourcesListener {

    private static volatile Delorean instance;

    private int plutonium;
    private double fuel;

    private Delorean() {
        plutonium = 5;
        fuel = 2d;
    }

    public static Delorean getInstance() {
        Delorean localInstance = instance;
        if(localInstance == null) {
            synchronized (Delorean.class) {
                localInstance = instance;
                if(localInstance == null) {
                    localInstance = instance = new Delorean();
                }
            }
        }
        return localInstance;
    }

    public int getPlutonium() {
        return plutonium;
    }

    public double getFuel() {
        return fuel;
    }

    //переместиться во времени
    //на одно перемещение расходуется один контейнер с плутонием
    public void travel() {
        this.plutonium--;
        this.fuel -= 0.1;
    }

    /**
     * Докупить плутоний
     * @param count: количество добавляемого плутония
     * @param price: цена за плутоний
     */
    @Override
    public void onAddPlutonium(int count, int price) {
        this.plutonium += count;
    }

    @Override
    public void onAddFuelListener(double count, int price) {

    }
}
