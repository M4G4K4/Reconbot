package com.e.reconbot.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.e.reconbot.DbHandler;
import com.e.reconbot.R;

public class HistoryFragment extends Fragment {

     private DbHandler handler;
     private RecyclerView rv;
     private HistoryAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        handler = new DbHandler(this.getContext());
        adapter = new HistoryAdapter(DbHandler.getRows());

        rv = view.findViewById(R.id.imagesRv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rv.setAdapter(adapter);
        return view;
    }
}
