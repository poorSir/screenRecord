package com.example.yxtdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-24
 * @Description
 */
public class RecordListAdapter extends RecyclerView.Adapter {
    private List<String> data = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public RecordListAdapter(Context context,List<String> data){
        this.context = context;
        this.data = data;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_record_list,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        ViewHolder vh = (ViewHolder) viewHolder;
        vh.textView.setText(data.get(i));
        vh.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(i);
                }
            }
        });
        vh.textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener!=null){
                    listener.OnItemLongClick(i);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
        }
    }
    interface OnItemClickListener{
        void onItemClick(int position);
        void OnItemLongClick(int position);
    }

}
