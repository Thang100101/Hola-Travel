package com.example.app_hola;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.app_hola.ObjectForApp.Tag;
import com.google.firebase.auth.FirebaseAuth;

public class LogoActivity extends AppCompatActivity {
    ImageView imgLogo;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        imgLogo = (ImageView) findViewById(R.id.img_logo);
        mAuth = FirebaseAuth.getInstance();
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.alpha);
        imgLogo.startAnimation(animation);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
//        Tag.createListTag();

        if(mAuth.getCurrentUser()!=null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("havesignin", true);
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.alpha_type_2,R.anim.alpha_type_2);
                    finish();
                }
            }, 2000);
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.putExtra("havesignin", false);
                    setResult(RESULT_OK, intent);
                    overridePendingTransition(R.anim.alpha_type_2,R.anim.alpha_type_2);
                    finish();
                }
            }, 2000);
        }
    }


}