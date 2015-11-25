package com.tixon.backtothefutureexchange;

public class Delorean implements OnAddResourcesListener, OnTimeTravelListener {

    private static volatile Delorean instance;

    private int plutonium;
    private int fuel;
    private int level;

    public void init() {
        level = 1;
        plutonium = 5;
        fuel = 2000;
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

    public void setPlutonium(int plutonium) {
        this.plutonium = plutonium;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public void addPlutonium(int plutonium) {
        this.plutonium += plutonium;
    }

    public void addFuel(int fuel) {
        this.fuel += fuel;
    }

    public void increaseLevel() {
        level++;
    }

    public int getLevel() {
        return level;
    }

    //переместиться во времени
    //на одно перемещение расходуется один контейнер с плутонием
    public void travel() {
        this.plutonium--;
        this.fuel -= 100;
    }

    /**
     * Докупить плутоний
     * @param count: количество добавляемого плутония
     */
    @Override
    public void onAddPlutonium(int count, double price) {
        //this.plutonium += count;
    }

    @Override
    public void onAddFuel(int count, double price) {

    }

    //запускается при перемещении во времени
    @Override
    public void onTimeTraveled() {
        travel();
    }
}
