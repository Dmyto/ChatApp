package com.example.chatapp.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.ImageFragment;
import com.example.chatapp.R;
import com.example.chatapp.model.ModelMessage;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENDER = 1;
    private static final int VIEW_TYPE_MESSAGE_RECIPIENT = 2;

    private List<ModelMessage> mModelMessagesList;
    private String recipientUserId;
    private FragmentManager fragmentManager;

    public MessageListAdapter(List<ModelMessage> messageList, String recipientUserId, FragmentManager fragmentManager) {
        mModelMessagesList = messageList;
        this.recipientUserId = recipientUserId;
        this.fragmentManager = fragmentManager;
    }


    @Override
    public int getItemViewType(int position) {
        ModelMessage message = mModelMessagesList.get(position);
        if (message.getSender().equals(recipientUserId)) {
            return VIEW_TYPE_MESSAGE_RECIPIENT;
        } else {
            return VIEW_TYPE_MESSAGE_SENDER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENDER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_sender, parent, false);
            return new SenderMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECIPIENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_recipient, parent, false);
            return new RecipientMessageHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ModelMessage message = mModelMessagesList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENDER:
                ((SenderMessageHolder) holder).bind(message);
                ((SenderMessageHolder) holder).photoImageView.setOnClickListener(v -> openImg(message));
                break;
            case VIEW_TYPE_MESSAGE_RECIPIENT:
                ((RecipientMessageHolder) holder).bind(message);
                ((RecipientMessageHolder) holder).photoImageView.setOnClickListener(v -> openImg(message));
                break;
            default:
        }
    }

    private void openImg(ModelMessage message) {
        Bundle bundle = new Bundle();
        bundle.putString("picture", message.getImageUrl());
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public int getItemCount() {
        return mModelMessagesList.size();
    }

    private static class SenderMessageHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView textTextView, timeTextView;

        public SenderMessageHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            textTextView = itemView.findViewById(R.id.text_textView);
            timeTextView = itemView.findViewById(R.id.text_message_time);
        }

        void bind(ModelMessage message) {
            if (message.getImageUrl() == null) {
                textTextView.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                textTextView.setText(message.getText());
                timeTextView.setText(message.getTimeSent());
            } else {
                timeTextView.setText(message.getTimeSent());
                textTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                Glide.with(photoImageView.getContext())
                        .load(message.getImageUrl())
                        .into(photoImageView);
            }
        }
    }

    private static class RecipientMessageHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;
        TextView textTextView, nameTextView, timeTextView;

        RecipientMessageHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            textTextView = itemView.findViewById(R.id.text_textView);
            nameTextView = itemView.findViewById(R.id.name_textView);
            timeTextView = itemView.findViewById(R.id.text_message_time);

        }

        void bind(ModelMessage message) {
            if (message.getImageUrl() == null) {
                textTextView.setVisibility(View.VISIBLE);
                photoImageView.setVisibility(View.GONE);
                textTextView.setText(message.getText());
                timeTextView.setText(message.getTimeSent());
            } else {
                timeTextView.setText(message.getTimeSent());
                textTextView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
                Glide.with(photoImageView.getContext())
                        .load(message.getImageUrl())
                        .into(photoImageView);
            }
            nameTextView.setText(message.getName());
        }

    }
}
