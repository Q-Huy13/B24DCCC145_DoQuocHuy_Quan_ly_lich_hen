package com.example.quanlylichhen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    public AppointmentAdapter(@NonNull Context context, @NonNull List<Appointment> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_appointment, parent, false);
        }
        Appointment a = getItem(position);
        if (a == null) return view;

        TextView tvTitle = view.findViewById(R.id.tvItemTitle);
        TextView tvLocation = view.findViewById(R.id.tvItemLocation);
        TextView tvTime = view.findViewById(R.id.tvItemTime);
        TextView tvTag = view.findViewById(R.id.tvItemTag);
        View viewBar = view.findViewById(R.id.viewBar);

        tvTitle.setText(a.getTitle());
        tvLocation.setText(getContext().getString(R.string.location_prefix, a.getLocation()));
        tvTime.setText(a.getDisplayDateTime());

        if (a.isReminder()) {
            tvTag.setText(R.string.tag_reminder_on);
            tvTag.setBackgroundResource(R.drawable.bg_chip_on);
            viewBar.setBackgroundResource(R.drawable.bg_bar_on);
            tvTag.setTextColor(getContext().getResources().getColor(R.color.remind_text));
        } else {
            tvTag.setText(R.string.tag_reminder_off);
            tvTag.setBackgroundResource(R.drawable.bg_chip_off);
            viewBar.setBackgroundResource(R.drawable.bg_bar_off);
            tvTag.setTextColor(getContext().getResources().getColor(R.color.gray_text));
        }
        return view;
    }
}
