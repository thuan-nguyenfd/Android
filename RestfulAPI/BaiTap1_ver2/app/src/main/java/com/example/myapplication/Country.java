package com.example.myapplication;

public class Country {
    private String name;
    private String capital;
    private String region;
    private  String flagUrl;

    public Country(String name, String capital, String region, String flagUrl) {
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.flagUrl=flagUrl;
    }

    public String getName() { return name; }
    public String getCapital() { return capital; }
    public String getRegion() { return region; }
    public String getFlagUrl() { return flagUrl; }

    @Override
    public String toString() {
        return name + " - Capital: " + capital + ", Region: " + region;
    }
}