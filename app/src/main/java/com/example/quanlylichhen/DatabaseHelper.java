package com.example.quanlylichhen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "appointments.db";
    public static final int DB_VERSION = 1;

    // Bảng users
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Bảng appointments
    private static final String TABLE_APPT = "appointments";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_LOCATION = "location";
    private static final String COL_DATE = "date";
    private static final String COL_TIME = "time";
    private static final String COL_REMINDER = "reminder";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_APPT + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT NOT NULL, "
                + COL_LOCATION + " TEXT NOT NULL, "
                + COL_DATE + " TEXT NOT NULL, "
                + COL_TIME + " TEXT NOT NULL, "
                + COL_REMINDER + " INTEGER NOT NULL)");

        // Dữ liệu mẫu: tài khoản
        insertUser(db, "admin", "123456");

        // Dữ liệu mẫu: 3 lịch hẹn
        insertAppointment(db, new Appointment("Họp nhóm đồ án", "Phòng B203", "2026-10-25", "09:00", true));
        insertAppointment(db, new Appointment("Khám răng", "Nha khoa Minh Tâm", "2026-10-26", "14:30", true));
        insertAppointment(db, new Appointment("Đi cà phê với bạn", "Highland Coffee", "2026-10-27", "19:00", false));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ------------------------------------------------------------------
    // USERS
    // ------------------------------------------------------------------

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ID + " FROM " + TABLE_USERS
                        + " WHERE " + COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                new String[]{username, password});
        boolean ok = cursor.getCount() > 0;
        cursor.close();
        return ok;
    }

    // ------------------------------------------------------------------
    // APPOINTMENTS
    // ------------------------------------------------------------------

    private ContentValues toValues(Appointment a) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, a.getTitle());
        values.put(COL_LOCATION, a.getLocation());
        values.put(COL_DATE, a.getDate());
        values.put(COL_TIME, a.getTime());
        values.put(COL_REMINDER, a.isReminder() ? 1 : 0);
        return values;
    }

    private long insertAppointment(SQLiteDatabase db, Appointment a) {
        return db.insert(TABLE_APPT, null, toValues(a));
    }

    /** Thêm lịch hẹn, trả về id vừa thêm (-1 nếu lỗi). */
    public long insertAppointment(Appointment a) {
        return insertAppointment(getWritableDatabase(), a);
    }

    private Appointment fromCursor(Cursor c) {
        return new Appointment(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_TITLE)),
                c.getString(c.getColumnIndexOrThrow(COL_LOCATION)),
                c.getString(c.getColumnIndexOrThrow(COL_DATE)),
                c.getString(c.getColumnIndexOrThrow(COL_TIME)),
                c.getInt(c.getColumnIndexOrThrow(COL_REMINDER)) == 1);
    }

    /** Lấy toàn bộ lịch hẹn, sắp xếp theo thời gian gần nhất trước. */
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_APPT
                        + " ORDER BY " + COL_DATE + " ASC, " + COL_TIME + " ASC", null);
        if (cursor.moveToFirst()) {
            do {
                list.add(fromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /** Lấy một lịch hẹn theo id, trả về null nếu không tồn tại. */
    public Appointment getAppointmentById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_APPT + " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)});
        Appointment a = null;
        if (cursor.moveToFirst()) {
            a = fromCursor(cursor);
        }
        cursor.close();
        return a;
    }

    public int updateReminder(int id, boolean on) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REMINDER, on ? 1 : 0);
        return db.update(TABLE_APPT, values, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int deleteAppointment(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_APPT, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int countAll() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPT, null);
        int n = 0;
        if (cursor.moveToFirst()) {
            n = cursor.getInt(0);
        }
        cursor.close();
        return n;
    }

    public int countReminders() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_APPT + " WHERE " + COL_REMINDER + " = ?",
                new String[]{"1"});
        int n = 0;
        if (cursor.moveToFirst()) {
            n = cursor.getInt(0);
        }
        cursor.close();
        return n;
    }
}
