package com.example.letitgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.letitgo.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    ActivityOtpactivityBinding binding;
    String phoneNumber;
    FirebaseAuth auth;
    String verification;
    String TAG;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        binding.otpView.requestFocus();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP code");
        dialog.setCancelable(false);
        dialog.show();

        auth = FirebaseAuth.getInstance();
        TAG = "Shajidur";
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        binding.phoneVarify.setText("Verify " + phoneNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60l, TimeUnit.SECONDS)
                .setActivity(OTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(OTPActivity.this, "Complete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                        Log.d("Shajidur", "onVerificationFailed: " + e);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(OTPActivity.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        binding.otpView.requestFocus();
                        dialog.dismiss();
                        verification = s;
                    }
                }).build();


        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                 PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification, otp);

                 auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){
                             Intent intent = new Intent(OTPActivity.this, SetUpActivity.class);
                             startActivity(intent);
                             finishAffinity();
                         }
                         else {
                             Toast.makeText(OTPActivity.this, "No", Toast.LENGTH_SHORT).show();
                             Log.d(TAG, "onComplete: No");
                         }
                     }
                 });
            }
        });

    }
}