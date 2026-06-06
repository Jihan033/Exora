package com.example.exora.admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExoraDB";
    private static final int DATABASE_VERSION = 3; // Incremented version again

    // Events Table
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    // Recruitment Config Table
    public static final String TABLE_RECRUITMENT_CONFIG = "recruitment_config";
    public static final String COLUMN_RC_ID = "id";
    public static final String COLUMN_RC_CLUB = "club_name";
    public static final String COLUMN_RC_DEADLINE = "deadline";
    public static final String COLUMN_RC_STATUS = "status";

    // Applicants Table
    public static final String TABLE_APPLICANTS = "applicants";
    public static final String COLUMN_APP_ID = "id";
    public static final String COLUMN_APP_NAME = "name";
    public static final String COLUMN_APP_DEPT = "department";
    public static final String COLUMN_APP_STATUS = "status";

    // Members Table
    public static final String TABLE_MEMBERS = "members";
    public static final String COLUMN_MEM_ID = "id";
    public static final String COLUMN_MEM_NAME = "name";
    public static final String COLUMN_MEM_ROLE = "role"; // President, Treasurer, etc.
    public static final String COLUMN_MEM_TYPE = "type"; // ADMIN, MEMBER

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_STATUS + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_RECRUITMENT_CONFIG + "("
                + COLUMN_RC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RC_CLUB + " TEXT,"
                + COLUMN_RC_DEADLINE + " TEXT,"
                + COLUMN_RC_STATUS + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_APPLICANTS + "("
                + COLUMN_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_APP_NAME + " TEXT,"
                + COLUMN_APP_DEPT + " TEXT,"
                + COLUMN_APP_STATUS + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_MEMBERS + "("
                + COLUMN_MEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEM_NAME + " TEXT,"
                + COLUMN_MEM_ROLE + " TEXT,"
                + COLUMN_MEM_TYPE + " TEXT" + ")");

        addInitialData(db);
    }

    private void addInitialData(SQLiteDatabase db) {
        insertEvent(db, "Robotics Club Workshop", "Sep 11, 2024", "09:00 AM - 10:30 AM", "Engineering Lab 402", "Weekly workshop.", "Registration Open");
        insertEvent(db, "Student Union Board Meeting", "Sep 11, 2024", "01:00 PM - 02:00 PM", "Meeting Room B", "Weekly meeting.", "Ongoing");

        ContentValues rcValues = new ContentValues();
        rcValues.put(COLUMN_RC_CLUB, "Robotics Club");
        rcValues.put(COLUMN_RC_DEADLINE, "Oct 15, 2024");
        rcValues.put(COLUMN_RC_STATUS, "OPEN");
        db.insert(TABLE_RECRUITMENT_CONFIG, null, rcValues);

        insertApplicant(db, "Kevin Hart", "Hardware Dept", "PENDING");
        insertApplicant(db, "Mia Wong", "Programming", "PENDING");

        insertMember(db, "Alex Chen", "President", "ADMIN");
        insertMember(db, "Sarah Miller", "Treasurer", "MEMBER");
        insertMember(db, "Jordan Smith", "Lead Engineer", "MEMBER");
    }

    private void insertEvent(SQLiteDatabase db, String name, String date, String time, String location, String description, String status) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_NAME, name); v.put(COLUMN_DATE, date); v.put(COLUMN_TIME, time);
        v.put(COLUMN_LOCATION, location); v.put(COLUMN_DESCRIPTION, description); v.put(COLUMN_STATUS, status);
        db.insert(TABLE_EVENTS, null, v);
    }

    private void insertApplicant(SQLiteDatabase db, String name, String dept, String status) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_APP_NAME, name); v.put(COLUMN_APP_DEPT, dept); v.put(COLUMN_APP_STATUS, status);
        db.insert(TABLE_APPLICANTS, null, v);
    }

    private void insertMember(SQLiteDatabase db, String name, String role, String type) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_MEM_NAME, name); v.put(COLUMN_MEM_ROLE, role); v.put(COLUMN_MEM_TYPE, type);
        db.insert(TABLE_MEMBERS, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECRUITMENT_CONFIG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        onCreate(db);
    }

    // Members CRUD
    public long addMember(String name, String role, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_MEM_NAME, name); v.put(COLUMN_MEM_ROLE, role); v.put(COLUMN_MEM_TYPE, type);
        return db.insert(TABLE_MEMBERS, null, v);
    }

    public Cursor getAllMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MEMBERS, null);
    }

    public void deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMBERS, COLUMN_MEM_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // Existing CRUD... (Events & Applicants)
    public long addEvent(EventModel event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DATE, event.getDate());
        values.put(COLUMN_TIME, event.getTime());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_STATUS, event.getStatus());
        return db.insert(TABLE_EVENTS, null, values);
    }

    public int updateEvent(EventModel event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DATE, event.getDate());
        values.put(COLUMN_TIME, event.getTime());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_STATUS, event.getStatus());
        return db.update(TABLE_EVENTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(event.getId())});
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public List<EventModel> getAllEvents() {
        List<EventModel> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        if (cursor.moveToFirst()) {
            do {
                eventList.add(new EventModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    public EventModel getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            EventModel event = new EventModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            cursor.close();
            return event;
        }
        return null;
    }

    public void updateRecruitmentStatus(String status, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RC_STATUS, status);
        values.put(COLUMN_RC_DEADLINE, deadline);
        db.update(TABLE_RECRUITMENT_CONFIG, values, COLUMN_RC_ID + " = 1", null);
    }

    public Cursor getRecruitmentConfig() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_RECRUITMENT_CONFIG + " WHERE id = 1", null);
    }

    public Cursor getApplicantsByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPLICANTS, null, COLUMN_APP_STATUS + "=?", new String[]{status}, null, null, null);
    }

    public void updateApplicantStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_APP_STATUS, status);
        db.update(TABLE_APPLICANTS, values, COLUMN_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public int getApplicantCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICANTS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getShortlistedCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICANTS + " WHERE status='ACCEPTED'", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}
