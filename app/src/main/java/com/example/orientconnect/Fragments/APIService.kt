package com.example.orientconnect.Fragments

import com.example.orientconnect.Notifications.MyResponse
import com.example.orientconnect.Notifications.Sender
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService
{

    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAOENptrE:APA91bHzcQU8deizmUavXa1P1gV1sReonME4ltkfQhGVUZ_vf81TfKFoSOFNTNWvyYRNrWab9KVLex7H3-KB4Q-rGDVtOYu5_075G8NUnp5pJt5rR42sH-inxuvqC-BHTSZ_b_iawBf8"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?
}