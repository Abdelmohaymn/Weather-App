package com.example.weather.model;

public class HourObj {
    private String time_epoch;
    private String temp_c;
    private Condition condition;

    public String getTime_epoch() {
        return time_epoch;
    }

    public String getTemp_c() {
        return temp_c;
    }

    public Condition getCondition() {
        return condition;
    }
}
