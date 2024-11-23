package com.easyfitness.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentTransaction;


import com.easyfitness.DatePickerDialogFragment;
import com.easyfitness.R;
import com.easyfitness.utils.DateConverter;
import com.easyfitness.utils.LoadingDialog;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    private AppCompatButton btnRegister;
    private TextView btnLogin;

    private EditText txtFullName, txtUsername, txtPassword, txtConfirmPassword;
    private Button btnSelectGender, btnSelectDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        btnLogin = findViewById(R.id.loginButton);
        btnRegister = findViewById(R.id.btnRegister);
        txtFullName = findViewById(R.id.fullName);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);
        txtConfirmPassword = findViewById(R.id.confirmPassword);

        btnSelectGender = findViewById(R.id.btnSelectGender);
        btnSelectGender.setOnClickListener(v -> showGenderPickerDialog());

        // Date Picker Button Click Listener
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
//
        btnLogin.setOnClickListener(view->{
            startActivity(new Intent(this, LoginActivity.class));
        });
//
        btnRegister.setOnClickListener(view -> {
            String fullName = txtFullName.getText().toString().trim().toLowerCase();
            String username = txtUsername.getText().toString().trim().toLowerCase();
            String password = txtPassword.getText().toString().trim().toLowerCase();
            String confirmPassword = txtConfirmPassword.getText().toString().trim().toLowerCase();
            String gender = btnSelectGender.getText().toString().toLowerCase();
            String birthday = btnSelectDate.getText().toString().toLowerCase();
            validateDetails(fullName, username, password, confirmPassword, gender, birthday);
        });
    }



    private void validateDetails(String fullName, String username, String password, String confirmPassword, String gender, String birthday) {
        if(fullName.isEmpty() || username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }else if (fullName.length() < 5) {
            Toast.makeText(this, "Full name must be at least 5 characters", Toast.LENGTH_SHORT).show();
        } else if (username.length() < 5) {
            Toast.makeText(this, "Username must be at least 5 characters", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }else if(gender.equals("options")){
        Toast.makeText(this, "Select your gender.", Toast.LENGTH_SHORT).show();
    }else if(birthday.equals("options")){
            Toast.makeText(this, "Select your birthday.", Toast.LENGTH_SHORT).show();
        }
        else{
            createAccount(fullName, username, password, gender, birthday);
        }
    }

    private void createAccount(String fullName, String username, String password, String gender, String birthday) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        ApiClient apiClient = new ApiClient();
        ApiEndpoints apiService = apiClient.getApiService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("fullName", fullName);
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password", password);
        jsonObject.addProperty("gender", gender);
        jsonObject.addProperty("birthday", birthday);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Call<AccountResponse> call = apiService.createAccount(requestBody);
        call.enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                if (response.isSuccessful()) {
                    loadingDialog.hide();
                    finish();
                    Toast.makeText(RegistrationActivity.this, "Registration Completed.", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    loadingDialog.hide();
                    if(!response.errorBody().equals(null)){
                        try {
                            String errorResponse = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            if (jsonObject.has("message")) {
                                String errorMessage = jsonObject.getString("message");
                                Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistrationActivity.this, "Error processing response.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {
                // Network error, handle failure case here
                loadingDialog.hide();
                Toast.makeText(RegistrationActivity.this, "Network error. Please check your network connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGenderPickerDialog() {
        final String[] genders = {"Male", "Female", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedGender = genders[which];
                        btnSelectGender.setText(selectedGender);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Do something with the selected date
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year; // Month is 0-based
                btnSelectDate.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

}
