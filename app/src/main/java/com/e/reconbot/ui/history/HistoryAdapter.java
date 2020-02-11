package com.e.reconbot.ui.history;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.e.reconbot.HistoryDetail;
import com.e.reconbot.HistoryItem;
import com.e.reconbot.MainActivity;
import com.e.reconbot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.e.reconbot.MainActivity.*;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.RVViewHolderClass> {

    ArrayList<HistoryItem> historyRV;

    public HistoryAdapter(ArrayList<HistoryItem> historyRV) {
        this.historyRV = historyRV;
    }


    @NonNull
    @Override
    public RVViewHolderClass onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new RVViewHolderClass(LayoutInflater.from(viewGroup.getContext())
            .inflate((R.layout.single_row), viewGroup, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final HistoryAdapter.RVViewHolderClass holder, int i) {

        final HistoryItem history = historyRV.get(i);
        byte[] image;
        final Bitmap bitmap;
        image = history.getPhoto();
        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        holder.results1TV.setText(history.getResults1());
        holder.results2TV.setText(history.getResults2());
        holder.results3TV.setText(history.getResults3());
        holder.imageIV.setImageBitmap(bitmap);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putByteArray("image", history.getPhoto());

                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                HistoryDetail detail = new HistoryDetail();
                detail.setArguments(args);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.frameContainer, detail)
                        .commit();
            }
        });


    }


    @Override
    public int getItemCount() {
        return historyRV.size();
    }


    public static class RVViewHolderClass extends RecyclerView.ViewHolder {
        TextView results1TV;
        TextView results2TV;
        TextView results3TV;
        ImageView imageIV;
        ImageView big;
        CardView cardView;

        public RVViewHolderClass(@NonNull View itemView) {
            super(itemView);
            results1TV = itemView.findViewById(R.id.result1);
            results2TV = itemView.findViewById(R.id.result2);
            results3TV = itemView.findViewById(R.id.result3);
            imageIV = itemView.findViewById(R.id.photoView);
            cardView = itemView.findViewById(R.id.card_view);
            big  = itemView.findViewById(R.id.imageBig);

        }
    }


}