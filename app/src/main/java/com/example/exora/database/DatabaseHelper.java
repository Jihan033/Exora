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
    private static final int DATABASE_VERSION = 24; // Rollback version

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_ATTENDANCE = "event_attendance";
    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String TABLE_RECRUITMENT_CONFIG = "recruitment_config";
    public static final String TABLE_APPLICANTS = "applicants";
    public static final String TABLE_MEMBERS = "members";

    // Columns - Users (Simplified Rollback)
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_STUDENT_ID = "student_id";
    public static final String COLUMN_USER_BIO = "bio";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_IMAGE = "image_uri";

    // Columns - Events
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_STATUS = "status";

    // Columns - Attendance
    public static final String COLUMN_ATT_ID = "id";
    public static final String COLUMN_ATT_EVENT_ID = "event_id";
    public static final String COLUMN_ATT_USER_NAME = "user_name";
    public static final String COLUMN_ATT_STATUS = "status";

    // Columns - Notifications
    public static final String COLUMN_NOTIF_ID = "id";
    public static final String COLUMN_NOTIF_TITLE = "title";
    public static final String COLUMN_NOTIF_MESSAGE = "message";
    public static final String COLUMN_NOTIF_TARGET = "target";
    public static final String COLUMN_NOTIF_IS_READ = "is_read";

    // Columns - Recruitment
    public static final String COLUMN_RC_ID = "id";
    public static final String COLUMN_RC_CLUB = "club_name";
    public static final String COLUMN_RC_DEADLINE = "deadline";
    public static final String COLUMN_RC_STATUS = "status";
    public static final String COLUMN_RC_DESC = "description";

    // Columns - Applicants
    public static final String COLUMN_APP_ID = "id";
    public static final String COLUMN_APP_NAME = "name";
    public static final String COLUMN_APP_DEPT = "department";
    public static final String COLUMN_APP_STATUS = "status";

    // Columns - Members
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
        db.execSQL("CREATE TABLE " + TABLE_USERS + "(" + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_STUDENT_ID + " TEXT," + COLUMN_USER_BIO + " TEXT," + COLUMN_USER_EMAIL + " TEXT UNIQUE," + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_IMAGE + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," + COLUMN_DATE + " TEXT," + COLUMN_TIME + " TEXT," + COLUMN_LOCATION + " TEXT," + COLUMN_DESCRIPTION + " TEXT," + COLUMN_STATUS + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + "(" + COLUMN_ATT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ATT_EVENT_ID + " INTEGER," + COLUMN_ATT_USER_NAME + " TEXT," + COLUMN_ATT_STATUS + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + "(" + COLUMN_NOTIF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NOTIF_TITLE + " TEXT," + COLUMN_NOTIF_MESSAGE + " TEXT," + COLUMN_NOTIF_TARGET + " TEXT," + COLUMN_NOTIF_IS_READ + " INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE " + TABLE_RECRUITMENT_CONFIG + "(" + COLUMN_RC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_RC_CLUB + " TEXT," + COLUMN_RC_DEADLINE + " TEXT," + COLUMN_RC_STATUS + " TEXT," + COLUMN_RC_DESC + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_APPLICANTS + "(" + COLUMN_APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_APP_NAME + " TEXT," + COLUMN_APP_DEPT + " TEXT," + COLUMN_APP_STATUS + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_MEMBERS + "(" + COLUMN_MEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_MEM_NAME + " TEXT," + COLUMN_MEM_ROLE + " TEXT," + COLUMN_MEM_TYPE + " TEXT," + COLUMN_MEM_CLUB + " TEXT)");
        addInitialData(db);
    }

    private void addInitialData(SQLiteDatabase db) {
        insertUser(db, "Admin Exora", "ADM001", "Official Administrator", "exoraorg123@gmail.com", "admin123");
        insertRecruitment(db, "Robotics Club", "Oct 15, 2024", "OPEN", "Join us to build the future!");
        insertMember(db, "Admin Exora", "Admin", "ADMIN", "Robotics Club");
    }

    private void insertUser(SQLiteDatabase db, String name, String studentId, String bio, String email, String password) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_NAME, name); v.put(COLUMN_USER_STUDENT_ID, studentId); v.put(COLUMN_USER_BIO, bio); v.put(COLUMN_USER_EMAIL, email); v.put(COLUMN_USER_PASSWORD, password);
        db.insert(TABLE_USERS, null, v);
    }

    private void insertRecruitment(SQLiteDatabase db, String club, String deadline, String status, String desc) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_RC_CLUB, club); v.put(COLUMN_RC_DEADLINE, deadline); v.put(COLUMN_RC_STATUS, status); v.put(COLUMN_RC_DESC, desc);
        db.insert(TABLE_RECRUITMENT_CONFIG, null, v);
    }

    private void insertMember(SQLiteDatabase db, String name, String role, String type, String club) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_MEM_NAME, name); v.put(COLUMN_MEM_ROLE, role); v.put(COLUMN_MEM_TYPE, type); v.put(COLUMN_MEM_CLUB, club);
        db.insert(TABLE_MEMBERS, null, v);
    }

    // AUTH METHODS
    public Cursor checkUser(String email, String password) {
        return getReadableDatabase().query(TABLE_USERS, null, COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?", new String[]{email, password}, null, null, null);
    }

    public long registerUser(String name, String email, String password, String studentId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_NAME, name);
        v.put(COLUMN_USER_EMAIL, email);
        v.put(COLUMN_USER_PASSWORD, password);
        v.put(COLUMN_USER_STUDENT_ID, studentId);
        v.put(COLUMN_USER_BIO, "New Exora Member");
        return db.insert(TABLE_USERS, null, v);
    }

    public Cursor getUserByEmail(String email) {
        return getReadableDatabase().query(TABLE_USERS, null, COLUMN_USER_EMAIL + "=?", new String[]{email}, null, null, null);
    }

    public Cursor getUser(String name) {
        return getReadableDatabase().query(TABLE_USERS, null, COLUMN_USER_NAME + "=?", new String[]{name}, null, null, null);
    }

    public int updateUserByEmail(String email, String newName, String studentId, String bio, String imageUri) {
        SQLiteDatabase db = getWritableDatabase();
        String oldName = "";
        Cursor c = getUserByEmail(email);
        if (c != null && c.moveToFirst()) {
            oldName = c.getString(c.getColumnIndexOrThrow(COLUMN_USER_NAME));
            c.close();
        }
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_NAME, newName);
        v.put(COLUMN_USER_STUDENT_ID, studentId);
        v.put(COLUMN_USER_BIO, bio);
        if (imageUri != null) v.put(COLUMN_USER_IMAGE, imageUri);
        int res = db.update(TABLE_USERS, v, COLUMN_USER_EMAIL + "=?", new String[]{email});
        if (!oldName.isEmpty() && !oldName.equals(newName)) {
            ContentValues nv = new ContentValues(); nv.put(COLUMN_ATT_USER_NAME, newName);
            db.update(TABLE_ATTENDANCE, nv, COLUMN_ATT_USER_NAME + "=?", new String[]{oldName});
            ContentValues mv = new ContentValues(); mv.put(COLUMN_MEM_NAME, newName);
            db.update(TABLE_MEMBERS, mv, COLUMN_MEM_NAME + "=?", new String[]{oldName});
        }
        return res;
    }

    // EVENT METHODS
    public long addEvent(EventModel e) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_NAME, e.getName()); v.put(COLUMN_DATE, e.getDate()); v.put(COLUMN_TIME, e.getTime()); v.put(COLUMN_LOCATION, e.getLocation()); v.put(COLUMN_DESCRIPTION, e.getDescription()); v.put(COLUMN_STATUS, e.getStatus());
        long id = getWritableDatabase().insert(TABLE_EVENTS, null, v);
        if (id != -1) addNotification("New Agenda", "Admin added: " + e.getName(), "USER");
        return id;
    }

    public List<EventModel> getAllEvents() {
        List<EventModel> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        if (c != null && c.moveToFirst()) {
            do { list.add(new EventModel(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6))); } while (c.moveToNext());
            c.close();
        }
        return list;
    }

    public EventModel getEvent(int id) {
        Cursor c = getReadableDatabase().query(TABLE_EVENTS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            EventModel e = new EventModel(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6));
            c.close(); return e;
        }
        return null;
    }

    public int updateEvent(EventModel e) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_NAME, e.getName()); v.put(COLUMN_DATE, e.getDate()); v.put(COLUMN_TIME, e.getTime()); v.put(COLUMN_LOCATION, e.getLocation()); v.put(COLUMN_DESCRIPTION, e.getDescription()); v.put(COLUMN_STATUS, e.getStatus());
        return getWritableDatabase().update(TABLE_EVENTS, v, COLUMN_ID + "=?", new String[]{String.valueOf(e.getId())});
    }

    public void deleteEvent(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EVENTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_ATTENDANCE, COLUMN_ATT_EVENT_ID + "=?", new String[]{String.valueOf(id)});
    }

    public long joinEvent(int eventId, String userName) {
        ContentValues v = new ContentValues();
        v.put(COLUMN_ATT_EVENT_ID, eventId); v.put(COLUMN_ATT_USER_NAME, userName); v.put(COLUMN_ATT_STATUS, "Joined");
        long res = getWritableDatabase().insert(TABLE_ATTENDANCE, null, v);
        if (res != -1) addNotification("New Participant", userName + " joined an event.", "ADMIN");
        return res;
    }

    public List<EventModel> getEventsForUser(String userName) {
        List<EventModel> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT e.* FROM " + TABLE_EVENTS + " e JOIN " + TABLE_ATTENDANCE + " a ON e." + COLUMN_ID + " = a." + COLUMN_ATT_EVENT_ID + " WHERE a." + COLUMN_ATT_USER_NAME + "=?", new String[]{userName});
        if (c != null && c.moveToFirst()) { do { list.add(new EventModel(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6))); } while (c.moveToNext()); c.close(); }
        return list;
    }

    public Cursor getEventParticipants(int eventId) {
        return getReadableDatabase().query(TABLE_ATTENDANCE, null, COLUMN_ATT_EVENT_ID + "=?", new String[]{String.valueOf(eventId)}, null, null, null);
    }

    // MEMBER METHODS
    public long addMember(String n, String r, String t, String c) {
        ContentValues v = new ContentValues(); v.put(COLUMN_MEM_NAME, n); v.put(COLUMN_MEM_ROLE, r); v.put(COLUMN_MEM_TYPE, t); v.put(COLUMN_MEM_CLUB, c);
        return getWritableDatabase().insert(TABLE_MEMBERS, null, v);
    }

    public Cursor getMembersByClub(String club) {
        return getReadableDatabase().query(TABLE_MEMBERS, null, COLUMN_MEM_CLUB + "=?", new String[]{club}, null, null, null);
    }

    public void deleteMember(int id) {
        getWritableDatabase().delete(TABLE_MEMBERS, COLUMN_MEM_ID + "=?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllMembers() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_MEMBERS, null);
    }

    // RECRUITMENT METHODS
    public Cursor getRecruitmentConfig() {
        return getReadableDatabase().query(TABLE_RECRUITMENT_CONFIG, null, null, null, null, null, null);
    }

    public Cursor getRecruitmentByClub(String clubName) {
        return getReadableDatabase().query(TABLE_RECRUITMENT_CONFIG, null, COLUMN_RC_CLUB + "=?", new String[]{clubName}, null, null, null);
    }

    public void updateRecruitmentStatus(String s, String d) {
        ContentValues v = new ContentValues(); v.put(COLUMN_RC_STATUS, s); v.put(COLUMN_RC_DEADLINE, d);
        getWritableDatabase().update(TABLE_RECRUITMENT_CONFIG, v, null, null);
    }

    public long saveRecruitment(String clubName, String status, String deadline, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_RC_CLUB, clubName); v.put(COLUMN_RC_STATUS, status); v.put(COLUMN_RC_DEADLINE, deadline); v.put(COLUMN_RC_DESC, desc);
        Cursor c = getRecruitmentByClub(clubName);
        if (c != null && c.moveToFirst()) { c.close(); return db.update(TABLE_RECRUITMENT_CONFIG, v, COLUMN_RC_CLUB + "=?", new String[]{clubName}); }
        else { if (c != null) c.close(); return db.insert(TABLE_RECRUITMENT_CONFIG, null, v); }
    }

    // APPLICANT METHODS
    public int getApplicantCount() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICANTS, null);
        int count = 0; if (c != null && c.moveToFirst()) { count = c.getInt(0); c.close(); }
        return count;
    }

    public int getShortlistedCount() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICANTS + " WHERE " + COLUMN_APP_STATUS + "='ACCEPTED'", null);
        int count = 0; if (c != null && c.moveToFirst()) { count = c.getInt(0); c.close(); }
        return count;
    }

    public Cursor getApplicantsByStatus(String s) {
        return getReadableDatabase().query(TABLE_APPLICANTS, null, COLUMN_APP_STATUS + "=?", new String[]{s}, null, null, null);
    }

    public void updateApplicantStatus(int id, String s) {
        ContentValues v = new ContentValues(); v.put(COLUMN_APP_STATUS, s);
        getWritableDatabase().update(TABLE_APPLICANTS, v, COLUMN_APP_ID + "=?", new String[]{String.valueOf(id)});
    }

    // NOTIFICATION METHODS
    public void addNotification(String t, String m, String tg) {
        ContentValues v = new ContentValues(); v.put(COLUMN_NOTIF_TITLE, t); v.put(COLUMN_NOTIF_MESSAGE, m); v.put(COLUMN_NOTIF_TARGET, tg);
        getWritableDatabase().insert(TABLE_NOTIFICATIONS, null, v);
    }

    public Cursor getUnreadNotifications(String tg) {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + COLUMN_NOTIF_TARGET + "=? AND " + COLUMN_NOTIF_IS_READ + "=0", new String[]{tg});
    }

    public void markNotificationsAsRead(String tg) {
        ContentValues v = new ContentValues(); v.put(COLUMN_NOTIF_IS_READ, 1);
        getWritableDatabase().update(TABLE_NOTIFICATIONS, v, COLUMN_NOTIF_TARGET + "=?", new String[]{tg});
    }

    public Cursor getUserClubs(String userName) {
        return getReadableDatabase().query(TABLE_MEMBERS, new String[]{COLUMN_MEM_CLUB}, COLUMN_MEM_NAME + "=?", new String[]{userName}, null, null, null);
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
}