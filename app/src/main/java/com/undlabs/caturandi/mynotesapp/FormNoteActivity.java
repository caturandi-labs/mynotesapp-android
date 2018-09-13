package com.undlabs.caturandi.mynotesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.undlabs.caturandi.mynotesapp.db.NoteHelper;
import com.undlabs.caturandi.mynotesapp.entity.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;

public class FormNoteActivity extends AppCompatActivity {

    private static final String TAG = "FormNoteActivity";
    EditText editTitle;
    EditText editDescription;
    Button btnSubmit;

    public static String EXTRA_NOTE = "extra_note";
    public static String EXTRA_POSITION = "extra_position";

    private boolean isEdit = false; //kondisi ketika data itu akan diedit
    public static int REQUEST_ADD = 100;
    public static int RESULT_ADD = 101;
    public static int REQUEST_UPDATE = 200;
    public static int RESULT_UPDATE = 201;
    public static int RESULT_DELETE = 301;

    private Note note;
    private int position;
    private NoteHelper noteHelper;

    AwesomeValidation mAwesomeValidation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_note);
        ButterKnife.bind(this);

        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        btnSubmit = findViewById(R.id.btn_submit);

        mAwesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        mAwesomeValidation.addValidation(this, R.id.edit_title, RegexTemplate
                .NOT_EMPTY, R.string.validasi_title);
        mAwesomeValidation.addValidation(this, R.id.edit_description, RegexTemplate.NOT_EMPTY, R
                .string.validasi_deskripsi);

        noteHelper = new NoteHelper(this);
        noteHelper.open();

        note = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (note != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        }

        String actionBarTitle = null;
        String btnTitle = null;

        if (isEdit) {
            actionBarTitle = "Ubah Data";
            btnTitle = "Update";
            editTitle.setText(note.getTitle());
            editDescription.setText(note.getDescription());
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSubmit.setText(btnTitle);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonSubmit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null) {
            noteHelper.close();
        }
    }

    private void onButtonSubmit() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        boolean isEmpty = false;

            /*
            Jika fieldnya masih kosong maka tampilkan error
             */
        if (TextUtils.isEmpty(title)) {
            isEmpty = true;
            editTitle.setError("Field can not be blank");
        }

        if (!isEmpty) {
            Note newNote = new Note();
            newNote.setTitle(title);
            newNote.setDescription(description);
            Intent intent = new Intent();

            if (isEdit) {
                newNote.setDate(note.getDate());
                newNote.setId(note.getId());
                noteHelper.update(newNote);

                intent.putExtra(EXTRA_POSITION, position);
                setResult(RESULT_UPDATE, intent);
                finish();

            } else {
                newNote.setDate(this.getCurrentDate());
                noteHelper.insert(newNote);
                setResult(RESULT_ADD);
                finish();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //    Konfirmasi Doalog sebelum proses batal atau hapus
//    close : 10, delete 20
    final int ALERT_DIALOG_CLOSE = 10;
    final int ALERT_DIALOG_DELETE = 20;

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle = null, dialogMessage = null;
        if (isDialogClose) {
            dialogTitle = "Batal";
            dialogMessage = "Apakah Anda Ingin membatalkan perubahan ?";
        } else {
            dialogTitle = "Hapus";
            dialogMessage = "Apakah Anda Yakin Ingin Menghapus data ?";
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (isDialogClose) {
                            finish();
                        } else {
                            noteHelper.delete(note.getId());
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);
                            setResult(RESULT_DELETE, intent);
                            finish();
                        }
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
