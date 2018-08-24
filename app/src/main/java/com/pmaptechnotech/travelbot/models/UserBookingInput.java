package com.pmaptechnotech.travelbot.models;

/**
 * Created by intel on 24-02-18.
 */

public class UserBookingInput {
    public String b_name;
    public String b_mobile_number;
    public String b_train_name;
    public String b_train_number;
    public String b_price;
    public String b_source;
    public String b_destination;
    public String b_booking_date;
    public String b_duration;

    // ALT INSERT FOR Constructor//


    public UserBookingInput(String b_name, String b_mobile_number, String b_train_name, String b_train_number, String b_price, String b_source, String b_destination, String b_booking_date, String b_duration) {
        this.b_name = b_name;
        this.b_mobile_number = b_mobile_number;
        this.b_train_name = b_train_name;
        this.b_train_number = b_train_number;
        this.b_price = b_price;
        this.b_source = b_source;
        this.b_destination = b_destination;
        this.b_booking_date = b_booking_date;
        this.b_duration = b_duration;
    }
}






