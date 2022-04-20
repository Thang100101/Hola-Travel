package com.example.app_hola;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_hola.ObjectForApp.NotificationContent;
import com.example.app_hola.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotiAdapter extends BaseAdapter {
    ArrayList<NotificationContent> listNoti;
    Context context;
    LayoutInflater inflater;

    public NotiAdapter(ArrayList<NotificationContent> listNoti, Context context) {
        this.listNoti = listNoti;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listNoti.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list_notification,null);
        ImageView img = (ImageView) view.findViewById(R.id.img_content);
        TextView txtMainContent = (TextView) view.findViewById(R.id.txt_content);
        TextView txtDate = (TextView) view.findViewById(R.id.txt_date);

        NotificationContent noti = listNoti.get(i);
        Picasso.get().load(noti.getImg().getLink()).into(img);
        String mainContent;
        if(noti.getReaderName().equals("Chưa có tên"))
            noti.setReaderName(context.getResources().getString(R.string.name_unknow));
        if(noti.getMainContent().equals("like_type_1"))
            mainContent = noti.getCountContact()+" "+context.getResources().getString(R.string.like_type_1);
        else if (noti.getMainContent().equals("like_type_2"))
            mainContent = noti.getReaderName()+" "+context.getResources().getString(R.string.like_type_2);
        else
            mainContent = noti.getReaderName()+" "+context.getResources().getString(R.string.comment);

        txtMainContent.setText(mainContent);
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy/HH").parse(noti.getDate());
            Calendar calendar = Calendar.getInstance();
            if(calendar.getTime().getDate()-date.getDate()>0)
                txtDate.setText(calendar.getTime().getDate()-date.getDate()+" ngày trước");
            else
                txtDate.setText(calendar.getTime().getHours()-date.getHours()+" giờ trước");
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        if (!noti.isRead())
//            view.setBackgroundColor(R.color.gray);
        return view;
    }
}
