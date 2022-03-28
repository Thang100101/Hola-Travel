package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CreateReviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        Mapping();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("Bài viết mới");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }

    private void Mapping(){

    }

    //Tạo và bắt sự kiện cho menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                CreateDialog();
                break;
            case R.id.menu_save:
                Toast.makeText(this, "Đã lưu bản sao bài viết", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    //Sự kiến bấm nút back trên android
    @Override
    public void onBackPressed() {
        CreateDialog();
//        super.onBackPressed();
    }

    //Tạo dialog thông báo
    private void CreateDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateReviewActivity.this);
        dialog.setTitle("Thông báo!!");
        dialog.setMessage("Bài viết của bạn chưa được lưu bạn có chắc chắn muốn thoát?");
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                CreateReviewActivity.this.finish();
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