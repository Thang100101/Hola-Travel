package com.example.app_hola;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //Khai báo
    Button btnExit, btnAccess, btnSignin, btnRegist, btnSignout, btnAnotherUser;
    TextView txtTile;
    LinearLayout mainLayout;
    Animation anim_bot_to_top,alpha;
    int check=1;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mapping();
        txtTile.setText("Chào mừng bạn đến với Hola");
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptOut();

            }
        });

        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(MainActivity.this,Home.class);
                startActivity(home);
                overridePendingTransition(R.anim.alpha_type_2,R.anim.alpha_type_2);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signin();
            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Regist();
            }
        });

        CountDownTimer timer = new CountDownTimer(3000,3000) {
            @Override
            public void onTick(long l) {
                if(check==0)
                {
                    txtTile.setText("Chào mừng bạn đến với Hola");
                    txtTile.startAnimation(alpha);
                    mainLayout.setBackgroundResource(R.drawable.background_type_1);
                    check=1;
                }
                else
                {
                    txtTile.setText("Đăng kí để nhận đặc quyền của thành viên nhé!!");
                    txtTile.startAnimation(alpha);
                    mainLayout.setBackgroundResource(R.drawable.background_type_2);
                    check=0;
                }
            }

            @Override
            public void onFinish() {
                this.start();
            }
        };
        timer.start();
        startAnim();

    }
    //Ánh xạ
    private void Mapping()
    {
        btnSignout = (Button) findViewById(R.id.btn_signout);
        btnAnotherUser = (Button) findViewById(R.id.btn_anotheruser) ;
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnAccess = (Button) findViewById(R.id.btn_access);
        btnSignin = (Button) findViewById(R.id.btn_signin);
        btnRegist = (Button) findViewById(R.id.btn_regist);
        txtTile = (TextView) findViewById(R.id.txt_title);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        anim_bot_to_top = AnimationUtils.loadAnimation(this,R.anim.bot_to_top_alpha);
        alpha = AnimationUtils.loadAnimation(this,R.anim.alpha);
    }

    //Chạy animation
    private void startAnim()
    {
        btnAccess.startAnimation(anim_bot_to_top);
        btnRegist.startAnimation(anim_bot_to_top);
        btnSignin.startAnimation(anim_bot_to_top);
        btnAnotherUser.startAnimation(anim_bot_to_top);
        btnSignout.startAnimation(anim_bot_to_top);
    }

    //Dialog
    private void acceptOut()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Khoan đã!!");
        dialog.setMessage("Bạn có chắc chắn muốn thoát không?");
        dialog.setIcon(R.drawable.icon_crying);
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.finish();
            }
        });
        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.show();

    }

    //Signin
    private void Signin()
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_signin);
        dialog.show();
    }

    //Regit
    private void Regist(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_regist);
        dialog.show();
    }
}