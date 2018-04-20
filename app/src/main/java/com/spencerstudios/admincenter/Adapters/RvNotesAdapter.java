package com.spencerstudios.admincenter.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spencerstudios.admincenter.Constants.Consts;
import com.spencerstudios.admincenter.Models.Note;
import com.spencerstudios.admincenter.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class RvNotesAdapter extends RecyclerView.Adapter<RvNotesAdapter.Holder> {

    private ArrayList<Note> notes;

    private DatabaseReference groupNotesReference;

    public RvNotesAdapter(ArrayList<Note> notes) {
        this.notes = notes;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        groupNotesReference = databaseReference.child(Consts.GROUP_NOTES);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_note_item, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        final String date = DateFormat.getDateInstance().format(notes.get(position).getTimeStamp());


        holder.author.setText(notes.get(position).getAuthor().concat("\n").concat(date));
        holder.tvSubject.setText(notes.get(position).getSubject());
        holder.tvNote.setText(notes.get(position).getNote());

        holder.noteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Context context = view.getContext();

                PopupMenu menu = new PopupMenu(context, view);
                menu.getMenu().add(Consts.NOTE_MENU_COPY);
                menu.getMenu().add(Consts.NOTE_MENU_DELETE);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals(Consts.NOTE_MENU_COPY)) {

                            int idx = holder.getAdapterPosition();
                            String text = notes.get(idx).getSubject().concat("\n\n").concat(notes.get(idx).getNote());

                            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("rv notes copy", text);
                            if (clipboard != null) {
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(context, "copied to clipboard", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "unable to copy to clipboard", Toast.LENGTH_SHORT).show();
                            }
                        } else if (menuItem.getTitle().equals(Consts.NOTE_MENU_DELETE)) {
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                            String user = sp.getString(Consts.PREF_CURRENT_USER, "");

                            if (user.equalsIgnoreCase(notes.get(holder.getAdapterPosition()).getAuthor())) {

                                Query query = groupNotesReference.orderByChild("id").equalTo(notes.get(holder.getAdapterPosition()).getId());

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("QUERY ERROR", "onCancelled", databaseError.toException());
                                    }
                                });
                            } else {
                                Toast.makeText(context, "only the note author may delete this note", Toast.LENGTH_LONG).show();
                            }
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        TextView tvSubject;
        TextView tvNote;
        TextView author;
        ImageView noteMenu;
        LinearLayout root;

        Holder(View view) {
            super(view);

            author = view.findViewById(R.id.tv_details);
            tvSubject = view.findViewById(R.id.tv_subject);
            tvNote = view.findViewById(R.id.tv_note);
            root = view.findViewById(R.id.ll_root_note_layout);
            noteMenu = view.findViewById(R.id.iv_note_menu);
        }
    }
}