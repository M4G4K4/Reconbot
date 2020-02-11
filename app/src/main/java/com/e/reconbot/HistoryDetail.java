package com.e.reconbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.e.reconbot.ui.history.HistoryFragment;

import static android.view.View.INVISIBLE;


public class HistoryDetail extends Fragment {

    public HistoryDetail() {
    }

     ImageView imageVw;
     ImageView imageBrgVw;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_details, container, false);
        imageVw = view.findViewById(R.id.imageBig);
        imageBrgVw = view.findViewById(R.id.imgBackground);
        byte[] image = getArguments().getByteArray("image");
        MainActivity.showBottomNavMenu();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        imageVw.setImageBitmap(bitmap);

        imageBrgVw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0);
                MainActivity.showBottomNavMenu();
                imageVw.setImageBitmap(null);
                imageVw.setVisibility(INVISIBLE);
                imageBrgVw.setVisibility(INVISIBLE);
            }
        });
        imageVw.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0);
                MainActivity.showBottomNavMenu();
                imageVw.setImageBitmap(null);
                imageVw.setVisibility(INVISIBLE);
                imageBrgVw.setVisibility(INVISIBLE);
            }
        });

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getActivity().getSupportFragmentManager().getBackStackEntryAt(0);
            getActivity().getSupportFragmentManager().popBackStack();
            MainActivity.showBottomNavMenu();

        } else {
            MainActivity.showBottomNavMenu();
        }



/*
        super.onDetach();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        HistoryFragment history = new HistoryFragment();
        MainActivity.showBottomNavMenu();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentDetails, history)
                .commit();/*

       getActivity().getSupportFragmentManager().beginTransaction().remove(this);
       getActivity().getSupportFragmentManager().popBackStack();
       MainActivity.showBottomNavMenu();


      if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1 ){
            super.onDetach();
            getActivity().getSupportFragmentManager().popBackStack();

        } else {
            super.onDetach();
            MainActivity.showBottomNavMenu();
        }
*/
    }
}


