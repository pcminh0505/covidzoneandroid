package com.example.covidquarantinemanagement.Activity;

import static android.content.ContentValues.TAG;
import com.example.covidquarantinemanagement.Util.DatabaseHandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.databinding.ActivityLogInBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityLogInBinding binding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationID;
    private ProgressDialog pd;
    private boolean isLogIn;

    // Setup Firestore database
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupUI(binding.getRoot());

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

        String mystring1 = "Didn't have an account? Sign up for one";
        SpannableString content1 = new SpannableString(mystring1);
        content1.setSpan(new UnderlineSpan(), 0, mystring1.length(), 0);
        binding.signUpText.setText(content1);

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

                // Get CountryCodePicker
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
                String code = ccp.getSelectedCountryCode();
                binding.reminderText.setText("Please type the verification code we sent to \n +" + code + binding.inputPhone.getText().toString().trim());
            }
        };

        binding.getotpbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get CountryCodePicker
                CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.countryCodePicker);
                String code = ccp.getSelectedCountryCode();
                String phoneNumber = binding.inputPhone.getText().toString();
                String userName = binding.userName.getText().toString();
                String phone = "+" + code + phoneNumber;
                System.out.println(phone + "neeeeeeeeeee");

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(LogInActivity.this, "Please your phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    ArrayList<Integer> results = new ArrayList<>();
                    DatabaseHandler.checkRegisteredUser(db, pd, phone, results);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        // False: Not in the dtb
                         if (results.get(0) == 1) {
                            isLogIn = false;
                            Toast.makeText(LogInActivity.this, "User hasn't been registered. Please enter your name to create an account", Toast.LENGTH_SHORT).show();
                         }
                        else { isLogIn = true; }
                    }, 5000);
                    System.out.println(results);
                    Toast.makeText(LogInActivity.this,"isLogIn = " + isLogIn, Toast.LENGTH_SHORT).show();
                    if ((isLogIn == false) && (TextUtils.isEmpty(userName)) && (binding.userName.getVisibility() == View.VISIBLE)) {
                        Toast.makeText(LogInActivity.this, "Please enter all the input field(s)...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        startPhoneNumberVerification(phone);
                    }
                }
            }
        });

        binding.signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signUpBar.setVisibility(View.VISIBLE);
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
        mAuth.signInWithCredential(credential)
                 .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                     @Override
                     public void onSuccess(AuthResult authResult) {
                         // Successfully signed in
                         pd.dismiss();
                         String phone = mAuth.getCurrentUser().getPhoneNumber();
                         String uid = mAuth.getUid();
                         String name = binding.userName.getText().toString().trim();

                         if (isLogIn == false) {
                             // TODO: Create user
                             // set title of progress bar
                             pd.setMessage("Signing up ...");
                             DatabaseHandler.createUserOnDatabase(db,LogInActivity.this,pd,uid,name,phone);
                         } else {
                             pd.setMessage("Logging In");
                             Toast.makeText(LogInActivity.this, "Logged in as " + phone, Toast.LENGTH_SHORT).show();
                         }
                         // Start profile activity
                         Intent i = new Intent(LogInActivity.this, MapsActivity.class);
                         setResult(RESULT_OK, i);
                         finish();
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

    private void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LogInActivity.this);
                    return false;
                }
            });
        }
    }
    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}