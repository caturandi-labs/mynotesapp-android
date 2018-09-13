package com.undlabs.caturandi.mynotesapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.undlabs.caturandi.mynotesapp.entity.Note;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.undlabs.caturandi.mynotesapp.db.DatabaseContract.TABLE_NOTE;

public class NoteHelper {

    private static final String TAG = "NoteHelper";
    private static String DATABASE_TABLE = TABLE_NOTE;
    private Context context;
    private DatabaseHelper databaseHelper;

    private SQLiteDatabase database;

    public NoteHelper(Context context) {
        this.context = context;
    }

    public NoteHelper open() throws SQLException {
        databaseHelper = new DatabaseHelper(context); //inisiasi objek database helper untuk
        // operasi DDL
        database = databaseHelper.getWritableDatabase(); // beri akses read write database agar
        // bisa dimanipulasi
        return this;
    }

    // Menampilkan data list dari database
    public ArrayList<Note> query() {
        ArrayList<Note> arrayList = new ArrayList<Note>(); //list untuk menyimpan koleksi objek note
        Cursor cursor = database.query(DATABASE_TABLE, null, null, null, null, null, _ID
                + " DESC", null); // jalankan query database dan simpan hasil objek kursor di
        // obj. kursor
        cursor.moveToFirst(); //pindahkan kursor ke baris pertama
        Note note;
        if (cursor.getCount() > 0) {
            do {
                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract
                        .NoteColumns.TITLE)));
                note.setDescription(cursor.getString(cursor.getColumnIndexOrThrow
                        (DatabaseContract.NoteColumns.DESCRIPTION)));
                note.setDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract
                        .NoteColumns.DATE)));
                Log.d(TAG, "OBJEK : " + note.getTitle() + "," + note.getId());
                arrayList.add(note);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());

        }
        cursor.close();
        return arrayList;
    }

    //untuk menyimpan data ke database
    public long insert(Note note) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.NoteColumns.TITLE, note.getTitle());
        contentValues.put(DatabaseContract.NoteColumns.DESCRIPTION, note.getDescription());
        contentValues.put(DatabaseContract.NoteColumns.DATE, note.getDate());
        return database.insert(DATABASE_TABLE, null, contentValues);

    }

    public int update(Note note) {
        ContentValues args = new ContentValues();
        args.put(DatabaseContract.NoteColumns.TITLE, note.getTitle());
        args.put(DatabaseContract.NoteColumns.DESCRIPTION, note.getDescription());
        args.put(DatabaseContract.NoteColumns.DATE, note.getDate());
        return database.update(DATABASE_TABLE, args, _ID + "= '" + note.getId() + "'", null);
    }

    public int delete(int id) {
        return database.delete(TABLE_NOTE, _ID + " = '" + id + "'", null);
    }

    public void close() {
        databaseHelper.close();
    }
}
