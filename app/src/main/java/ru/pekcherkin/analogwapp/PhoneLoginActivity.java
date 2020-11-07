package ru.pekcherkin.analogwapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private Button sendVerificationCodeButton, verifyButton;
    private EditText inputPhoneNumber, inputVerificationCode;

    private FirebaseAuth mAuth;
    ProgressDialog loadingBar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        sendVerificationCodeButton = (Button) findViewById(R.id.send_ver_code_button);
        verifyButton = (Button) findViewById(R.id.verify_button);

        inputPhoneNumber = (EditText) findViewById(R.id.phone_number_input);
        inputVerificationCode = (EditText) findViewById(R.id.verification_code_input);

        sendVerificationCodeButton.setOnClickListener(v -> {

            String phoneNumber = inputPhoneNumber.getText().toString();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            } else {

                loadingBar.setTitle("Проверка номера");
                loadingBar.setMessage("Подождите пожалуйста...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,       // Phone number to verify
                        60,
                        TimeUnit.SECONDS,
                        PhoneLoginActivity.this,
                        callbacks);

            }
        });

        verifyButton.setOnClickListener(v -> {

            sendVerificationCodeButton.setVisibility(View.INVISIBLE);
            inputPhoneNumber.setVisibility(View.INVISIBLE);

            String verificationCode = inputVerificationCode.getText().toString();
            if (TextUtils.isEmpty(verificationCode)) {
                Toast.makeText(this, "Введите код", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle("Проверка кода");
                loadingBar.setMessage("Подождите пожалуйста...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                signInWithPhoneAuthCredential(credential);
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Ошибка, введите коректный номер телефона...", Toast.LENGTH_SHORT).show();

                sendVerificationCodeButton.setVisibility(View.VISIBLE);
                inputPhoneNumber.setVisibility(View.VISIBLE);

                verifyButton.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);

                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Код отправлен на телефон", Toast.LENGTH_SHORT).show();

                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                inputPhoneNumber.setVisibility(View.INVISIBLE);

                verifyButton.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        } else {
String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Ошибка: "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}