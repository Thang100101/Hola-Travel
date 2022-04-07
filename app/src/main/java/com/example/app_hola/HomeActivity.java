package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.Content;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    HorizontalScrollView hScrollView;
    Button btnUpload;
    ArrayList<Content> listContent = new ArrayList<Content>();
    ListView listViewContent;
    ActionBar actionBar;
    SharedPreferences prefer;
    boolean search =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Mapping();
        addList();
        customActionBar();

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

    private void Mapping(){
        hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        listViewContent = (ListView) findViewById(R.id.listContent);
        prefer = getSharedPreferences("rememberlogin",MODE_PRIVATE);

    }
    private void addList(){
    }

    //Tạo và bắt sự kiện cho menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(true)
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
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("signin",true);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_signout:
                Intent intent2 = new Intent(getApplicationContext(),MainActivity.class);
                intent2.putExtra("signout",true);
                startActivity(intent2);
                finish();
                break;
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
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
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

    //Dialog đăng nhập

}