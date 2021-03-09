package com.example.application;

public class GetManufacturerNameModel {

    private String name;

    public GetManufacturerNameModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
