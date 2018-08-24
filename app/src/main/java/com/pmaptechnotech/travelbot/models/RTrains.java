package com.pmaptechnotech.travelbot.models;

import com.google.gson.JsonArray;

/**
 * Created by Prasad K P on 1/7/2018.
 */

public class RTrains {
    public String number;
    public String name;
    public String travel_time;
    public String src_departure_time;
    public String dest_arrival_time;
    public JsonArray from_station;
    public JsonArray to_station;
    public JsonArray classes;
    public JsonArray days;
}
