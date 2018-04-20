package com.spencerstudios.admincenter.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spencerstudios.admincenter.Adapters.RvNotesAdapter;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.Models.Note;
import com.spencerstudios.admincenter.R;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentNotes extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_group_notes, container, false);


        final ArrayList<Note> notes = new ArrayList<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        DatabaseReference groupNotesReference = databaseReference.child(Consts.GROUP_NOTES);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        final RvNotesAdapter adapter = new RvNotesAdapter(notes);
        RecyclerView rv = view.findViewById(R.id.rv_notes);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setNestedScrollingEnabled(false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        groupNotesReference.orderByChild(Consts.TIME_STAMP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notes.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Note note = ds.getValue(Note.class);
                    if (note != null) {
                        notes.add(new Note(note.getSubject(), note.getNote(), note.getAuthor(), note.getTimeStamp(), note.getId()));
                    }
                }
                Collections.reverse(notes);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("notes frag db error: ", databaseError.getMessage());
            }
        });
        return view;
    }
}

