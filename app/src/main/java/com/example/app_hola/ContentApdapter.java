package com.example.app_hola;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app_hola.ObjectForApp.Content;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContentApdapter extends BaseAdapter {
Context context;
List<Content> contents;
LayoutInflater inflater;

    public ContentApdapter(Context context, List<Content> contents) {
        this.context = context;
        this.contents = contents;
        inflater=(LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StorageReference storageReference= FirebaseStorage.getInstance().getReference(contents.get(i).getImageContent().getLink());
        view=inflater.inflate(R.layout.listview_content,null);
        ImageView imageView=(ImageView) view.findViewById(R.id.img_content);
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imageView);
            }
        });
        TextView textView=(TextView) view.findViewById(R.id.txt_content);
        textView.setText(contents.get(i).getTitle());
        TextView textView2=(TextView) view.findViewById(R.id.txt_date);
        textView2.setText(contents.get(i).getDate());
        return view;
    }
}
