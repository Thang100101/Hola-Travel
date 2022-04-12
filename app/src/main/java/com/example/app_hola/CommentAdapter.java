package com.example.app_hola;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.app_hola.ObjectForApp.Comment;
import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {
    ArrayList<Comment> listConment;
    Context context;
    LayoutInflater inflater;

    public CommentAdapter(ArrayList<Comment> listConment, Context context) {
        this.listConment = listConment;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listConment.size();
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
        view = inflater.inflate(R.layout.list_comment,null);
        ImageView imgAvatar = (ImageView) view.findViewById(R.id.img_avatar);
        TextView txtName = (TextView) view.findViewById(R.id.txt_username);
        TextView txtContent = (TextView) view.findViewById(R.id.txt_content);
        Comment comment = listConment.get(i);

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        dataRef.child("Users").child(comment.getUserID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    User user = task.getResult().getValue(User.class);
                    Picasso.get().load(user.getAvatar()).into(imgAvatar);
                    txtName.setText(user.getName());
                }
            }
        });
        txtContent.setText(comment.getMainContent());

        return view;
    }
}
