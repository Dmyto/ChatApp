package com.example.chatapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.chatapp.model.ModelMessage;
import com.example.chatapp.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ModelMessage> {
    public MessageAdapter(@NonNull Context context,
                          int resource,
                          List<ModelMessage> messages) {
        super(context, resource, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.message_item, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);

        TextView textTextView = convertView.findViewById(R.id.text_textView);

        TextView nameTextView = convertView.findViewById(R.id.name_textView);

        ModelMessage message = getItem(position);

        if (message.getImageUrl() == null){
            textTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            textTextView.setText(message.getText());
        } else {
            textTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getImageUrl())
                    .into(photoImageView);
        }

        nameTextView.setText(message.getName());

        return convertView;
    }
}
