package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {
    Button btnEditName, btnEditBirth, btnEditSex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Mapping();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("Thông tin cá nhân");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void Mapping(){
        btnEditName = (Button) findViewById(R.id.btn_edit_name);
        btnEditBirth = (Button) findViewById(R.id.btn_edit_birth);
        btnEditSex = (Button) findViewById(R.id.btn_edit_sex);
    }

    //Tạo và bắt sự kiện cho menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
               ProfileActivity.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}