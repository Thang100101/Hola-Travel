package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    HorizontalScrollView hScrollView;
    Button btnUpload;
    ArrayList<Content> listContent = new ArrayList<Content>();
    ListView listViewContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Mapping();
        addList();
        ListViewContent adapter = new ListViewContent(listContent,this);
        listViewContent.setAdapter(adapter);
        btnUpload.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(Home.this, "Đăng bài", Toast.LENGTH_SHORT).show();
                return false;
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
                Intent profile = new Intent(Home.this, Profile.class);
                startActivity(profile);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}