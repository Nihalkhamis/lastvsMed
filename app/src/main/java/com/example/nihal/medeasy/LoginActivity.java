package com.example.nihal.medeasy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nihal.medeasy.Utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;

public class LoginActivity extends AppCompatActivity {

    EditText loginPhone, loginPassWord;
    Button login;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPhone = findViewById(R.id.loginPhone);
        loginPassWord = findViewById(R.id.loginPassWord);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 final ProgressDialog loding = new ProgressDialog(LoginActivity.this);

                loding.setMessage("Loding please w8 ..."); // dh m4 btt7t fe el stringFile
                loding.setCancelable(false);

                String UserPhoneInFireB = loginPhone.getText().toString();
                String PassWordInFireB = loginPassWord.getText().toString();

                if (UserPhoneInFireB.isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.Toast_needPhone, Toast.LENGTH_SHORT).show();
                } else if (PassWordInFireB.equals("")) {
                    Toast.makeText(LoginActivity.this, R.string.Toast_needPassword, Toast.LENGTH_SHORT).show();
                } else {
                    loding.show();
                    UserPhoneInFireB = UserPhoneInFireB + "@gmail.com";
                    auth.signInWithEmailAndPassword(UserPhoneInFireB, PassWordInFireB).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                loding.dismiss();
                                Hawk.put(Constants.userID,user.getUid());
                                databaseReference=FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child(user.getUid()).child("Info").child("type").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String type = (String) dataSnapshot.getValue();
                                        Log.d("TTT", "onDataChange: "+type);
                                        if(type.equals("1")){
                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                                        }else{
                                            startActivity(new Intent(LoginActivity.this, DoctorLogin.class));

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                loding.show();
                                loding.dismiss();

                                {

                                    String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                    switch (errorCode) {

                                        case "ERROR_INVALID_CUSTOM_TOKEN":
                                            Toast.makeText(LoginActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                            Toast.makeText(LoginActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_INVALID_CREDENTIAL":
                                            Toast.makeText(LoginActivity.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_INVALID_EMAIL":
                                            //Toast.makeText(SignupActivity.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                            loginPhone.setError("The Phone number is badly formatted.");
                                            loginPhone.requestFocus();
                                            break;

                                        case "ERROR_WRONG_PASSWORD":
                                            //Toast.makeText(SignupActivity.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                                            loginPassWord.setError("password is incorrect ");
                                            loginPassWord.requestFocus();
                                            loginPassWord.setText("");
                                            break;

                                        case "ERROR_USER_MISMATCH":
                                            Toast.makeText(LoginActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_REQUIRES_RECENT_LOGIN":
                                            Toast.makeText(LoginActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                            Toast.makeText(LoginActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            //Toast.makeText(SignupActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                                            loginPhone.setError("The email address is already in use by another account.");
                                            loginPhone.requestFocus();
                                            break;

                                        case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                            Toast.makeText(LoginActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_USER_DISABLED":
                                            Toast.makeText(LoginActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_USER_TOKEN_EXPIRED":
                                            Toast.makeText(LoginActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_USER_NOT_FOUND":
                                            Toast.makeText(LoginActivity.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_INVALID_USER_TOKEN":
                                            Toast.makeText(LoginActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_OPERATION_NOT_ALLOWED":
                                            Toast.makeText(LoginActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                            break;

                                        case "ERROR_WEAK_PASSWORD":
                                            //Toast.makeText(SignupActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                                            loginPassWord.setError("The password is invalid it must 6 characters at least");
                                            loginPassWord.requestFocus();
                                            break;
                                    }
                                }
                                }
                        }
                    });
                }
            }
        });


    }

    /**
     * to going to signUpForm
     **/

    public void signUpForm(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

}
