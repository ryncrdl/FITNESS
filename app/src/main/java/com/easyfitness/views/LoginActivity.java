package com.easyfitness.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.easyfitness.DAO.DAOProfile;
import com.easyfitness.DAO.Profile;
import com.easyfitness.MainActivity;
import com.easyfitness.R;
import com.easyfitness.enums.Gender;
import com.easyfitness.intro.MainIntroActivity;
import com.easyfitness.intro.NewProfileFragment;
import com.easyfitness.utils.DateConverter;
import com.easyfitness.utils.LoadingDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity  extends AppCompatActivity {
    public boolean canGoForward() {
        return true;
    }
    private AppCompatButton btnLogin;
    private TextView btnRegister;
    private EditText txtUsername, txtPassword;
    private boolean mProfilCreated = false;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
        if (isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.loginButton);
        btnRegister = findViewById(R.id.registerButton);

        btnLogin.setOnClickListener(view ->{
            String getUsername = txtUsername.getText().toString().trim().toLowerCase();
            String getPassword = txtPassword.getText().toString().trim().toLowerCase();
            validateInfo(getUsername, getPassword);
        });

        btnRegister.setOnClickListener(view ->{
            startActivity( new Intent(LoginActivity.this, RegistrationActivity.class));
            Toast.makeText(LoginActivity.this, "Registration Clicked", Toast.LENGTH_SHORT).show();
        });

    }

    private void validateInfo(String username, String password){
        if(username.isEmpty()){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
        }else if(password.isEmpty()){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
        }else{
            savedLoginSession(username,password);

        }
    }

    private void savedLoginSession(String username, String password){
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<AccountResponse> call = apiService.loginAccount(requestBody);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                AccountResponse createResponse = response.body();
                if (response.code() == 200) {
                    String accountId = createResponse.get_id();
                    String username = createResponse.getUsername();
                    Toast.makeText(LoginActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                    setLoggedInStatus(true);
                    storeUserData(accountId, username);
                    loadingDialog.hide();


                    DAOProfile mDbProfiles = new DAOProfile(LoginActivity.this);
                    String birthday = createResponse.getBirthday().toLowerCase().toString();
                    String gender = createResponse.getGender().toLowerCase().toString();

                    int lGender = Gender.UNKNOWN;
                    if (gender.equals("male")) {
                        lGender = Gender.MALE;
                    } else if (gender.equals("female")) {
                        lGender = Gender.FEMALE;
                    } else if (gender.equals("other")) {
                        lGender = Gender.OTHER;
                    }

                    Profile p = new Profile(createResponse.getFullName(), 0, DateConverter.localDateStrToDate(birthday, LoginActivity.this), lGender);
                    mDbProfiles.addProfile(p);

//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
////                    intent.putExtra("username", username);
////                    intent.putExtra("fullName", createResponse.getFullName());
////                    intent.putExtra("gender", createResponse.getGender());
////                    intent.putExtra("birthday", createResponse.getBirthday());
////                    intent.putExtra("userId", createResponse.get_id());
//                    startActivity(intent);

                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(p.getName())
                            .setContentText("Login Successfully")
                            .setConfirmClickListener(sDialog -> {
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                                Toast.makeText(LoginActivity.this, "Main Activity not properly working!", Toast.LENGTH_SHORT).show();
//                                openNewProfileFragment(createResponse.getFullName(), birthday, gender);
                                Toast.makeText(LoginActivity.this, "Dashboard is loading..., Please wait.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .show();
//                    mProfilCreated = true;
//                    startActivity(new Intent(LoginActivity.this, NewProfileFragment.class));

//                    Toast.makeText(LoginActivity.this, "new profile: " + p, Toast.LENGTH_SHORT).show();
                } else {
                    setLoggedInStatus(false);
                    if(!response.errorBody().equals(null)){
                        try {
                            String errorResponse = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            if (jsonObject.has("message")) {
                                if (response.code() == 403) {
                                    String errorMessage = jsonObject.getString("message");
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    loadingDialog.hide();
                }

            }
            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                loadingDialog.hide();
                Toast.makeText(LoginActivity.this, "Server failed!", Toast.LENGTH_SHORT).show();
            }


        });


    }

    private void openNewProfileFragment(String fullName, String birthday, String gender){
        NewProfileFragment fragment = new NewProfileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Bundle data = new Bundle();
        data.putString("fullName", fullName);
        if(gender.equals("male")){
            data.putBoolean("male", true);
        }else{
            data.putBoolean("male", false);
        }
        if(gender.equals("female")){
            data.putBoolean("female", true);
        }else{
            data.putBoolean("female", false);
        }
        if(gender.equals("other")){
            data.putBoolean("other", true);
        }else{
            data.putBoolean("other", false);
        }
        data.putString("birthday", birthday);
        fragment.setArguments(data);
        transaction.replace(R.id.fragment_container, fragment).commit();
    }


    private void storeUserData(String personId, String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("accountId", personId);
        editor.putString("username", username);

        editor.apply();
    }

    private void setLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
