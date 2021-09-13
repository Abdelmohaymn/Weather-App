package com.example.weather.model;

import java.util.List;

public class ForcastObj {
    private String date_epoch;
    private Day day;
    private List<HourObj> hour;

    public String getDate_epoch() {
        return date_epoch;
    }

    public Day getDay() {
        return day;
    }

    public List<HourObj> getHour() {
        return hour;
    }
}
