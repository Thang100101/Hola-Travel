package com.example.app_hola;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.app_hola.ObjectForApp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity  {
    public static final String EXTRA_MESSAGE="com.example.app_hola.MESSAGE";
    HorizontalScrollView hScrollView;
    String txtUser,txtTitle,txtMaincontent,txtDate,txtLink;
    String[] list;
    Button btnUpload;
    List<Content> contents;
    List<User> users;
    ArrayList<Content> listContent = new ArrayList<Content>();
    ListView listViewContent;
    ActionBar actionBar;
    SharedPreferences prefer;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference reference;
    ContentApdapter contentApdapter;
    boolean search =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        listViewContent=(ListView) findViewById(R.id.listContent) ;
        contents=new ArrayList<>();

            contentApdapter = new ContentApdapter(this, contents);
            listViewContent.setAdapter(contentApdapter);

        Mapping();
        addList();
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
                Intent intent=new Intent(HomeActivity.this,ReadContent.class);
                txtUser=contents.get(i).getUserID().getUsername();
                txtDate=contents.get(i).getDate();
                txtLink=contents.get(i).getImageContent().getLink();
                txtMaincontent=contents.get(i).getMainContent();
                txtTitle=contents.get(i).getTitle();
                list=new String[]{txtTitle,txtUser,txtLink,txtDate,txtMaincontent};
                intent.putExtra(EXTRA_MESSAGE,list);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, list[1], Toast.LENGTH_SHORT).show();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CreateReviewActivity.class);
                startActivity(intent);
            }
        });

    }
    private void getInfOfContent(){
//        users=new ArrayList<>();
//        reference= FirebaseDatabase.getInstance().getReference("Users");
//        User user=new User("1951012131","thien@gmail.com","123456");
//        reference.child("Thien's acount").setValue(user);
//        reference=FirebaseDatabase.getInstance().getReference();
//        ImageContent imageContent=new ImageContent("HaNoi.jfif","HaNoi");
//        reference.child("ImageContents").child(imageContent.getContentID()).setValue(imageContent);
//        Content content=new Content(imageContent,"Nội dung","Phong cảnh Hà Nội",user,"0","10/04/2022");
//        reference.child("Contents").child(content.getID()).setValue(content);
        reference= FirebaseDatabase.getInstance().getReference("Contents");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                contents.add(snapshot.getValue(Content.class));
                contentApdapter.notifyDataSetChanged();
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
    }

    private void Mapping(){
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        listViewContent = (ListView) findViewById(R.id.listContent);
        prefer = getSharedPreferences("rememberlogin",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void addList(){
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

    private void testFilter(){

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