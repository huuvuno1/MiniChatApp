package com.vuhung.minichatapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.model.Message;
import com.vuhung.minichatapp.utils.Constant;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private final static int MY_MESSAGE = 1;
    private List<Message> mListMessage;

    public void setData(List<Message> list) {
        this.mListMessage = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == MY_MESSAGE)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_sent_message,parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message,parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (Constant.MY_USERNAME.equals(mListMessage.get(position).getSender()))
            return MY_MESSAGE;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mListMessage.get(position);
        if(message == null) {
            return;
        }
        holder.tvMessage.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        if(mListMessage != null) {
            return mListMessage.size();
        }
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
