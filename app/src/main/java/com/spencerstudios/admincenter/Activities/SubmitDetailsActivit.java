package com.spencerstudios.admincenter.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.Models.Member;
import com.spencerstudios.admincenter.R;

public class SubmitDetailsActivit extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private EditText etName, etOther;
    private TextInputLayout til;
    private RadioButton rad1, rad2, rad3, rad4, rad5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpViews();
    }

    private void showMessage(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_send:
                submitDetails();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitDetails() {
        String name = etName.getText().toString().trim();
        String reason = "";
        boolean otherReasonSelected = false;
        if (rad1.isChecked()) {
            reason = rad1.getText().toString();
        } else if (rad2.isChecked()) {
            reason = rad2.getText().toString();
        } else {
            otherReasonSelected = true;
            reason = etOther.getText().toString().trim();
        }
        boolean warned = rad4.isChecked();
        long time = System.currentTimeMillis();
        String author = "";

        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        boolean legalForm = true;

        if (name.isEmpty()) {
            legalForm = false;
        }
        if (otherReasonSelected) {
            if (etOther.getText().toString().isEmpty()) {
                legalForm = false;
            }
        }
        if (firebaseAuth != null) {
            if (firebaseAuth.getEmail() != null) {
                author = firebaseAuth.getEmail().substring(0, firebaseAuth.getEmail().indexOf("@"));
            } else {
                author = "unavailable";
            }
        } else {
            legalForm = false;
        }
        if (legalForm) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
            if (databaseReference != null) {
                Member member = new Member(name, reason, author, warned, time);
                databaseReference.child(Consts.HIT_LIST_NODE).push().setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessage("successfully added new member to the hit list");
                        } else {
                            showMessage("there was a problem adding member to the database");
                        }
                    }
                });
            } else {
                showMessage("an error has occurred");
            }
        } else {
            showMessage("one or more fields has missing information");
        }
    }

    private void setUpViews(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName = findViewById(R.id.et_name);
        etOther = findViewById(R.id.et_reason_other);
        til = findViewById(R.id.input_layout_other_reason);

        til.setVisibility(View.GONE);

        rad1 = findViewById(R.id.radio_button_one);
        rad2 = findViewById(R.id.radio_button_two);
        rad3 = findViewById(R.id.radio_button_3);
        rad4 = findViewById(R.id.radio_button_4);
        rad5 = findViewById(R.id.radio_button_5);

        rad1.setChecked(true);
        rad5.setChecked(true);

        rad3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                til.setVisibility(b ? View.VISIBLE : View.GONE);
                if (b) {
                    til.setVisibility(View.VISIBLE);
                    til.requestFocus();
                } else {
                    til.setVisibility(View.GONE);
                }
            }
        });
    }
}
