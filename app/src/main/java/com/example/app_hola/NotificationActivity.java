package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.NotificationContent;
import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationActivity extends AppCompatActivity {
    Button btnRead, btnUnread;
    ListView listViewNoti;
    NotiAdapter adapter;
    ArrayList<NotificationContent> listNoti;
    ArrayList<NotificationContent> listNotiFilter;
    DatabaseReference dataRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    User currentUser;
    boolean unread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("Thông báo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Mapping();
        getNotificationForUser();
        btnUnread.setBackgroundResource(R.drawable.button_have_check_type_1);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unread) {
                    unread=false;
                    FilterNoti();
                    btnRead.setBackgroundResource(R.drawable.button_have_check_type_1);
                    btnUnread.setBackgroundResource(R.drawable.button_type_2);
                }
            }
        });
        btnUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!unread) {
                    unread=true;
                    FilterNoti();
                    btnUnread.setBackgroundResource(R.drawable.button_have_check_type_1);
                    btnRead.setBackgroundResource(R.drawable.button_type_2);
                }
            }
        });

        listViewNoti.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dataRef.child("Contents").child(listNotiFilter.get(i).getContentID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Intent intent = new Intent(NotificationActivity.this, ReadContent.class);
                        intent.putExtra("content", task.getResult().getValue(Content.class));
//                        listNotiFilter.get(i).setRead(true);
                        dataRef.child("Notifications").child(listNotiFilter.get(i).getID()).child("read").setValue(true);
                        startActivity(intent);
                    }
                });

            }
        });
    }
    private void Mapping()
    {
        btnRead = (Button) findViewById(R.id.btn_read);
        btnUnread= (Button) findViewById(R.id.btn_unread);
        listViewNoti = (ListView) findViewById(R.id.list_notification);
        listNoti = new ArrayList<>();
        listNotiFilter = new ArrayList<>();
        adapter = new NotiAdapter(listNotiFilter,this);
        listViewNoti.setAdapter(adapter);
        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dataRef= FirebaseDatabase.getInstance().getReference();
        dataRef.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                currentUser = task.getResult().getValue(User.class);
            }
        });
        unread = true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getNotificationForUser(){
        Query query = dataRef.child("Notifications").orderByChild("userID").equalTo(user.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Lọc sắp xếp lại thời gian của notification
                NotificationContent noti = snapshot.getValue(NotificationContent.class);
                if(listNoti.size()==0)
                    listNoti.add(noti);
                else{
                    boolean check = false;
                    for(int i=0; i<listNoti.size(); i++)
                    {
                        String d1 = noti.getDate();
                        String d2 = listNoti.get(i).getDate();
                        if(checkDate(d1,d2))
                        {
                            check=true;
                            listNoti.add(i,noti);
                            break;
                        }
                    }
                    if(!check)
                        listNoti.add(noti);
                }
                FilterNoti();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(int i=0; i<listNoti.size(); i++)
                    if(snapshot.getValue(NotificationContent.class).getID() == listNoti.get(i).getID())
                    {
                        listNoti.set(i,snapshot.getValue(NotificationContent.class));
                        FilterNoti();
                    }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void FilterNoti()
    {
        listNotiFilter.clear();
        for(int i=0; i<listNoti.size(); i++)
            if(listNoti.get(i).isRead()!=unread) {
                listNotiFilter.add(listNoti.get(i));
            }
        adapter.notifyDataSetChanged();

    }

    private boolean checkDate(String date1, String date2)
    {
        try {
            Date d1 = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss").parse(date1);
            Date d2 = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss").parse(date2);
            if(d1.getTime()> d2.getTime())
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}