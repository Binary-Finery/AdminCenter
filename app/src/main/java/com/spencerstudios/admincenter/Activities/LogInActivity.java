package com.spencerstudios.admincenter.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.R;
import com.spencerstudios.admincenter.Utilities.PrefUtils;

import java.util.Random;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient mGoogleApiClient;
    private final static int RC_SIGN_IN = 2;

    private LinearLayout llSignInContainer;
    private ImageView loginPic;

    boolean isSignedIn = false;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginPic = findViewById(R.id.log_in_pic);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loginPic.setImageResource(R.mipmap.ic_launcher);
            }
        },1000);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(getResources().getString(R.string.toolbar_subtitle));
        }
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    isSignedIn = true;
                    Log.d("AUTH STATE", "CURRENT USER != NULL");
                } else {
                    Log.d("AUTH STATE", "NULL");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(LogInActivity.this)
                .enableAutoManage(LogInActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LogInActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                        llSignInContainer.setVisibility(View.VISIBLE);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        llSignInContainer = findViewById(R.id.ll_sign_in_container);
        llSignInContainer.setVisibility(View.INVISIBLE);

        SignInButton signInButton = findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasSignedOut = sp.getBoolean("has_signed_out", true);

        if(hasSignedOut){
            llSignInContainer.setVisibility(View.VISIBLE);
        }else{
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                fireBaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LogInActivity.this, "an error has occurred", Toast.LENGTH_SHORT).show();
                llSignInContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LogInActivity.this, PrimaryActvity.class));
                            FirebaseUser fbu = FirebaseAuth.getInstance().getCurrentUser();
                            String email = "";
                            if (fbu != null) {
                                email = fbu.getEmail().substring(0, fbu.getEmail().indexOf("@"));
                            }
                            PrefUtils.setUserPref(LogInActivity.this, email);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException() != null ? task.getException().toString() : getResources().getString(R.string.error_message), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onBackPressed(){
        finishAffinity();
    }
}