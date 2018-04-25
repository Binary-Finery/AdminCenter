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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spencerstudios.admincenter.Adapters.RvFlaggedAdapter;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.Models.Member;
import com.spencerstudios.admincenter.R;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentFlagged extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_hit_list, container, false);

        final ArrayList<Member> mList = new ArrayList<>();


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        DatabaseReference hitListReference = databaseReference.child(Consts.HIT_LIST_NODE);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        final RvFlaggedAdapter adapter = new RvFlaggedAdapter(mList);
        RecyclerView rv = view.findViewById(R.id.rv);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setNestedScrollingEnabled(false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        hitListReference.orderByChild(Consts.TIME_STAMP).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Member member = ds.getValue(Member.class);
                    if (member != null) {
                        mList.add(new Member(member.getName(), member.getReason(), member.getAuthor(), member.getTimeStamp(), member.isBanned(), member.getId()));
                    }
                }
                Collections.reverse(mList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("flagged frag db error: ", databaseError.getMessage());
            }
        });
        return view;
    }
}
