package com.example.app_hola;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.Like;
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

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity  {
    public static final String EXTRA_MESSAGE="com.example.app_hola.MESSAGE";
    HorizontalScrollView hScrollView;
    String[] list;
    Button btnUpload, btnHome, btnYourReview;
    ArrayList<Content> listContent = new ArrayList<Content>();
    ListView listViewContent;
    ActionBar actionBar;
    SharedPreferences prefer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference dataRef;
    ContentAdapter adapter;
    User mainUser;
    final int ALL = 0, TOP10 = 1, FOOD = 2, HOTEL = 3, REVIEW = 4, TIP = 5, EXP = 6;

    boolean search =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Mapping();
        customActionBar();
        getInfOfContent();

        btnUpload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(HomeActivity.this, "Đăng bài", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Content content = listContent.get(i);
                Intent intent = new Intent(HomeActivity.this, ReadContent.class);
                intent.putExtra("content", content);
                startActivity(intent);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser==null)
                    Toast.makeText(HomeActivity.this, "Đăng nhập để đăng tải bài viết", Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(HomeActivity.this, CreateContentActivity.class);
                    startActivity(intent);
                }
            }
        });
        btnYourReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser==null)
                    Toast.makeText(HomeActivity.this, "Đăng nhập để xem danh sách bài viết của bạn", Toast.LENGTH_LONG).show();
                else
                {
                    Intent intent = new Intent(HomeActivity.this, YourContentActivity.class);
                    intent.putExtra("userID", currentUser.getUid());
                    intent.putExtra("userName", mainUser.getName());
                    startActivity(intent);
                }
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(ALL);
            }
        });

    }

    ///Lấy danh sách content
    private void getInfOfContent(){
        Dialog dialogLoading = new Dialog(HomeActivity.this);
        dialogLoading.setContentView(R.layout.dialog_loading);
        dialogLoading.show();
        dialogLoading.setCancelable(false);
        dataRef= FirebaseDatabase.getInstance().getReference("Contents");
        dataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listContent.add(snapshot.getValue(Content.class));
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
        },2000);

    }

    //Ánh xạ
    private void Mapping(){
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnHome = (Button) findViewById(R.id.btn_home);
        btnYourReview = (Button) findViewById(R.id.btn_your_review);
        listViewContent = (ListView) findViewById(R.id.listContent);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        listContent = new ArrayList<>();
        adapter = new ContentAdapter(listContent, this);
        listViewContent.setAdapter(adapter);
        dataRef = FirebaseDatabase.getInstance().getReference();
        if(currentUser!=null)
            dataRef.child("Users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                        mainUser = task.getResult().getValue(User.class);
                }
            });
    }

    //Tạo và bắt sự kiện cho menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentUser!=null)
            getMenuInflater().inflate(R.menu.main_menu,menu);
        else
            getMenuInflater().inflate(R.menu.main_menu_without_signin,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_profile:
                Intent profile = new Intent(HomeActivity.this, ProfileActivity.class);
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
                                Toast.makeText(HomeActivity.this, "Search!!", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    });
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void Filter(int TYPE){
        switch (TYPE)
        {
            case ALL:
                listContent.clear();
                getInfOfContent();
                break;
            case TOP10:
                break;
            case TIP:
                listContent.clear();

                break;
            case EXP:
                listContent.clear();

                break;
            case HOTEL:
                listContent.clear();

                break;
            case FOOD:
                break;
            case REVIEW:
                break;
        }
    }


    //Thay đổi UI của actionBar
    private void customActionBar()
    {
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("");
        actionBar.setLogo(R.drawable.logo_3);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
    }

    //Thay đổi trạng thái search/nosearch

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
        dialog.setTitle("Khoan đã!!");
        dialog.setMessage("Bạn có chắc chắn muốn thoát không?");
        dialog.setIcon(R.drawable.icon_crying);
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
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
        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();
    }


}