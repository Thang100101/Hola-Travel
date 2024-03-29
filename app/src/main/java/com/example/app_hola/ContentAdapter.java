package com.example.app_hola;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_hola.ObjectForApp.Content;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ContentAdapter extends BaseAdapter {
    ArrayList<Content> listContent;
    Context context;
    LayoutInflater inflater;
    Animation animation;

    public ContentAdapter(ArrayList<Content> listContent, Context context) {
        this.listContent = listContent;
        this.context = context;
        this.inflater = (LayoutInflater.from(context)) ;
    }

    @Override
    public int getCount() {
        return listContent.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.listview_content,null);
        ImageView imgContent = (ImageView) view.findViewById(R.id.img_content);
        TextView txtDate = (TextView) view.findViewById(R.id.txt_date);
        TextView txtContent = (TextView) view.findViewById(R.id.txt_content);
        Content content = (Content)listContent.get(i);

        txtDate.setText(content.getDate());
        txtContent.setText(content.getTitle());
        if(content.getImageContent()!=null)
            Picasso.get().load(content.getImageContent().getLink()).into(imgContent);
        animation = AnimationUtils.loadAnimation(context,R.anim.alpha_type_2);
        view.startAnimation(animation);

        return view;
    }
}
