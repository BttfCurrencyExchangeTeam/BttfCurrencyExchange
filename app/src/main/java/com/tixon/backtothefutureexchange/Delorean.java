package com.tixon.backtothefutureexchange;

public class Delorean implements OnAddPlutoniumListener {

    private static volatile Delorean instance;

    private int plutonium;

    private Delorean() {
        plutonium = 5;
    }

    public static Delorean getDelorean() {
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

    //переместиться во времени
    //на одно перемещение расходуется один контейнер с плутонием
    public void travel() {
        this.plutonium--;
    }

    /**
     * Докупить плутоний
     * @param count: количество добавляемого плутония
     */
    @Override
    public void onAddPlutonium(int count) {
        this.plutonium += count;
    }
}
