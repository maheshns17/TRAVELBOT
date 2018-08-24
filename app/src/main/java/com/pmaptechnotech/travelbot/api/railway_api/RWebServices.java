package com.pmaptechnotech.travelbot.api.railway_api;

import com.pmaptechnotech.travelbot.logics.P;
import com.pmaptechnotech.travelbot.models.RNameToCodeResult;
import com.pmaptechnotech.travelbot.models.RTrainsBetweenResult;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Prasad K P on 1/20/2018.
 */

public interface RWebServices {

    @GET(P.RL_API)
    Call<RNameToCodeResult> getCodeFromName();

    @GET(P.RL_API)
    Call<RTrainsBetweenResult> getTrainsBetween();

}


