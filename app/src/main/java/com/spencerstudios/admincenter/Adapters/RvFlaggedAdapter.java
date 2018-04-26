package com.spencerstudios.admincenter.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.Models.Member;
import com.spencerstudios.admincenter.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class RvFlaggedAdapter extends RecyclerView.Adapter<RvFlaggedAdapter.ItemHolder> {

    private ArrayList<Member> mList = new ArrayList<>();
    private DatabaseReference groupNotesReference;

    public RvFlaggedAdapter(ArrayList<Member> mList) {
        this.mList = mList;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        groupNotesReference = databaseReference.child(Consts.HIT_LIST_NODE);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_flagged_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, int position) {
        holder.tvName.setText(mList.get(position).getName());
        holder.tvReason.setText(mList.get(position).getReason());
        holder.flaggedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Context context = view.getContext();

                PopupMenu menu = new PopupMenu(context, view);

                if (mList.get(holder.getAdapterPosition()).isBanned()) {
                    menu.getMenu().add(Consts.FLAGGED_DELETE_MEMBER);
                } else {
                    menu.getMenu().add(Consts.FLAGGED_MARK_AS_BANNED);
                    menu.getMenu().add(Consts.FLAGGED_DELETE_MEMBER);
                }
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        final Query query = groupNotesReference.orderByChild("id").equalTo(mList.get(holder.getAdapterPosition()).getId());

                        if (menuItem.getTitle().equals(Consts.FLAGGED_MARK_AS_BANNED)) {
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().child("banned").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "member banner", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "there was a problem", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("QUERY ERROR", "onCancelled", databaseError.toException());
                                }
                            });

                        } else if (menuItem.getTitle().equals(Consts.FLAGGED_DELETE_MEMBER)) {

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("FLAGGED MEMBER", "SUCCESSFULLY REMOVED FROM FB");
                                                } else {
                                                    Toast.makeText(context, "there was a problem removing this member", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e("QUERY ERROR", "onCancelled", databaseError.toException());
                                }
                            });
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });

        long timeMillis = mList.get(position).getTimeStamp();
        holder.tvDatetime.setText(mList.get(position).getAuthor().concat("\n").concat(DateFormat.getDateInstance().format(timeMillis)));

        if (mList.get(holder.getAdapterPosition()).isBanned()) {
            holder.llRoot.setBackgroundColor(Color.parseColor(Consts.FLAGGED_MEMBER_BANNED_BG_COLOR));
            holder.llBanned.setVisibility(View.VISIBLE);
        } else {
            holder.llRoot.setBackgroundColor(Color.WHITE);
            holder.llBanned.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        LinearLayout llRoot, llBanned;
        TextView tvName;
        TextView tvReason;
        TextView tvDatetime;
        ImageView flaggedMenu;

        ItemHolder(View view) {
            super(view);

            llRoot = view.findViewById(R.id.ll_root_flagged);
            llBanned = view.findViewById(R.id.ll_banned_container);
            tvName = view.findViewById(R.id.tv_name);
            tvReason = view.findViewById(R.id.tv_reason);
            tvDatetime = view.findViewById(R.id.tv_datetime);
            flaggedMenu = view.findViewById(R.id.iv_flagged_menu);
        }
    }
}