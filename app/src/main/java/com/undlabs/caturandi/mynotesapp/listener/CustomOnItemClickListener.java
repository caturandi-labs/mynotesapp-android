package com.undlabs.caturandi.mynotesapp.listener;

import android.view.View;
import android.widget.AdapterView;

public class CustomOnItemClickListener implements View.OnClickListener {
    int position;
    private OnItemClickCallback onItemClickCallback;

    public CustomOnItemClickListener(int pos, OnItemClickCallback callback) {
        this.position = pos;
        this.onItemClickCallback = callback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, position);
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int position);
    }

}
