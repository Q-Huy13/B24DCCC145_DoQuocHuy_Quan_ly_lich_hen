package com.example.quanlylichhen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "appointment_id";

    private TextView tvTotal, tvReminderCount, tvEmpty;
    private ListView lvAppointments;
    private Button btnAdd;

    private DatabaseHelper db;
    // ArrayList chỉ chứa kết quả truy vấn để hiển thị
    private final List<Appointment> data = new ArrayList<>();
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_main);

        db = new DatabaseHelper(this);

        tvTotal = findViewById(R.id.tvTotal);
        tvReminderCount = findViewById(R.id.tvReminderCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        lvAppointments = findViewById(R.id.lvAppointments);
        btnAdd = findViewById(R.id.btnAdd);

        adapter = new AppointmentAdapter(this, data);
        lvAppointments.setAdapter(adapter);
        lvAppointments.setEmptyView(tvEmpty);

        lvAppointments.setOnItemClickListener((parent, view, position, id) -> {
            Appointment a = data.get(position);
            Intent intent = new Intent(MainActivity.this, AppointmentDetailActivity.class);
            intent.putExtra(EXTRA_ID, a.getId());
            startActivity(intent);
        });

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddAppointmentActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // tải lại danh sách và thống kê sau khi thêm / sửa / xóa
    }

    private void loadData() {
        data.clear();
        data.addAll(db.getAllAppointments());
        adapter.notifyDataSetChanged();

        tvTotal.setText(String.valueOf(db.countAll()));
        tvReminderCount.setText(String.valueOf(db.countReminders()));
    }
}
