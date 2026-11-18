package com.example.quanlylichhen;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Calendar;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {

    private EditText edtTitle, edtLocation;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SwitchCompat swReminder;
    private Button btnSave;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(R.string.title_add);
        }

        db = new DatabaseHelper(this);

        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        swReminder = findViewById(R.id.swReminder);
        btnSave = findViewById(R.id.btnSave);

        timePicker.setIs24HourView(true);

        btnSave.setOnClickListener(v -> saveAppointment());
    }

    private void saveAppointment() {
        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        if (title.isEmpty()) {
            edtTitle.setError(getString(R.string.err_title_empty));
            edtTitle.requestFocus();
            return;
        }
        if (location.isEmpty()) {
            edtLocation.setError(getString(R.string.err_location_empty));
            edtLocation.requestFocus();
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth();      // 0-11
        int day = datePicker.getDayOfMonth();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar chosen = Calendar.getInstance();
        chosen.set(year, month, day, hour, minute, 0);
        chosen.set(Calendar.MILLISECOND, 0);

        if (chosen.getTimeInMillis() <= System.currentTimeMillis()) {
            Toast.makeText(this, R.string.err_time_past, Toast.LENGTH_LONG).show();
            return;
        }

        String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        String time = String.format(Locale.US, "%02d:%02d", hour, minute);

        Appointment a = new Appointment(title, location, date, time, swReminder.isChecked());
        long id = db.insertAppointment(a);

        if (id > 0) {
            Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
            finish(); // quay về danh sách, onResume sẽ tải lại
        } else {
            Toast.makeText(this, R.string.msg_save_failed, Toast.LENGTH_SHORT).show();
        }
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
