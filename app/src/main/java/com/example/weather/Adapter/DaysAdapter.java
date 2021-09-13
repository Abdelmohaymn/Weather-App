package com.example.weather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.ForcastObj;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.ViewHolder> {
    private List<ForcastObj> days;

    public DaysAdapter(List<ForcastObj> mDays){
        days = mDays;
    }

    @NonNull
    @Override
    public DaysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DaysAdapter.ViewHolder holder, int position) {

        ForcastObj currentDay = days.get(position);

        holder.getTextViewStateDay().setText(currentDay.getDay().getCondition().getText());
        holder.getTextViewMaxTemp().setText(temp(currentDay.getDay().getMaxtemp_c()) + "\u00B0");
        holder.getTextViewMinTemp().setText(temp(currentDay.getDay().getMintemp_c()) + "\u00B0");
        Picasso.get().load("https:" + currentDay.getDay().getCondition().getIcon()).into(holder.getImageViewDayIcon());

        if (position == 0){
            holder.getTextViewDayItem().setText("Tomorrow");
        }else {
            holder.getTextViewDayItem().setText(timeFormater(Long.parseLong(currentDay.getDate_epoch())));
        }

    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewDayItem;
        private final TextView textViewStateDay;
        private final TextView textViewMaxTemp;
        private final TextView textViewMinTemp;
        private final ImageView imageViewDayIcon;

        public ViewHolder(View view){
            super(view);

            textViewDayItem = view.findViewById(R.id.textViewDayItem);
            textViewStateDay = view.findViewById(R.id.textViewStateDay);
            textViewMaxTemp = view.findViewById(R.id.textViewMaxTemp);
            textViewMinTemp = view.findViewById(R.id.textViewMinTemp);
            imageViewDayIcon = view.findViewById(R.id.imageViewDayIcon);
        }

        public TextView getTextViewDayItem() {
            return textViewDayItem;
        }

        public TextView getTextViewStateDay() {
            return textViewStateDay;
        }

        public TextView getTextViewMaxTemp() {
            return textViewMaxTemp;
        }

        public TextView getTextViewMinTemp() {
            return textViewMinTemp;
        }

        public ImageView getImageViewDayIcon() {
            return imageViewDayIcon;
        }
    }

    private String timeFormater(long time){
        Date date = new java.util.Date(time*1000);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("E");
        //sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        String formattedTime = sdf.format(date);
        return formattedTime;
    }

    private String temp(String t){
        if (t.contains(".")){
            String out = "";
            char [] arr = t.toCharArray();
            for (char c : arr){
                if (c != '.'){
                    out += c;
                }else {
                    return out;
                }
            }
        }
        return t;
    }

}
