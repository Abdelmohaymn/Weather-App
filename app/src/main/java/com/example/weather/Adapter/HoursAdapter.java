package com.example.weather.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.model.HourObj;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.ViewHolder> {

    private List<HourObj> hours;

    public HoursAdapter(List<HourObj> mhours){
        hours = mhours;
    }

    @NonNull
    @Override
    public HoursAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hour_item,parent,false);
        return  new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull HoursAdapter.ViewHolder holder, int position) {
        HourObj currentHour = hours.get(position);

        holder.getTextViewHourTime().setText(timeFormater(Long.parseLong(currentHour.getTime_epoch())));
        holder.getTextViewHourTemp().setText(temp(currentHour.getTemp_c()) + "\u00B0");
        Picasso.get().load("https:" + currentHour.getCondition().getIcon()).into(holder.getImageViewHourIcon());

    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView textViewHourTime;
        private final TextView textViewHourTemp;
        private final ImageView imageViewHourIcon;

        public ViewHolder(View view){
            super(view);

            textViewHourTime = view.findViewById(R.id.textViewHourTime);
            textViewHourTemp = view.findViewById(R.id.textViewHourTemp);
            imageViewHourIcon = view.findViewById(R.id.imageViewHourIcon);
        }

        public TextView getTextViewHourTime() {
            return textViewHourTime;
        }

        public TextView getTextViewHourTemp() {
            return textViewHourTemp;
        }

        public ImageView getImageViewHourIcon() {
            return imageViewHourIcon;
        }
    }

    private String timeFormater(long time){
        Date date = new java.util.Date(time*1000);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("H:00");
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
