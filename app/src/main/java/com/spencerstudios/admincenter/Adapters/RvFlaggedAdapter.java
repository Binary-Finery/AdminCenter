package com.spencerstudios.admincenter.Adapters;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spencerstudios.admincenter.Models.Member;
import com.spencerstudios.admincenter.R;

import java.text.DateFormat;
import java.util.ArrayList;

public class RvFlaggedAdapter extends RecyclerView.Adapter<RvFlaggedAdapter.ItemHolder> {

    ArrayList<Member> mList = new ArrayList<>();

    public RvFlaggedAdapter(ArrayList<Member> mList) {
        this.mList = mList;
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
        holder.cbWarned.setChecked(mList.get(position).isAlreadyWarned());

        long timeMillis = mList.get(position).getTimeStamp();

        String date = DateFormat.getDateTimeInstance().format(timeMillis);

        holder.tvDatetime.setText(mList.get(position).getAuthor().concat("\n").concat(date));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvReason;
        TextView tvDatetime;
        AppCompatCheckBox cbWarned;

        ItemHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tv_name);
            tvReason = view.findViewById(R.id.tv_reason);
            tvDatetime = view.findViewById(R.id.tv_datetime);
            cbWarned = view.findViewById(R.id.cb_warned);
        }
    }
}