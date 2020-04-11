package foodorder.dev.com.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import foodorder.dev.com.R;
import foodorder.dev.com.model.User;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout UserName, Password, Name, Surname, Email, PhoneNum;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SetUI();
    }

    private void SetUI() {

        Name = findViewById(R.id.TextInputLayoutName);
        Surname = findViewById(R.id.TextInputLayoutSurname);
        UserName = findViewById(R.id.TextInputLayoutUsername);
        Password = findViewById(R.id.TextInputLayoutPassword);
        Email = findViewById(R.id.TextInputLayoutEmail);
        PhoneNum = findViewById(R.id.TextInputLayoutPhoneNum);
    }

    public void btnSignUpOnClick(View view) {

        if (!validateName() | !validateSurname() | !validateUserName() | !validatePassword() | !validateEmail() | !validatePhoneNum()){
            return;
        }

        final String userName = UserName.getEditText().getText().toString();

        reference = FirebaseDatabase.getInstance().getReference("users");
        Query queryCheck = reference.orderByChild("username").equalTo(userName);

        queryCheck.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    UserName.setError("The username "+ "'"+ userName +"'"+ " already exist");
                }
                else{

                    String name = Name.getEditText().getText().toString();
                    String surname = Surname.getEditText().getText().toString();
                    String password = Password.getEditText().getText().toString();
                    String email = Email.getEditText().getText().toString();
                    String phoneNum = PhoneNum.getEditText().getText().toString();

                    Intent intent = new Intent(getApplicationContext(),VerifyPhoneNo.class);

                    intent.putExtra("name",name);
                    intent.putExtra("surname",surname);
                    intent.putExtra("userName",userName);
                    intent.putExtra("password",password);
                    intent.putExtra("email",email);
                    intent.putExtra("phoneNum",phoneNum);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Boolean validateName() {

        String value = Name.getEditText().getText().toString().trim();

        if (value.isEmpty()) {
            Name.setError("Field cannot be empty");
            return false;
        } else {
            Name.setError(null);
            Name.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateSurname() {

        String value = Surname.getEditText().getText().toString().trim();

        if (value.isEmpty()) {
            Surname.setError("Field cannot be empty");
            return false;
        } else {
            Surname.setError(null);
            Surname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUserName() {

        String value = UserName.getEditText().getText().toString().trim();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (value.isEmpty()) {
            UserName.setError("Field cannot be empty");
            return false;
        } else if (value.length() >= 15) {
            UserName.setError("Username is too long");
            return false;
        } else if (!value.matches(noWhiteSpace)) {
            UserName.setError("White spaces are not allowed");
            return false;
        } else {
            UserName.setError(null);
            UserName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {

        String value = Password.getEditText().getText().toString().trim();
        String passwordValue = "^" +
                //"(?=.*[0-9])" +  // at least 1 digit
                //"(?=.*[a-z])" +  // at least 1 lower case letter
                //"(?=.*[A-Z])" +  // at least 1 upper case letter
                "(?=.*[a-zA-Z])" + // any letter
                "(?=.*[@#$%^&+=_])" + // at least 1 special character
                "(?=\\S+$)" +        // no white space
                ".{6,}" +            // at least 6 characters
                "$";

        if (value.isEmpty()) {
            Password.setError("Field cannot be empty");
            return false;
        } else if (!value.matches(passwordValue)){
            Password.setError("Password is too weak");
            return false;
        }
        else {
            Password.setError(null);
            Password.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {

        String value = Email.getEditText().getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (value.isEmpty()) {
            Email.setError("Field cannot be empty");
            return false;
        } else if (!value.matches(emailPattern)){
            Email.setError("Invalid email address");
            return false;
        }
        else {
            Email.setError(null);
            Email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePhoneNum() {

        String value = PhoneNum.getEditText().getText().toString().trim();

        if (value.isEmpty()) {
            PhoneNum.setError("Field cannot be empty");
            return false;
        } else {
            PhoneNum.setError(null);
            PhoneNum.setErrorEnabled(false);
            return true;
        }
    }

    public void btnGoLogInPageOnClick(View view) {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
