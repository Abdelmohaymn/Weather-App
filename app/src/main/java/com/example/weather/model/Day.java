package com.example.weather.model;

public class Day {
    private String maxtemp_c;
    private String mintemp_c;
    private Condition condition;

    public String getMaxtemp_c() {
        return maxtemp_c;
    }

    public String getMintemp_c() {
        return mintemp_c;
    }

    public Condition getCondition() {
        return condition;
    }
}
