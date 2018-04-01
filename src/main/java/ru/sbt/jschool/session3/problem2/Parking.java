package ru.sbt.jschool.session3.problem2;

import java.util.HashMap;
import java.util.Map;

public class Parking {
    private int capacity;
    private double cost;
    private double nightFactor;
    private Map<Long,Long> carsEnterTime;  // carID -> enterTime

    public Parking(int capacity, float cost) {
        this.capacity = capacity;
        this.cost = cost;
        nightFactor = 2.;
        carsEnterTime = new HashMap<>(capacity*2);
    }

    public boolean tryEnter(long carID, long comingTime){
        if(capacity>0 && !carsEnterTime.containsKey(carID)) {
            carsEnterTime.put(carID, comingTime);
            capacity--;
            return true;
        }
        return false;
    }

    public double moveOut(long carID,long leaveTime){
        double result = .0;
        if (carsEnterTime.containsKey(carID)&& carsEnterTime.get(carID)<=leaveTime){

            carsEnterTime.remove(carID);
            capacity++;

            leaveTime++;
            result += cost * (leaveTime / 24 * (nightFactor * 7 + 17) + 6 * nightFactor);
            leaveTime %= 24;
            result += cost* ((leaveTime -= 7) < 0?nightFactor:1) * leaveTime;

        }
        return result;
    }

    public static void main(String[] args) {

        double result=0;
        int leaveTime;
        double cost = 1;
        double nightFactor = 2;

        for (int i = 0;i<311;i++,result=0) {
            leaveTime=i+1;
            result += cost * (leaveTime / 24 * (nightFactor * 7 + 17) + 6 * nightFactor);
            leaveTime %= 24;
            result += cost* ((leaveTime -= 7) < 0?nightFactor:1) * leaveTime;
            System.out.printf("%2d)%3.0f%n",i%24,result);
        }
    }
}
