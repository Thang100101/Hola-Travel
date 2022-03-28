package com.example.app_hola;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Khai báo
    Button btnExit, btnAccess, btnSignin, btnRegist, btnSignout, btnAnotherUser;
    TextView txtTile, txtUser;
    LinearLayout mainLayout;
    Animation anim_bot_to_top,alpha;
    SharedPreferences prefer;
    int check=1;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mapping();
        CheckHaveSignin();


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
                Intent home = new Intent(MainActivity.this, HomeActivity.class);
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

        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = prefer.edit();
                edit.remove("user");
                edit.remove("pass");
                edit.remove("checkrmb");
                edit.putBoolean("havesignin",false);
                edit.commit();
                ChangeMainButton(false);
                txtUser.setText("");
            }
        });

        btnAnotherUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor edit = prefer.edit();
                edit.remove("user");
                edit.remove("pass");
                edit.remove("checkrmb");
                edit.commit();
                ChangeMainButton(false);
                txtUser.setText("");
                Signin();
            }
        });

        //Chạy chữ chào mừng
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
        txtUser = (TextView) findViewById(R.id.txt_user);
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

    @Override
    public void onBackPressed() {
        acceptOut();
//        super.onBackPressed();
    }

    //Dialog đăng nhập
    private void Signin()
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_signin);
        dialog.show();
        //Mapping
        EditText editUser = (EditText) dialog.findViewById(R.id.edit_user);
        EditText editPass = (EditText) dialog.findViewById(R.id.edit_pass);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        Button btnBack = (Button) dialog.findViewById(R.id.btn_back);
        CheckBox cbRemember = (CheckBox) dialog.findViewById(R.id.cb_remember);
        // Kiểm tra đăng nhập
        prefer = getSharedPreferences("rememberlogin",MODE_PRIVATE);

        editUser.setText(prefer.getString("user",""));
        editPass.setText(prefer.getString("pass",""));
        cbRemember.setChecked(prefer.getBoolean("checkrmb",false));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CheckSignin(editUser.getText().toString(), editPass.getText().toString()))
                {
                    if(cbRemember.isChecked())
                    {
                        SharedPreferences.Editor edit = prefer.edit();
                        edit.putString("user", editUser.getText().toString());
                        edit.putString("pass", editPass.getText().toString());
                        edit.putBoolean("checkrmb",true);
                        edit.putBoolean("havesignin",true);
                        edit.commit();
                        ChangeMainButton(true);
                        txtUser.setText("Tài khoản " + editUser.getText().toString());
                        dialog.dismiss();
                    }
                    else
                    {
                        SharedPreferences.Editor edit = prefer.edit();
                        edit.remove("user");
                        edit.remove("pass");
                        edit.remove("checkrmb");
                        edit.commit();
                        ChangeMainButton(true);
                        txtUser.setText("Tài khoản " + editUser.getText().toString());
                        dialog.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //Dialog đăng kí
    private void Regist(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_regist);
        dialog.show();
        //Kiểm tra đăng kí
    }
    ///Kiểm tra đăng nhập, đăng kí
    private boolean CheckSignin(String user, String pass)
    {
        if(user.equals("thang") && pass.equals("123"))
            return true;
        return false;
    }
    private boolean CheckRegist(String user, String pass, String confirm)
    {
        if(true && (pass.equals(confirm)))
            return true;
        return false;
    }

    //Kiểm tra đã đăng nhập từ trước
    private void CheckHaveSignin()
    {
        prefer = getSharedPreferences("rememberlogin",MODE_PRIVATE);
        if(prefer.getBoolean("havesignin",false))
        {
            txtUser.setText("Tài khoản "+prefer.getString("user",""));
            ChangeMainButton(true);
        }
        else
        {
            txtUser.setText("");
            ChangeMainButton(false);
        }
    }
    //thay đổi button theo trạng thái
    private void ChangeMainButton(boolean signin)
    {
        if(signin)
        {
            btnSignout.setVisibility(View.VISIBLE);
            btnAnotherUser.setVisibility(View.VISIBLE);
            btnSignin.setVisibility(View.GONE);
            btnRegist.setVisibility(View.GONE);
        }
        else
        {
            btnSignout.setVisibility(View.GONE);
            btnAnotherUser.setVisibility(View.GONE);
            btnSignin.setVisibility(View.VISIBLE);
            btnRegist.setVisibility(View.VISIBLE);
        }
    }
}