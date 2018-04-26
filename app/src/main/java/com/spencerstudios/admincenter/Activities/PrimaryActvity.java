package com.spencerstudios.admincenter.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.spencerstudios.admincenter.Fragments.FragmentFlagged;
import com.spencerstudios.admincenter.Fragments.FragmentNotes;
import com.spencerstudios.admincenter.R;

public class PrimaryActvity extends AppCompatActivity {

    private int mode = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener changeListener;
    private ViewPager viewPager;


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_actvity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        viewPager = findViewById(R.id.container);

        viewPager.setBackgroundResource(sp.getBoolean("boobs", true) ? R.drawable.lady : 0);

        boolean subscribed = sp.getBoolean("subscribe", true);
        if (subscribed) {
            FirebaseMessaging.getInstance().subscribeToTopic("global");
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("global");
        }

        changeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("subscribe")) {
                    boolean sub = sp.getBoolean("subscribe", true);
                    if (sub) {
                        FirebaseMessaging.getInstance().subscribeToTopic("global");
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("global");
                    }
                } else if (key.equals("boobs")) {
                    boolean baps = sp.getBoolean("boobs", true);
                    if (baps) {
                        viewPager.setBackgroundResource(R.drawable.lady);
                    } else {
                        viewPager.setBackgroundResource(0);
                    }
                }
            }
        };

        sp.registerOnSharedPreferenceChangeListener(changeListener);
        final SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("has_signed_out", false).apply();

        LinearLayout rootLayout = findViewById(R.id.main_content);

        firebaseAuth = FirebaseAuth.getInstance();

        String fbu = firebaseAuth.getCurrentUser().getEmail();
        String user = fbu.substring(0, fbu.indexOf("@"));

        if (user.length() > 0) {
            Snackbar.make(rootLayout, "Signed is as '" + user + "'", Snackbar.LENGTH_LONG).show();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    editor.putBoolean("has_signed_out", true).apply();
                    startActivity(new Intent(PrimaryActvity.this, LogInActivity.class));
                    finish();
                } else {
                    Log.d("AUTH_STATE_LISTENER", "AUTH NOT NULL");
                }
            }
        };

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle("Flagged members");
        }

        TabLayout tabLayout = findViewById(R.id.tabs);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (getSupportActionBar() != null) {
                    if (position == 0) {
                        mode = 1;
                        getSupportActionBar().setSubtitle("Flagged members");
                    } else if (position == 1) {
                        mode = 2;
                        getSupportActionBar().setSubtitle("Group notes");
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add:
                if (mode == 1) {
                    startActivity(new Intent(PrimaryActvity.this, AddFlaggedMemberActivity.class));
                } else if (mode == 2) {
                    startActivity(new Intent(PrimaryActvity.this, AddNoteActivity.class));
                }
                break;

            case R.id.action_settings:
                //DialogBuilders.infoDialog(PrimaryActvity.this, "Settings", "This feature is yet to be added");
                startActivity(new Intent(PrimaryActvity.this, SettingsActivity.class));
                break;

            case R.id.action_sign_out:
                signOut();
                break;
        }
        return false;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("has_signed_out", true).apply();
    }

    public void onBackPressed() {
        sp.unregisterOnSharedPreferenceChangeListener(changeListener);
        firebaseAuth.removeAuthStateListener(authStateListener);
        finishAffinity();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentFlagged();
                case 1:
                    return new FragmentNotes();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}