package com.pmaptechnotech.travelbot.api;

import com.pmaptechnotech.travelbot.models.UserBookingInput;
import com.pmaptechnotech.travelbot.models.UserBookingResult;
import com.pmaptechnotech.travelbot.models.UserLoginInput;
import com.pmaptechnotech.travelbot.models.UserLoginResult;
import com.pmaptechnotech.travelbot.models.UserRegisterInput;
import com.pmaptechnotech.travelbot.models.UserRegisterResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Admin on 1/20/2018.
 */

public interface WebServices {
    @POST("TBUserLogin_c/userLogin")
    Call<UserLoginResult> userLogin(@Body UserLoginInput input);

    @POST("TBUserRegister_c/userRegister")
    Call<UserRegisterResult> userRegister(@Body UserRegisterInput input);

    @POST("TBBookingDetailes_c/userBooking")
    Call<UserBookingResult> userBooking(@Body UserBookingInput input);




}


