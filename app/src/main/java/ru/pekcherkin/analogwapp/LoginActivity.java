package ru.pekcherkin.analogwapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.OnClick;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button loginButton, phoneLoginButton;
    private EditText userEmail, userPassword;
    private TextView needNewAccountLink, forgetPasswordLink;
    private ProgressDialog loadingBar;

//    @BindView(R.id.need_new_account_link)
//    private TextView needNewAccountLink;
//
//    @OnClick(R.id.need_new_account_link)
//    void needNewAccountLinkClick(){
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        initializeFields();

        needNewAccountLink.setOnClickListener(v -> {
            sendUserToRegisterActivity();
        });

        loginButton.setOnClickListener(v -> {
            allowUserToLogin();
        });

        phoneLoginButton.setOnClickListener(v -> {
            Intent phoneLoginIntent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
            startActivity(phoneLoginIntent);
        });

    }

    private void allowUserToLogin() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Введите адрес электронной почты", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Вход");
            loadingBar.setMessage("Пожалуйста подождите...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendUserToMainActivity();
                            Toast.makeText(LoginActivity.this, "Успешный вход...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    });
        }
    }

    private void initializeFields() {
        loginButton = (Button) findViewById(R.id.login_button);
        phoneLoginButton = (Button) findViewById(R.id.phone_login_button);
        userEmail = (EditText) findViewById(R.id.login_email);
        userPassword = (EditText) findViewById(R.id.login_password);
        needNewAccountLink = (TextView) findViewById(R.id.need_new_account_link);
        forgetPasswordLink = (TextView) findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
    }



    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}