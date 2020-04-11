package foodorder.dev.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import foodorder.dev.com.R;
import foodorder.dev.com.model.User;

public class LogInActivity extends AppCompatActivity {
    Button btnSignIn;
    TextInputLayout userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        SetUI();
    }

    private void SetUI() {

        btnSignIn = findViewById(R.id.signIn);
        userName = findViewById(R.id.editTxtUserName);
        password = findViewById(R.id.editTxtPassword);
    }


    public void btnSignInOnClick(View view) {

        if (!validateUserName() | !validatePassword()) {
            return;
        } else {
            isUser();
        }
    }

    private void isUser() {

        final String userEnteredUserName = userName.getEditText().getText().toString().trim();
        final String userEnteredPassword = password.getEditText().getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUserName);
        checkUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userName.setError(null);
                    userName.setErrorEnabled(false);

                    String passwordFromDb = dataSnapshot.child(userEnteredUserName).child("password").getValue(String.class);

                    if (passwordFromDb != null) {
                        if (passwordFromDb.equals(userEnteredPassword)) {

                            userName.setError(null);
                            userName.setErrorEnabled(false);

                            /*String nameFromDb = dataSnapshot.child(userEnteredUserName).child("name").getValue(String.class);
                            String surnameFromDb = dataSnapshot.child(userEnteredUserName).child("surname").getValue(String.class);
                            String phoneNumberFromDb = dataSnapshot.child(userEnteredUserName).child("phoneNumber").getValue(String.class);
                            String userNameFromDb = dataSnapshot.child(userEnteredUserName).child("username").getValue(String.class);*/

                            Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                            startActivity(intent);
                        } else {
                            password.setError("Wrong Password");
                            password.requestFocus();
                        }
                    }
                } else {
                    userName.setError("User doesn't exist");
                    userName.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private Boolean validateUserName() {

        String value = userName.getEditText().getText().toString();

        if (value.isEmpty()) {
            userName.setError("Field cannot be empty");
            return false;
        } else {
            userName.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {

        String value = password.getEditText().getText().toString();

        if (value.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public void btnRegisterOnClick(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
    }
}
