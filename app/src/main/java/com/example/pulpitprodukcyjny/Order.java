package com.example.pulpitprodukcyjny;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private int id;
    private String number;
    private String customer;
    private String product;
    private String productCatalogNumber;
    private String specialPole1;
    private String specialPole2;
    private String specialPole3;
    private String shipDate;
    private ArrayList<Resource> resources;
    private boolean inProgress;

    public Order(int id, String number, String customer, String product, String catalogNumber, String specialPole1, String specialPole2, String specialPole3, String shipDate,
                 ArrayList<Resource> resources, boolean inProgress) {
        this.id = id;
        this.number = number;
        this.customer = customer;
        this.product = product;
        this.productCatalogNumber = catalogNumber;
        this.specialPole1 = specialPole1;
        this.specialPole2 = specialPole2;
        this.specialPole3 = specialPole3;
        this.shipDate = shipDate;
        this.resources = resources;
        this.inProgress = inProgress;
    }

    public int getResourcesSize() {
        return resources.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }


    public String getShipDate() {
        return shipDate;
    }


    public String getProduct() {
        return product;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public String getProductCatalogNumber() {
        return productCatalogNumber;
    }

    public void setProductCatalogNumber(String productCatalogNumber) {
        this.productCatalogNumber = productCatalogNumber;
    }

    public String getSpecialPole1() {
        return specialPole1;
    }


    public String getSpecialPole2() {
        return specialPole2;
    }


    public String getSpecialPole3() {
        return specialPole3;
    }


    public String getCustomer() {
        return customer;
    }


    public Resource getIndexOfArrarResources(int index) {
        return resources.get(index);
    }

    public int getOverallResult() {
        int plannedQuantity = 0;
        int realizedQuantity = 0;
        for (int i = 0; i < resources.size(); i++) {
            plannedQuantity += resources.get(i).getPlannedQuantity();
            realizedQuantity += resources.get(i).getRealizedQuantity();
        }
        if (plannedQuantity == 0)
            return 0;
        else
            return (realizedQuantity * 100 / plannedQuantity);
    }



}


