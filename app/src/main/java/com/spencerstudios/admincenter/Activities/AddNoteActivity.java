package com.spencerstudios.admincenter.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.Models.Note;
import com.spencerstudios.admincenter.Utilities.PrefUtils;
import com.spencerstudios.admincenter.Utilities.PushNotificationHelper;
import com.spencerstudios.admincenter.R;

public class AddNoteActivity extends AppCompatActivity {

    private EditText etSubject;
    private EditText etNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        etSubject = findViewById(R.id.et_note_subject);
        etNote = findViewById(R.id.et_note_body);
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
                submitNewNote();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void submitNewNote(){

        boolean legalForm = true;
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        String subject = etSubject.getText().toString().trim();
        String noteBody = etNote.getText().toString().trim();

        if (subject.isEmpty() || noteBody.isEmpty()){
            legalForm = false;
        }

        String author = "";

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
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            if (databaseReference != null) {
                String id = databaseReference.child(Consts.GROUP_NOTES).push().getKey();
                Note note = new Note(subject, noteBody, author, System.currentTimeMillis(), id);
                databaseReference.child(Consts.GROUP_NOTES).push().setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            etSubject.setText("");
                            etNote.setText("");
                            showMessage("successfully added new note");
                            PushNotificationHelper.sendPushNotification(PrefUtils.getUserPref(AddNoteActivity.this), "note");
                        } else {
                            showMessage("there was a problem adding new note to the database");
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
}
