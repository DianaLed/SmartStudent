package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.studentapp.databinding.ActivityAuthBinding;
import com.example.studentapp.db.ApiInterface;
import com.example.studentapp.db.ServiceBuilder;
import com.example.studentapp.db.Subjects;
import com.example.studentapp.db.Users;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    private ActivityAuthBinding binding;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        apiInterface = ServiceBuilder.buildRequest().create(ApiInterface.class);
        Paper.init(this);

        binding.authBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Users aUser = new Users(2,binding.loginAuth.getText().toString(),
                        binding.passwordAuth.getText().toString());
                Call<Users> authUser = apiInterface.authUsers(aUser);
                authUser.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {

                        Toast.makeText(AuthActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();


                        if (response.body()!= null){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            Users.writeUser(response.body());
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(AuthActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        Toast.makeText(AuthActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}