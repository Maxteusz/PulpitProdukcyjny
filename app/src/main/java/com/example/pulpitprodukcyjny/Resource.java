package com.example.pulpitprodukcyjny;

import java.io.Serializable;

public class Resource implements Serializable {
    private String name;
    private int plannedQuantity;
    private int realizedQuantity;
    private double plannedTime, realTime;

    public String getName() {
        return name;
    }

    public int getPlannedQuantity() {
        return plannedQuantity;
    }


    public int getRealizedQuantity() {
        return realizedQuantity;
    }


    public double getPlannedTime() {
        return plannedTime;
    }


    public double getRealTime() {
        return realTime;
    }

   public String getEstamitedTime()
    {
        return String.format("%.1f%n",(realTime/realizedQuantity)*plannedQuantity);
    }


    public Resource(String name, int plannedQuantity, int realizedQuantity) {
        this.name = name;
        this.plannedQuantity = plannedQuantity;
        this.realizedQuantity = realizedQuantity;
    }

    public Resource(String name, int plannedQuantity, int realizedQuantity, double plannedTime, double realTime) {
        this.name = name;
        this.plannedQuantity = plannedQuantity;
        this.realizedQuantity = realizedQuantity;
        this.plannedTime = plannedTime;
        this.realTime = realTime;
    }

    public int realizedInPercentage() {
        if (plannedQuantity != 0)
            return (int) (realizedQuantity * 100 / plannedQuantity);
        else
            return 0;
    }


}
