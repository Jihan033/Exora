package com.example.exora.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.exora.model.EventModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExoraDB";
    private static final int DATABASE_VERSION = 8; // Incremented for user image column

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_STUDENT_ID = "student_id";
    public static final String COLUMN_USER_BIO = "bio";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_IMAGE = "image_uri";

    // Events Table
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    // Attendance Table
    public static final String TABLE_ATTENDANCE = "event_attendance";
    public static final String COLUMN_ATT_ID = "id";
    public static final String COLUMN_ATT_EVENT_ID = "event_id";
    public static final String COLUMN_ATT_USER_NAME = "user_name";
    public static final String COLUMN_ATT_STATUS = "status";

    // Notifications Table
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_NOTIF_ID = "id";
    public static final String COLUMN_NOTIF_TITLE = "title";
    public static final String COLUMN_NOTIF_MESSAGE = "message";
    public static final String COLUMN_NOTIF_TARGET = "target"; // ADMIN or USER
    public static final String COLUMN_NOTIF_IS_READ = "is_read";

    // Recruitment Config Table
    public static final String TABLE_RECRUITMENT_CONFIG = "recruitment_config";
    public static final String COLUMN_RC_ID = "id";
    public static final String COLUMN_RC_CLUB = "club_name";
    public static final String COLUMN_RC_DEADLINE = "deadline";
    public static final String COLUMN_RC_STATUS = "status";
    public static final String COLUMN_RC_DESC = "description";

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
    public static final String COLUMN_MEM_ROLE = "role"; 
    public static final String COLUMN_MEM_TYPE = "type"; 
    public static final String COLUMN_MEM_CLUB = "club_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_STUDENT_ID + " TEXT,"
                + COLUMN_USER_BIO + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_IMAGE + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_EVENTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_LOCATION + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_STATUS + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + "("
                + COLUMN_ATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ATT_EVENT_ID + " INTEGER,"
                + COLUMN_ATT_USER_NAME + " TEXT,"
                + COLUMN_ATT_STATUS + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIF_TITLE + " TEXT,"
                + COLUMN_NOTIF_MESSAGE + " TEXT,"
                + COLUMN_NOTIF_TARGET + " TEXT,"
                + COLUMN_NOTIF_IS_READ + " INTEGER DEFAULT 0" + ")");

        db.execSQL("CREATE TABLE " + TABLE_RECRUITMENT_CONFIG + "("
                + COLUMN_RC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RC_CLUB + " TEXT,"
                + COLUMN_RC_DEADLINE + " TEXT,"
                + COLUMN_RC_STATUS + " TEXT,"
                + COLUMN_RC_DESC + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_APPLICANTS + "("
                + COLUMN_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_APP_NAME + " TEXT,"
                + COLUMN_APP_DEPT + " TEXT,"
                + COLUMN_APP_STATUS + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_MEMBERS + "("
                + COLUMN_MEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEM_NAME + " TEXT,"
                + COLUMN_MEM_ROLE + " TEXT,"
                + COLUMN_MEM_TYPE + " TEXT,"
                + COLUMN_MEM_CLUB + " TEXT" + ")");

        addInitialData(db);
    }

    private void addInitialData(SQLiteDatabase db) {
        insertUser(db, "Alex Chen", "20210801045", "Active student interested in Robotics and Software Development.", "alex.chen@example.com");

        insertEvent(db, "Robotics Club Workshop", "Sep 11, 2024", "09:00 AM - 10:30 AM", "Engineering Lab 402", "Weekly workshop.", "Registration Open");
        insertEvent(db, "Student Union Board Meeting", "Sep 11, 2024", "01:00 PM - 02:00 PM", "Meeting Room B", "Weekly meeting.", "Ongoing");

        insertAttendance(db, 1, "Alex Chen", "Present");
        insertAttendance(db, 1, "Sarah Miller", "Late");
        insertAttendance(db, 1, "Jordan Smith", "Present");
        insertAttendance(db, 2, "Alex Chen", "Present");

        insertNotification(db, "Welcome to Exora!", "Start exploring organization activities.", "USER");
        insertNotification(db, "System Update", "New dashboard features are live.", "ADMIN");

        insertRecruitment(db, "Robotics Club", "Oct 15, 2024", "OPEN", "Join us to build future tech!");
        insertRecruitment(db, "Coding Society", "Oct 20, 2024", "OPEN", "Master fullstack development.");
        insertRecruitment(db, "OSIS Council", "Sep 30, 2024", "CLOSED", "Lead the student body.");

        insertApplicant(db, "Kevin Hart", "Hardware Dept", "PENDING");
        insertApplicant(db, "Mia Wong", "Programming", "PENDING");

        insertMember(db, "Alex Chen", "President", "ADMIN", "Robotics Club");
        insertMember(db, "Sarah Miller", "Treasurer", "MEMBER", "Robotics Club");
        insertMember(db, "Jordan Smith", "Lead Engineer", "MEMBER", "Robotics Club");
        insertMember(db, "Jessica Lee", "Lead Dev", "ADMIN", "Coding Society");
    }

    private void insertUser(SQLiteDatabase db, String name, String studentId, String bio, String email) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_NAME, name);
        v.put(COLUMN_USER_STUDENT_ID, studentId);
        v.put(COLUMN_USER_BIO, bio);
        v.put(COLUMN_USER_EMAIL, email);
        db.insert(TABLE_USERS, null, v);
    }

    private void insertEvent(SQLiteDatabase db, String name, String date, String time, String location, String description, String status) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_NAME, name); v.put(COLUMN_DATE, date); v.put(COLUMN_TIME, time);
        v.put(COLUMN_LOCATION, location); v.put(COLUMN_DESCRIPTION, description); v.put(COLUMN_STATUS, status);
        db.insert(TABLE_EVENTS, null, v);
    }

    private void insertAttendance(SQLiteDatabase db, int eventId, String userName, String status) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_ATT_EVENT_ID, eventId);
        v.put(COLUMN_ATT_USER_NAME, userName);
        v.put(COLUMN_ATT_STATUS, status);
        db.insert(TABLE_ATTENDANCE, null, v);
    }

    private void insertNotification(SQLiteDatabase db, String title, String message, String target) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_NOTIF_TITLE, title);
        v.put(COLUMN_NOTIF_MESSAGE, message);
        v.put(COLUMN_NOTIF_TARGET, target);
        db.insert(TABLE_NOTIFICATIONS, null, v);
    }

    private void insertRecruitment(SQLiteDatabase db, String club, String deadline, String status, String desc) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_RC_CLUB, club);
        v.put(COLUMN_RC_DEADLINE, deadline);
        v.put(COLUMN_RC_STATUS, status);
        v.put(COLUMN_RC_DESC, desc);
        db.insert(TABLE_RECRUITMENT_CONFIG, null, v);
    }

    public void addNotification(String title, String message, String target) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_NOTIF_TITLE, title);
        v.put(COLUMN_NOTIF_MESSAGE, message);
        v.put(COLUMN_NOTIF_TARGET, target);
        db.insert(TABLE_NOTIFICATIONS, null, v);
    }

    public Cursor getUnreadNotifications(String target) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_NOTIF_TARGET + " = ? AND " + COLUMN_NOTIF_IS_READ + " = 0", new String[]{target});
    }

    public void markNotificationsAsRead(String target) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_NOTIF_IS_READ, 1);
        db.update(TABLE_NOTIFICATIONS, v, COLUMN_NOTIF_TARGET + " = ?", new String[]{target});
    }

    public long joinEvent(int eventId, String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_ATT_EVENT_ID, eventId);
        v.put(COLUMN_ATT_USER_NAME, userName);
        v.put(COLUMN_ATT_STATUS, "Joined");
        
        long result = db.insert(TABLE_ATTENDANCE, null, v);
        if (result != -1) {
            addNotification("New Participant", userName + " joined an event.", "ADMIN");
        }
        return result;
    }

    public Cursor getEventParticipants(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ATTENDANCE, null, COLUMN_ATT_EVENT_ID + "=?", new String[]{String.valueOf(eventId)}, null, null, null);
    }

    public List<EventModel> getEventsForUser(String userName) {
        List<EventModel> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.* FROM " + TABLE_EVENTS + " e " +
                "JOIN " + TABLE_ATTENDANCE + " a ON e." + COLUMN_ID + " = a." + COLUMN_ATT_EVENT_ID + " " +
                "WHERE a." + COLUMN_ATT_USER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userName});
        if (cursor.moveToFirst()) {
            do {
                eventList.add(new EventModel(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventList;
    }

    private void insertApplicant(SQLiteDatabase db, String name, String dept, String status) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_APP_NAME, name); v.put(COLUMN_APP_DEPT, dept); v.put(COLUMN_APP_STATUS, status);
        db.insert(TABLE_APPLICANTS, null, v);
    }

    private void insertMember(SQLiteDatabase db, String name, String role, String type, String club) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_MEM_NAME, name); v.put(COLUMN_MEM_ROLE, role); v.put(COLUMN_MEM_TYPE, type); v.put(COLUMN_MEM_CLUB, club);
        db.insert(TABLE_MEMBERS, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECRUITMENT_CONFIG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICANTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBERS);
        onCreate(db);
    }

    // Users CRUD
    public Cursor getUser(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_NAME + " = ?", new String[]{name}, null, null, null);
    }

    public int updateUser(String oldName, String newName, String studentId, String bio, String email, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_NAME, newName);
        v.put(COLUMN_USER_STUDENT_ID, studentId);
        v.put(COLUMN_USER_BIO, bio);
        v.put(COLUMN_USER_EMAIL, email);
        v.put(COLUMN_USER_IMAGE, imageUri);
        
        // Also update name in other tables if necessary
        db.update(TABLE_ATTENDANCE, v, COLUMN_ATT_USER_NAME + " = ?", new String[]{oldName});
        db.update(TABLE_MEMBERS, v, COLUMN_MEM_NAME + " = ?", new String[]{oldName});
        
        return db.update(TABLE_USERS, v, COLUMN_USER_NAME + " = ?", new String[]{oldName});
    }

    // Members CRUD
    public long addMember(String name, String role, String type, String club) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_MEM_NAME, name); v.put(COLUMN_MEM_ROLE, role); v.put(COLUMN_MEM_TYPE, type); v.put(COLUMN_MEM_CLUB, club);
        return db.insert(TABLE_MEMBERS, null, v);
    }

    public Cursor getAllMembers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_MEMBERS, null);
    }
    
    public Cursor getMembersByClub(String clubName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MEMBERS, null, COLUMN_MEM_CLUB + " = ?", new String[]{clubName}, null, null, null);
    }

    public void deleteMember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMBERS, COLUMN_MEM_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public long addEvent(EventModel event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, event.getName());
        values.put(COLUMN_DATE, event.getDate());
        values.put(COLUMN_TIME, event.getTime());
        values.put(COLUMN_LOCATION, event.getLocation());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_STATUS, event.getStatus());
        long id = db.insert(TABLE_EVENTS, null, values);
        if (id != -1) {
            addNotification("New Agenda", "Admin added: " + event.getName(), "USER");
        }
        return id;
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
        // Also delete attendance for this event
        db.delete(TABLE_ATTENDANCE, COLUMN_ATT_EVENT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getOpenRecruitments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECRUITMENT_CONFIG, null, COLUMN_RC_STATUS + " = 'OPEN'", null, null, null, null);
    }
    
    public Cursor getUserClubs(String userName) {
        // Find clubs where this user is a member
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MEMBERS, new String[]{COLUMN_MEM_CLUB}, COLUMN_MEM_NAME + " = ?", new String[]{userName}, null, null, null);
    }

    public Cursor getRecruitmentConfig() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECRUITMENT_CONFIG, null, null, null, null, null, null);
    }

    public void updateRecruitmentStatus(String status, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_RC_STATUS, status);
        v.put(COLUMN_RC_DEADLINE, deadline);
        db.update(TABLE_RECRUITMENT_CONFIG, v, null, null);
    }

    public int getApplicantCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICANTS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public int getShortlistedCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICANTS + " WHERE " + COLUMN_APP_STATUS + " = 'ACCEPTED'", null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    public Cursor getApplicantsByStatus(String status) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPLICANTS, null, COLUMN_APP_STATUS + " = ?", new String[]{status}, null, null, null);
    }

    public void updateApplicantStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_APP_STATUS, status);
        db.update(TABLE_APPLICANTS, v, COLUMN_APP_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
