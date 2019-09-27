package com.example.yxtdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author szh
 * @Date 2019-09-19
 * @Description
 */
public class DemoAdapter extends RecyclerView.Adapter {
    private List<ScreenRecordEntity> data = new ArrayList<>();
    private Context context;
    public DemoAdapter(Context context, List<ScreenRecordEntity> data){
        this.data = data;
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  =LayoutInflater.from(context).inflate(R.layout.item_draw,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder vh  = (ViewHolder) viewHolder;
        ScreenRecordEntity screenRecordEntity =data.get(i);
        if("0".equals(screenRecordEntity.getType())){
            vh.layout.setBackgroundColor(Color.WHITE);
        }else{
            Glide.with(context)
                    .load(screenRecordEntity.getPath())
                    .asBitmap()
                    .fitCenter()
                    .into(vh.imageView);
        }
        vh.doodleView.setCanDraw(screenRecordEntity.isCanDraw());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends  RecyclerView.ViewHolder{
        RelativeLayout layout;
        SimpleDoodleView doodleView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            imageView = itemView.findViewById(R.id.imageView);
            doodleView = itemView.findViewById(R.id.doodleView);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
