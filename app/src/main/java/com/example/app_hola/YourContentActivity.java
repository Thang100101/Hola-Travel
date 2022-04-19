package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.Content;
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

import java.util.ArrayList;

public class YourContentActivity extends AppCompatActivity {
    Button btnUpload, btnHome, btnYourContent;
    ListView listViewContent;
    DatabaseReference dataRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    User currentUser;
    String ownerID, ownerName;
    ArrayList<Content> listContent = new ArrayList<>();
    ArrayList<Content> listFilter = new ArrayList<>();
    ContentAdapter adapter;
    ActionBar actionBar;
    boolean search=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_content);
        Mapping();
        Intent intent = getIntent();
        ownerID = intent.getStringExtra("userID");
        ownerName = intent.getStringExtra("userName");
        getListContent();
        customActionBar();
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser==null)
                    Toast.makeText(YourContentActivity.this, getResources().getString(R.string.sign_in_upload), Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(YourContentActivity.this, CreateContentActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(YourContentActivity.this, HomeActivity.class);
                startActivity(home);
                finish();
            }
        });
        btnYourContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user==null)
                    Toast.makeText(YourContentActivity.this, getResources().getString(R.string.sign_in_see_posts), Toast.LENGTH_LONG).show();
                else if(!user.getUid().equals(ownerID))
                {
                    Intent intent = new Intent(YourContentActivity.this, YourContentActivity.class);
                    intent.putExtra("userID", currentUser.getUserID());
                    intent.putExtra("userName",currentUser.getName());
                    startActivity(intent);
                }

            }
        });
        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Content content = listContent.get(i);
                Intent intent = new Intent(YourContentActivity.this, ReadContent.class);
                intent.putExtra("content", content);
                startActivity(intent);
            }
        });
    }


    private void Mapping() {
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnHome = (Button) findViewById(R.id.btn_home);
        btnYourContent = (Button) findViewById(R.id.btn_your_review);
        listViewContent = (ListView) findViewById(R.id.listContent);
        dataRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user!=null)
            dataRef.child("Users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                        currentUser = task.getResult().getValue(User.class);
                }
            });
        adapter = new ContentAdapter(listFilter,YourContentActivity.this);
        listViewContent.setAdapter(adapter);
    }

    private void getListContent() {
        Dialog dialogLoading = new Dialog(YourContentActivity.this);
        dialogLoading.setCancelable(false);
        dialogLoading.setContentView(R.layout.dialog_loading);
        dialogLoading.show();
        Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(ownerID);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listContent.add(snapshot.getValue(Content.class));
                listFilter.add(snapshot.getValue(Content.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialogLoading.dismiss();
            }
        },1000);
    }
    private void customActionBar()
    {
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle(getResources().getString(R.string.post_of)+ " " +ownerName);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        if(user!=null) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(currentUser.isHaveNotification())
                        menu.getItem(2).setIcon(R.drawable.icon_bell_noti);
                }
            },2000);
            dataRef.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentUser = snapshot.getValue(User.class);
                    if(currentUser.isHaveNotification()){
                        menu.getItem(2).setIcon(R.drawable.icon_bell_noti);
                    }
                    else
                        menu.getItem(2).setIcon(R.drawable.icon_bell);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
            getMenuInflater().inflate(R.menu.main_menu_without_signin,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //Bắt sự kiện click trên menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_profile:
                Intent profile = new Intent(YourContentActivity.this, ProfileActivity.class);
                startActivity(profile);
                break;
            case R.id.menu_search:
                if(search==false) {
                    searchActionBar(true);
                    EditText editSearch = (EditText) findViewById(R.id.edit_search);
                    editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if(i == EditorInfo.IME_ACTION_SEARCH)
                            {
                                String search = editSearch.getText().toString();
                                listFilter.clear();
                                for(Content content : listContent) {
                                    if(content.getTitle().toLowerCase().indexOf(search.toLowerCase())>=0)
                                        listFilter.add(content);
                                    else if(content.getMainContent().toLowerCase().indexOf(search.toLowerCase())>=0)
                                        listFilter.add(content); }
                                adapter.notifyDataSetChanged();
                            }
                            return false;
                        }
                    });
                    editSearch.setText("");
                }
                else
                    searchActionBar(false);
                break;
            case R.id.menu_exit:
                acceptOut();
                break;
            case R.id.menu_signin:
                Intent intent = new Intent(getApplicationContext(), WellcomeActivity.class);
                intent.putExtra("signin",true);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_signout:
                Intent intent2 = new Intent(getApplicationContext(), WellcomeActivity.class);
                intent2.putExtra("signout",true);
                mAuth.signOut();
                startActivity(intent2);
                finish();
                break;
            case R.id.menu_another:
                Intent intent3 = new Intent(getApplicationContext(), WellcomeActivity.class);
                intent3.putExtra("another",true);
                mAuth.signOut();
                startActivity(intent3);
                finish();
            case R.id.menu_noti:
                dataRef.child("Users").child(currentUser.getUserID()).child("haveNotification").setValue(false);
                item.setIcon(R.drawable.icon_bell);
                Intent intent4 = new Intent(getApplicationContext(),NotificationActivity.class);
                startActivity(intent4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //Thay đổi thanh tìm kiếm
    private void searchActionBar(boolean haveSearch)
    {
        if(haveSearch)
        {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.search_view);
            search = true;
        }
        else
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            search=false;
        }
    }
    //Dialog thoát
    private void acceptOut()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.wait));
        dialog.setMessage(getResources().getString(R.string.want_exit));
        dialog.setIcon(R.drawable.icon_crying);
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), WellcomeActivity.class);
                startActivity(intent);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                finish();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }
}