package com.example.hoamanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.LoginUserCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Modules.HomePage;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TextView createAccountLink;
    private EditText emailET, passwordET;
    private Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        createAccountLink = findViewById(R.id.createAccountLink);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn =findViewById(R.id.loginBtn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        createAccountLink.setOnClickListener(s -> {
            navigateTo(SignupPage.class);
        });

        loginBtn.setOnClickListener(g -> {
            loginUser();
        });
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
    private void loginUser() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if(email.isEmpty()) {
            emailET.setError("Email is required");
            emailET.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return;
        }

        setLoadingState();

        FirebaseAuthManager.loginUser(email, password, new LoginUserCallback() {
            @Override
            public void onSuccess(FirebaseUser user, HomeOwnerRentersModel userDetails) {

                if(userDetails.getRole().equals("Admin")) {
                    Toast.makeText(MainActivity.this, "you are an admin", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtra("role", userDetails.getRole());
                    intent.putExtra("uid", userDetails.getUid());
                    intent.putExtra("name", userDetails.getFirstName() + " " + userDetails.getLastName());
                    intent.putExtra("email", userDetails.getEmail());
                    intent.putExtra("block", userDetails.getBlock());
                    intent.putExtra("lot", userDetails.getLot());
                    intent.putExtra("street", userDetails.getStreet());
                    intent.putExtra("lavanyaPhaseType", userDetails.getLavanyaPhaseType());
                    intent.putExtra("image", userDetails.getImageUrl());

                    startActivity(intent);
                    setNormalState();
                    finish();
                }


            }

            @Override
            public void onFailure(String failed) {
                setNormalState();
                Toast.makeText(MainActivity.this, failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setLoadingState() {
        loginBtn.setEnabled(false);
        loginBtn.setAlpha(0.5f);
        loginBtn.setText("Logging in...");
    }

    private void setNormalState() {
        loginBtn.setEnabled(true);
        loginBtn.setAlpha(1f);
        loginBtn.setText("Login");
    }
}