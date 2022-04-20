package com.example.app_hola;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyCharacterMap;
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
import com.example.app_hola.ObjectForApp.Tag;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity  {
    public static final String EXTRA_MESSAGE="com.example.app_hola.MESSAGE";
    HorizontalScrollView hScrollView;
    String[] list;
    String tagID = "";
    Button btnUpload, btnHome, btnYourReview, btnTop10, btnFood, btnHotel, btnReview, btnTip, btnExp;
    ArrayList<Content> listContent = new ArrayList<Content>();
    ListView listViewContent;
    ActionBar actionBar;
    SharedPreferences prefer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference dataRef;
    ContentAdapter adapter;
    Dialog dialogLoading;
    User mainUser;
    long contentCountCheck;
    ArrayList<Content> listFilter = new ArrayList<>();
    final int ALL = 0, TOP10 = 1, FOOD = 2, HOTEL = 3, REVIEW = 4, TIP = 5, EXP = 6, BYTAG = 7;

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
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.upload), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnHome.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        btnYourReview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.my_post), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        listViewContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Content content = listFilter.get(i);
                Intent intent = new Intent(HomeActivity.this, ReadContentActivity.class);
                intent.putExtra("content", content);
                startActivity(intent);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser==null)
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.sign_in_upload), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.sign_in_see_posts), Toast.LENGTH_LONG).show();
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
                unCheckButtonAll();
            }
        });
        btnTop10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(TOP10);
                unCheckButtonAll();
                btnTop10.setBackgroundResource(R.drawable.button_have_check_type_1);
            }
        });
        btnExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(EXP);
                unCheckButtonAll();
                btnExp.setBackgroundResource(R.drawable.button_have_check_type_1);
            }
        });
        btnTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(TIP);
                unCheckButtonAll();
                btnTip.setBackgroundResource(R.drawable.button_have_check_type_1);
            }
        });
        btnFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(FOOD);
                unCheckButtonAll();
                btnFood.setBackgroundResource(R.drawable.button_have_check_type_1);
            }
        });
        btnHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(HOTEL);
                unCheckButtonAll();
                btnHotel.setBackgroundResource(R.drawable.button_have_check_type_1);
            }
        });
        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filter(REVIEW);
                unCheckButtonAll();
                btnReview.setBackgroundResource(R.drawable.button_have_check_type_1);
            }
        });

    }

    ///Lấy danh sách content
    private void getInfOfContent(){
        dialogLoading.show();
        contentCountCheck=-1;
        DatabaseReference dataRef;
        dataRef= FirebaseDatabase.getInstance().getReference("Contents");
        dataRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(contentCountCheck<0) {
                    contentCountCheck = task.getResult().getChildrenCount();
                    dataRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            contentCountCheck--;
                            listContent.add(snapshot.getValue(Content.class));
                            listFilter.add(snapshot.getValue(Content.class));
                            adapter.notifyDataSetChanged();
                            if(contentCountCheck<=0) {
                                dialogLoading.dismiss();
                                Intent intent = getIntent();
                                tagID = intent.getStringExtra("tagid");
                                if (tagID != null) {
                                    Filter(BYTAG);
                                }
                            }
                        }
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                for(int i =0; i<listContent.size(); i++)
                                    if(listContent.get(i).getID().equals(snapshot.getValue(Content.class).getID()))
                                    {
                                        listContent.set(i,snapshot.getValue(Content.class));
                                        adapter.notifyDataSetChanged();
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
                    if (contentCountCheck==0)
                        dialogLoading.dismiss();
                }
            }
        });

//        dataRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                contentCountCheck = snapshot.getChildrenCount();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        if(contentCountCheck>0)

        //


    }

    //Ánh xạ
    private void Mapping(){
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnHome = (Button) findViewById(R.id.btn_home);
        btnYourReview = (Button) findViewById(R.id.btn_your_review);
        btnTop10 = (Button) findViewById(R.id.btn_top_10);
        btnReview = (Button) findViewById(R.id.btn_community);
        btnFood = (Button) findViewById(R.id.btn_food);
        btnHotel = (Button) findViewById(R.id.btn_hotel);
        btnTip = (Button) findViewById(R.id.btn_tip);
        btnExp = (Button) findViewById(R.id.btn_exp);
        listViewContent = (ListView) findViewById(R.id.listContent);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference();
        if(currentUser!=null)
            dataRef.child("Users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful())
                        mainUser = task.getResult().getValue(User.class);
                }
            });

        adapter = new ContentAdapter(listFilter, this);
        listViewContent.setAdapter(adapter);
        dialogLoading = new Dialog(HomeActivity.this);
        dialogLoading.setContentView(R.layout.dialog_loading);
        dialogLoading.setCancelable(false);
    }

    //Tạo và bắt sự kiện cho menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentUser!=null) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mainUser.isHaveNotification())
                        menu.getItem(2).setIcon(R.drawable.icon_bell_noti);
                }
            },4000);
            dataRef.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mainUser = snapshot.getValue(User.class);
                    if(mainUser.isHaveNotification()){
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

    ///Bắt sự kiện click cái item trên menu
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
                    searchActionBar(true, item);
                    EditText editSearch = (EditText) findViewById(R.id.edit_search);
                    editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if(i == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
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
                    searchActionBar(false,item);
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
                Dialog dialogAnother = new Dialog(this);
                dialogAnother.setContentView(R.layout.dialog_another_user);
                dialogAnother.show();
//                Intent intent3 = new Intent(getApplicationContext(), WellcomeActivity.class);
//                intent3.putExtra("another",true);
//                mAuth.signOut();
//                startActivity(intent3);
//                finish();
                break;
            case R.id.menu_noti:
                dataRef.child("Users").child(mainUser.getUserID()).child("haveNotification").setValue(false);
                item.setIcon(R.drawable.icon_bell);
                Intent intent4 = new Intent(getApplicationContext(),NotificationActivity.class);
                startActivity(intent4);
        }
        return super.onOptionsItemSelected(item);
    }

    //Hàm lọc danh sách content
    private void Filter(int TYPE){
        switch (TYPE)
        {
            case ALL:
                listFilter.clear();
                for(Content content : listContent)
                    listFilter.add(content);
                adapter.notifyDataSetChanged();
                break;
            case TOP10:
                listFilter.clear();
                dialogLoading.show();
                for(Content content : listContent) {
                    if (listFilter.size() < 10)
                        listFilter.add(content);
                    else {
                        int min = 0;
                        for (int i = 0; i < listFilter.size() - 1; i++) {
                            if (listFilter.get(i).getListLike().size() > listFilter.get(i + 1).getListLike().size())
                                min = i + 1;
                        }
                        if (content.getListLike().size() > listFilter.get(min).getListLike().size()) {
                            listFilter.remove(min);
                            listFilter.add(content);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                dialogLoading.dismiss();
                break;

            case TIP:
                listFilter.clear();
                for(Content content : listContent) {
                    if(content.getListTag()!=null)
                        for(Tag tag : content.getListTag())
                            if(tag.getID().equals("tag2"))
                                listFilter.add(content);
                }
                adapter.notifyDataSetChanged();
                break;

            case EXP:
                listFilter.clear();
                for(Content content : listContent) {
                    if(content.getListTag()!=null)
                        for(Tag tag : content.getListTag())
                            if(tag.getID().equals("tag3"))
                                listFilter.add(content);
                }
                adapter.notifyDataSetChanged();
                break;

            case HOTEL:
                listFilter.clear();
                for(Content content : listContent) {
                    if(content.getListTag()!=null)
                        for(Tag tag : content.getListTag())
                            if(tag.getID().equals("tag4"))
                                listFilter.add(content);
                }
                adapter.notifyDataSetChanged();
                break;

            case FOOD:
                listFilter.clear();
                for(Content content : listContent) {
                    if(content.getListTag()!=null)
                        for(Tag tag : content.getListTag())
                            if(tag.getID().equals("tag1"))
                                listFilter.add(content);
                }
                adapter.notifyDataSetChanged();
                break;

            case REVIEW:
                listFilter.clear();
                for(Content content : listContent) {
                    boolean check = true;
                    for (Tag tag : content.getListTag()) {
                        if (tag.getID().equals("tag1") || tag.getID().equals("tag2") ||
                                tag.getID().equals("tag3") || tag.getID().equals("tag4")) {
                            check = false;
                            break;
                        }
                    }
                    if(check)
                        listFilter.add(content);
                }
                adapter.notifyDataSetChanged();
                break;
            case BYTAG:
                listFilter.clear();
                for(Content content : listContent){
                    for(Tag tag : content.getListTag()){
                        if(tagID.equals(tag.getID())) {
                            listFilter.add(content);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
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

    private void searchActionBar(boolean haveSearch, MenuItem item)
    {
        if(haveSearch)
        {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.search_view);
            item.setIcon(R.drawable.icon_x_4);
            search = true;
        }
        else
        {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            item.setIcon(R.drawable.icon_search);
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
    private void unCheckButtonAll(){
        btnExp.setBackgroundResource(R.drawable.button_type_2);
        btnTip.setBackgroundResource(R.drawable.button_type_2);
        btnHotel.setBackgroundResource(R.drawable.button_type_2);
        btnFood.setBackgroundResource(R.drawable.button_type_2);
        btnReview.setBackgroundResource(R.drawable.button_type_2);
        btnTop10.setBackgroundResource(R.drawable.button_type_2);
    }

}