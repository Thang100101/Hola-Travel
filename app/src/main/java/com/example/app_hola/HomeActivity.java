package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    HorizontalScrollView hScrollView;
    Button btnUpload;
    ArrayList<Content> listContent = new ArrayList<Content>();
    ListView listViewContent;
    ActionBar actionBar;
    boolean search =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Mapping();
        addList();

        //Thay đổi UI của action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("");
        actionBar.setLogo(R.drawable.logo_3);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

        ListViewContent adapter = new ListViewContent(listContent,this);
        listViewContent.setAdapter(adapter);
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
                TextView txt = (TextView) view.findViewById(R.id.txt_content);
                Toast.makeText(HomeActivity.this, ""+txt.getText()
                        , Toast.LENGTH_SHORT).show();
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
    }
    private void addList(){
        listContent.add(new Content(R.drawable.ha_noi,"Review về Hà nội", "10/01/2001"));
        listContent.add(new Content(R.drawable.ha_giang,"Review về Hà Giang", "10/01/2001"));
        listContent.add(new Content(R.drawable.sa_pa,"Review về Sapa", "10/01/2001"));
        listContent.add(new Content(R.drawable.vinh_ha_long,"Review về Vịnh Hạ Long", "10/01/2001"));
        listContent.add(new Content(R.drawable.ninh_binh,"Review về Ninh Bình", "10/01/2001"));
        listContent.add(new Content(R.drawable.ha_noi,"Review về Hà nội", "10/01/2001"));
        listContent.add(new Content(R.drawable.ha_giang,"Review về Hà Giang", "10/01/2001"));
        listContent.add(new Content(R.drawable.sa_pa,"Review về Sapa", "10/01/2001"));
        listContent.add(new Content(R.drawable.vinh_ha_long,"Review về Vịnh Hạ Long", "10/01/2001"));
        listContent.add(new Content(R.drawable.ninh_binh,"Review về Ninh Bình", "10/01/2001"));
        listContent.add(new Content(R.drawable.ha_noi,"Review về Hà nội", "10/01/2001"));
        listContent.add(new Content(R.drawable.ha_giang,"Review về Hà Giang", "10/01/2001"));
        listContent.add(new Content(R.drawable.sa_pa,"Review về Sapa", "10/01/2001"));
        listContent.add(new Content(R.drawable.vinh_ha_long,"Review về Vịnh Hạ Long", "10/01/2001"));
        listContent.add(new Content(R.drawable.ninh_binh,"Review về Ninh Bình", "10/01/2001"));
    }

    //Tạo và bắt sự kiện cho menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
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
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.setDisplayUseLogoEnabled(false);
                    actionBar.setDisplayShowCustomEnabled(true);
                    actionBar.setCustomView(R.layout.search_view);
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
                    search = true;
                }
                else
                {
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.setDisplayShowCustomEnabled(false);
                    search=false;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void testFilter(){

    }
}