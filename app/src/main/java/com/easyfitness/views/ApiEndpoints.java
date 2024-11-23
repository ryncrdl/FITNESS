package com.easyfitness.views;

//import com.example.onlinelaundry.Booking.BookingResponse;
//import com.example.onlinelaundry.Booking.CreateBooking;
//import com.example.onlinelaundry.Login.AccountResponse;
//import com.example.onlinelaundry.Registration.CreateAccount;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiEndpoints {

    //Login
    @POST("app/application-0-xqlir/endpoint/loginAccount")
    Call<AccountResponse> loginAccount(@Body RequestBody requestBody);

    //Register
    @POST("app/application-0-xqlir/endpoint/createAccount")
    Call<AccountResponse> createAccount(@Body RequestBody requestBody);

//    //User Profile
//    @POST("app/application-0-lpuiw/endpoint/getUserById")
//    Call<AccountResponse> getUserById(@Body RequestBody requestBody);
//
//    @POST("app/application-0-lpuiw/endpoint/updateUserById")
//    Call<CreateAccount> updateUserById(@Body RequestBody requestBody);
//
//    //Booking
//    @POST("app/application-0-lpuiw/endpoint/createBooking")
//    Call<CreateBooking> createBooking(@Body RequestBody requestBody);
//
//    @POST("app/application-0-lpuiw/endpoint/getBookingById")
//    Call<List<BookingResponse>> getBooking(@Body RequestBody requestBody);
}
