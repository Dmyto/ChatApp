package com.example.chatapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends DialogFragment {

    private static final String ARG_IMAGE = "image";


    public ImageFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String mImage = (String) getArguments().getSerializable(ARG_IMAGE);


        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        Glide.with(imageView.getContext())
                .load(mImage)
                .into(imageView);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    public static ImageFragment newInstance(String image) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }
}