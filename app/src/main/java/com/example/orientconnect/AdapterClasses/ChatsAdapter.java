package com.example.orientconnect.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orientconnect.ModelClasses.Chat;
import com.example.orientconnect.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

    private Context mContext;
    private List<Chat> mChatList;
    private String imageUrl;

    public ChatsAdapter(Context mContext, List<Chat> mChatList, String imageUrl) {
        this.mContext = mContext;
        this.mChatList = mChatList;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getItemViewType(int position) {
        // Check if the message is sent by the current user
        if (mChatList.get(position).getSender().equals(imageUrl)) {
            return 1; // For messages sent by the current user
        } else {
            return 0; // For messages sent by others
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = mChatList.get(position);

        if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")) {
            // If it's an image message
            holder.show_text_message.setVisibility(View.GONE);
            holder.left_image_view.setVisibility(View.VISIBLE);
            holder.right_image_view.setVisibility(View.GONE);

            Picasso.get().load(chat.getUrl()).into(holder.left_image_view);
        } else if (!chat.getMessage().equals("") && chat.getUrl().equals("")) {
            // If it's a text message
            holder.show_text_message.setVisibility(View.VISIBLE);
            holder.left_image_view.setVisibility(View.GONE);
            holder.right_image_view.setVisibility(View.GONE);

            holder.show_text_message.setText(chat.getMessage());
        } else if (!chat.getMessage().equals("") && !chat.getUrl().equals("")) {
            // If it's both text and image message
            holder.show_text_message.setVisibility(View.VISIBLE);
            holder.left_image_view.setVisibility(View.VISIBLE);
            holder.right_image_view.setVisibility(View.GONE);

            holder.show_text_message.setText(chat.getMessage());
            Picasso.get().load(chat.getUrl()).into(holder.left_image_view);
        }

        // Display the profile image and set the "seen" status
        if (position == mChatList.size() - 1) {
            if (chat.isIsSeen()) {
                holder.text_seen.setText("Seen");
            } else {
                holder.text_seen.setText("Delivered");
            }
        } else {
            holder.text_seen.setVisibility(View.GONE);
        }

        Picasso.get().load(imageUrl).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profile_image;
        public TextView show_text_message;
        public ImageView left_image_view;
        public TextView text_seen;
        public ImageView right_image_view;

        public ViewHolder(View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            show_text_message = itemView.findViewById(R.id.show_text_message);
            left_image_view = itemView.findViewById(R.id.left_image_view);
            text_seen = itemView.findViewById(R.id.text_seen);
            right_image_view = itemView.findViewById(R.id.right_image_view);
        }
    }
}
