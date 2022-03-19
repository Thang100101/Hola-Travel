package com.example.app_hola;

import androidx.appcompat.app.AppCompatActivity;

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
    Button btnExit, btnAccess, btnSignin, btnRegist;
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
                finish();
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

        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(MainActivity.this,Home.class);
                startActivity(home);
                overridePendingTransition(R.anim.alpha_type_2,R.anim.alpha_type_2);
            }
        });
    }
    //Ánh xạ
    private void Mapping()
    {
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
    }
}