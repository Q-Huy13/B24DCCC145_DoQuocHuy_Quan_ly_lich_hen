package com.example.quanlylichhen;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvLocation, tvTime, tvReminderState;
    private SwitchCompat swReminder;
    private Button btnBack, btnDelete;

    private DatabaseHelper db;
    private int appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(R.string.title_detail);
        }

        db = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvTime = findViewById(R.id.tvDetailTime);
        tvReminderState = findViewById(R.id.tvReminderState);
        swReminder = findViewById(R.id.swDetailReminder);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        // Nhận id qua Intent, truy vấn lại CSDL theo id
        appointmentId = getIntent().getIntExtra(MainActivity.EXTRA_ID, -1);
        Appointment a = db.getAppointmentById(appointmentId);
        if (a == null) {
            Toast.makeText(this, R.string.msg_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(a.getTitle());
        tvLocation.setText(a.getLocation());
        tvTime.setText(a.getDisplayDateTime());

        // Đặt trạng thái ban đầu TRƯỚC khi gắn listener
        swReminder.setChecked(a.isReminder());
        updateReminderText(a.isReminder());

        swReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.updateReminder(appointmentId, isChecked);
            updateReminderText(isChecked);
        });

        btnBack.setOnClickListener(v -> finish());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void updateReminderText(boolean on) {
        tvReminderState.setText(on ? R.string.reminder_on : R.string.reminder_off);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_msg)
                .setPositiveButton(R.string.confirm_yes, (dialog, which) -> {
                    db.deleteAppointment(appointmentId);
                    Toast.makeText(this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.confirm_no, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
