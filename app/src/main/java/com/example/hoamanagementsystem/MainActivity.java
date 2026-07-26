package com.example.hoamanagementsystem;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDatabaseManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.LoginUserCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserDatasCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Modules.HomePage;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.utls.AppSettingsManager;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView createAccountLink;
    private EditText emailET, passwordET;
    private Button loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createAccountLink = findViewById(R.id.createAccountLink);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn =findViewById(R.id.loginBtn);


        createAccountLink.setOnClickListener(s -> {
            navigateTo(SignupPage.class);
        });

        loginBtn.setOnClickListener(g -> {
            loginUser();
        });

        checkAppAvailability();
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

        // Step 1: Check app availability FIRST, before attempting login at all
        AppSettingsManager.checkOperatingHours(new AppSettingsManager.OperatingHoursCallback() {
            @Override
            public void onResult(boolean isOpen, int openHour, int closeHour) {
                if (!isOpen) {
                    setNormalState();
                    FirebaseAuthManager.logout();
                    showAppClosedDialog(openHour, closeHour);
                    return; // stop here, don't even attempt login
                }

                // Step 2: App is open, now actually attempt login
                attemptLogin(email, password);
            }

            @Override
            public void onFailure(String error) {
                setNormalState();
                FirebaseAuthManager.logout();
                Toast.makeText(MainActivity.this, "Unable to verify app status. Please check your connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void attemptLogin(String email, String password) {
        FirebaseAuthManager.loginUser(email, password, new LoginUserCallback() {
            @Override
            public void onSuccess(FirebaseUser user, HomeOwnerRentersModel userDetails) {

                if(userDetails.getIsAccountApprovedByAdmin().equals("no")) {
                    setNormalState();
                    showPendingApprovalDialog();
                    FirebaseAuthManager.logout();
                    return;
                }

                if(userDetails.getIsAccountDisabled().equals("yes")) {
                    setNormalState();
                    showAccountDisabledDialog();
                    FirebaseAuthManager.logout();
                    return;
                }

                if(userDetails.getIsAccountBanned().equals("yes")) {
                    setNormalState();
                    showAccountBannedDialog();
                    FirebaseAuthManager.logout();
                    return;
                }

                UserSession.getInstance().setCurrentUser(userDetails);

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
    private void autoLoginUser() {
        if(FirebaseAuthManager.getCurrentUser() == null) {
            return;
        }
        String uid = FirebaseAuthManager.getCurrentUserUid();

        FirebaseDatabaseManager.getUserDatas(uid, new UserDatasCallback() {
            @Override
            public void onSuccess(HomeOwnerRentersModel user) {

                if(user.getIsAccountApprovedByAdmin().equals("no")) {
                    setNormalState();
                    showPendingApprovalDialog();
                    FirebaseAuthManager.logout();
                    return;
                }

                if(user.getIsAccountDisabled().equals("yes")) {
                    setNormalState();
                    showAccountDisabledDialog();
                    FirebaseAuthManager.logout();
                    return;
                }

                if(user.getIsAccountBanned().equals("yes")) {
                    setNormalState();
                    showAccountBannedDialog();
                    FirebaseAuthManager.logout();
                    return;
                }

                UserSession.getInstance().setCurrentUser(user);
                if(user.getRole().equals("Admin")) {

                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtra("role", user.getRole());
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("name", user.getFirstName() + " " + user.getLastName());
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("block", user.getBlock());
                    intent.putExtra("lot", user.getLot());
                    intent.putExtra("street", user.getStreet());
                    intent.putExtra("lavanyaPhaseType", user.getLavanyaPhaseType());
                    intent.putExtra("image", user.getImageUrl());

                    startActivity(intent);

                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtra("role", user.getRole());
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("name", user.getFirstName() + " " + user.getLastName());
                    intent.putExtra("email", user.getEmail());
                    intent.putExtra("block", user.getBlock());
                    intent.putExtra("lot", user.getLot());
                    intent.putExtra("street", user.getStreet());
                    intent.putExtra("lavanyaPhaseType", user.getLavanyaPhaseType());
                    intent.putExtra("image", user.getImageUrl());

                    startActivity(intent);

                    finish();
                }

            }

            @Override
            public void onFailure(String message) {
            }
        });

    }
    private void showPendingApprovalDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pending_approval, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button okBtn = dialogView.findViewById(R.id.pendingOkBtn);
        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void showAccountDisabledDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account_disabled, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button okBtn = dialogView.findViewById(R.id.disabledOkBtn);
        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void showAccountBannedDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_account_banned, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button okBtn = dialogView.findViewById(R.id.bannedOkBtn);
        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void showAppClosedDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_app_closed, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button okBtn = dialogView.findViewById(R.id.closedOkBtn);
        okBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    private void checkAppAvailability() {
        AppSettingsManager.checkOperatingHours(new AppSettingsManager.OperatingHoursCallback() {
            @Override
            public void onResult(boolean isOpen, int openHour, int closeHour) {
                if (isOpen) {
                    autoLoginUser();
                } else {
                    showAppClosedDialog(openHour, closeHour);

                }
            }

            @Override
            public void onFailure(String error) {
                // Fail open — don't lock users out over a network/read error
                autoLoginUser();
            }
        });
    }



    private void proceedToMainActivity() {
        startActivity(new Intent(MainActivity.this, MainActivity.class));
        finish();
    }
    private void showAppClosedDialog(int openHour, int closeHour) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_app_closed, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView message = dialogView.findViewById(R.id.closedMessage);
        message.setText(String.format(Locale.getDefault(),
                "The app is available from %s to %s daily. Please come back within our operating hours.",
                formatHour(openHour), formatHour(closeHour)));

        Button okBtn = dialogView.findViewById(R.id.closedOkBtn);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity();
            return;
        });

        dialog.show();
    }

    private String formatHour(int hour24) {
        int hour12 = hour24 % 12 == 0 ? 12 : hour24 % 12;
        String period = hour24 < 12 ? "AM" : "PM";
        return hour12 + ":00 " + period;
    }
}