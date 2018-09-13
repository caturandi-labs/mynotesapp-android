package com.undlabs.caturandi.mynotesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.undlabs.caturandi.mynotesapp.adapter.NoteAdapter;
import com.undlabs.caturandi.mynotesapp.db.NoteHelper;
import com.undlabs.caturandi.mynotesapp.entity.Note;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rvNotes;
    ProgressBar progressBar;
    FloatingActionButton fabAdd;

    private LinkedList<Note> list;
    private NoteAdapter noteAdapter;
    private NoteHelper noteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Notes");

        rvNotes = findViewById(R.id.rv_notes);
        progressBar = findViewById(R.id.progress_bar);
        fabAdd = findViewById(R.id.fab_add);

        openDBConn();


        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        fabAdd.setOnClickListener(this);

        list = new LinkedList<>();

        noteAdapter = new NoteAdapter(this);
        noteAdapter.setListNotes(list);
        rvNotes.setAdapter(noteAdapter);

        new LoadNoteAsyncTask().execute();
    }

    private void openDBConn() {
        noteHelper = new NoteHelper(this);
        noteHelper.open();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add) {
            Intent intent = new Intent(MainActivity.this, FormNoteActivity.class);
            startActivityForResult(intent, FormNoteActivity.REQUEST_ADD);
        }
    }

    private class LoadNoteAsyncTask extends AsyncTask<Void, Void, ArrayList<Note>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            if (list.size() > 0) {
                list.clear();
            }
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return noteHelper.query();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            progressBar.setVisibility(View.GONE);

            list.addAll(notes);
            noteAdapter.setListNotes(list);
            noteAdapter.notifyDataSetChanged();

            if (list.size() == 0) {
                showSnackbarMessage("Tidak Ada Untuk Saat Ini");
            }
        }


    }

    public void showSnackbarMessage(String message) {
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FormNoteActivity.REQUEST_ADD) {
            if (resultCode == FormNoteActivity.RESULT_ADD) {
                new LoadNoteAsyncTask().execute();
                showSnackbarMessage("Satu item berhasil disimpan");
                rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), 0);
            }
        } else if (requestCode == FormNoteActivity.REQUEST_UPDATE) {
            if (resultCode == FormNoteActivity.RESULT_UPDATE) {
                new LoadNoteAsyncTask().execute();
                showSnackbarMessage("Satu item berhasil diubah");
                int position = data.getIntExtra(FormNoteActivity.EXTRA_POSITION, 0);
                rvNotes.getLayoutManager().smoothScrollToPosition(rvNotes, new RecyclerView.State(), position);
            } else if (resultCode == FormNoteActivity.RESULT_DELETE) {
                int position = data.getIntExtra(FormNoteActivity.EXTRA_POSITION, 0);
                list.remove(position);
                noteAdapter.setListNotes(list);
                noteAdapter.notifyDataSetChanged();
                showSnackbarMessage("Satu Item berhasil dihapus");
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noteHelper != null) {
            noteHelper.close();
        }
    }
}
