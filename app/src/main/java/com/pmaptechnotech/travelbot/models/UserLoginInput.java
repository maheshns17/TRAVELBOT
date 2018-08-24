package com.pmaptechnotech.travelbot.models;

/**
 * Created by intel on 12-02-18.
 */

public class UserLoginInput {
    public String user_email;
    public String user_password;

    // ALT INSERT FOR Constructor//


    public UserLoginInput(String user_email, String user_password) {
        this.user_email = user_email;
        this.user_password = user_password;
    }
}

