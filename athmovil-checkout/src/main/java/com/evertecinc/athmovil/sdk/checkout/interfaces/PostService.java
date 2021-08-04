package com.evertecinc.athmovil.sdk.checkout.interfaces;

import com.evertecinc.athmovil.sdk.checkout.utils.ConstantUtil;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PostService {
    @Headers({
            "Content-type: application/json"
    })
    @POST(ConstantUtil.API_ROUTE)
    Call<JsonObject> sendPost(@Body JsonObject body);
}
