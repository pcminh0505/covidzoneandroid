package com.example.covidquarantinemanagement.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.databinding.ActivityLogInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityLogInBinding binding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationID;
    private ProgressDialog pd;

//    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneLayout.setVisibility(View.VISIBLE);
        binding.verificationLayout.setVisibility(View.GONE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Init process dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        String mystring = "Didn't get the OTP? Resend";
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        binding.resendOtp.setText(content);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                Log.d(TAG, "onVerificationCompleted:" + credential);
//
                signInWithAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                pd.dismiss();
                Toast.makeText(LogInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent: "+verificationId);

                mVerificationID = verificationId;
                forceResendingToken = token;
                pd.dismiss();

                // Hide phone layout
                binding.phoneLayout.setVisibility(View.GONE);
                binding.verificationLayout.setVisibility(View.VISIBLE);
                Toast.makeText(LogInActivity.this, "Verification code sent...", Toast.LENGTH_SHORT).show();

                binding.reminderText.setText("Please type the verification code we sent to \n +" + binding.inputPhone.getText().toString().trim());
            }
        };

        binding.getotpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get CountryCodePicker
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
                String code = ccp.getSelectedCountryCode();
                String phoneNumber = binding.inputPhone.getText().toString();
                String phone = "+" + code + phoneNumber;
                System.out.println(phone + "neeeeeeeeeee");
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(LogInActivity.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    startPhoneNumberVerification(phone);
                }
            }
        });

        binding.resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get CountryCodePicker
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
                String code = ccp.getSelectedCountryCode();
                String phoneNumber = binding.inputPhone.getText().toString();
                String phone = "+" + code + phoneNumber;
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(LogInActivity.this, "Please enter phone number...", Toast.LENGTH_SHORT).show();
                }
                else {
                    resendVerificationCode(phone, forceResendingToken);
                }
            }
        });

        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = binding.inputOtp.getText().toString().trim();
                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(LogInActivity.this, "Please enter verification code...", Toast.LENGTH_SHORT).show();
                }
                else {
                    verifyPhoneNumberWithCode(mVerificationID, otp);
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phone, PhoneAuthProvider.ForceResendingToken token) {
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String mVerificationID, String otp) {
        pd.setMessage("Verifying OTP");
        pd.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, otp);
        signInWithAuthCredential(credential);
    }

    private void signInWithAuthCredential(PhoneAuthCredential credential) {
        pd.setMessage("Loggin In");
        mAuth.signInWithCredential(credential)
                 .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                     @Override
                     public void onSuccess(AuthResult authResult) {
                         // Successfully signed in
                         pd.dismiss();
                         String phone = mAuth.getCurrentUser().getPhoneNumber();
                         Toast.makeText(LogInActivity.this, "Logged in as " + phone, Toast.LENGTH_SHORT).show();

                         // Start profile activity

                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         // Failed to sign in
                         pd.dismiss();
                         Toast.makeText(LogInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
////        updateUI(currentUser);
//    }
}