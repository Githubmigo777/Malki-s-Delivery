package foodorder.dev.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import foodorder.dev.com.R;
import foodorder.dev.com.model.User;

public class VerifyPhoneNo extends AppCompatActivity {
    EditText verification_code_entered_by_user;
    Button verify_btn;
    ProgressBar progressBar;
    String verificationCodeBySystem;
    String name, surname, username, password, email, phoneNo;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);

        SetUI();
        sendVerificationCodeToUser(phoneNo);
    }

    private void sendVerificationCodeToUser(String phoneNo) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+994" + phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneNo.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredential(credential);
    }

    private void saveUserInfoToDataBase() {
        reference = FirebaseDatabase.getInstance().getReference("users");
        Query queryChecker = reference.orderByChild("username").equalTo(username);
        queryChecker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = new User(name, surname, username, password, email, phoneNo);
                reference.child(username).setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VerifyPhoneNo.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInTheUserByCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneNo.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            saveUserInfoToDataBase();

                            Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(VerifyPhoneNo.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SetUI() {

        verification_code_entered_by_user = findViewById(R.id.verification_code_entered_by_user);
        verify_btn = findViewById(R.id.verify_btn);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        name = getIntent().getStringExtra("name");
        surname = getIntent().getStringExtra("surname");
        username = getIntent().getStringExtra("userName");
        password = getIntent().getStringExtra("password");
        email = getIntent().getStringExtra("email");
        phoneNo = getIntent().getStringExtra("phoneNum");
    }

    public void btnVerifyPhoneNoOnClick(View view) {
        String code = verification_code_entered_by_user.getText().toString();
        if (code.isEmpty() || code.length() < 6) {
            verification_code_entered_by_user.setError("Wrong OTP..");
            verification_code_entered_by_user.requestFocus();
        }
        progressBar.setVisibility(View.VISIBLE);
        verifyCode(code);

    }
}
