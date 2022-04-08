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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements Serializable{
    //Khai báo
    private FirebaseAuth mAuth;
    Button btnExit, btnAccess, btnSignin, btnRegist, btnAnotherUser;
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
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        boolean signin = intent.getBooleanExtra("signin",false);
        if(signin)
            Signin(MainActivity.this);
        boolean signout = intent.getBooleanExtra("signout",false);
        if(signout)
            Signout();


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
                Signin(MainActivity.this);
            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Regist();
            }
        });


        btnAnotherUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signout();
                Signin(MainActivity.this);
            }
        });

        //Chạy chữ chào mừng
        runWellcome();
        startAnim();

    }
    //Ánh xạ
    private void Mapping()
    {
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

    @Override
    public void onBackPressed() {
        acceptOut();
//        super.onBackPressed();
    }

    //Dialog đăng kí
    private boolean regisStatus = false;
    private void Regist(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_regist);
        dialog.show();
        //Kiểm tra đăng kí
        EditText editUser = (EditText) dialog.findViewById(R.id.edit_user);
        EditText editPass = (EditText) dialog.findViewById(R.id.edit_pass);
        EditText editConfirm = (EditText) dialog.findViewById(R.id.edit_confirm);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_submit);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckRegist(editUser.getText().toString(),
                        editPass.getText().toString(), editConfirm.getText().toString());
                if (regisStatus){
                    dialog.dismiss();
                    Signin(MainActivity.this);
                }
            }
        });
    }
    private void CheckRegist(String user, String pass, String confirm)
    {
        if(!pass.equals(confirm)){
            Toast.makeText(MainActivity.this, "Mật khẩu không trùng khớp!", Toast.LENGTH_LONG).show();
        }else if (pass.length() < 8) {
            Toast.makeText(MainActivity.this, "Mật khẩu phải lớn hơn 8 kí tự", Toast.LENGTH_LONG).show();
        }else {
            register(user, pass);
        }
    }
    private void register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Successful.",
                                    Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            regisStatus = true;
                            return;
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    //Dialog đăng nhập
    public void Signin(Context context)
    {
        Dialog dialog = new Dialog(context);
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
                CheckSignin(editUser.getText().toString(),
                        editPass.getText().toString(), cbRemember.isChecked());
            }
        });

    }
    ///Kiểm tra đăng nhập, đăng kí
    private void CheckSignin(String email, String password, boolean rememberStatus)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if (rememberStatus){
                                SharedPreferences.Editor edit = prefer.edit();
                                edit.putString("user", email);
                                edit.putString("pass", password);
                                edit.putBoolean("checkrmb",true);
                                edit.putBoolean("havesignin",true);
                                edit.commit();
                                ChangeMainButton(true);
                                txtUser.setText("Tài khoản " + email);
                            }
                            else {
                                SharedPreferences.Editor edit = prefer.edit();
                                edit.remove("user");
                                edit.remove("pass");
                                edit.remove("checkrmb");
                                edit.commit();
                                ChangeMainButton(true);
                                txtUser.setText("Tài khoản " + email);
                            }
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                            return;
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
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
            btnAnotherUser.setVisibility(View.VISIBLE);
            btnSignin.setVisibility(View.GONE);
            btnRegist.setVisibility(View.GONE);
        }
        else
        {
            btnAnotherUser.setVisibility(View.GONE);
            btnSignin.setVisibility(View.VISIBLE);
            btnRegist.setVisibility(View.VISIBLE);
        }
    }

    //Chạy TextView chào mừng
    private void runWellcome(){
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
    }

    ///Đăng xuất
    private void Signout(){
        SharedPreferences.Editor edit = prefer.edit();
        edit.remove("user");
        edit.remove("pass");
        edit.remove("checkrmb");
        edit.putBoolean("havesignin",false);
        edit.commit();
        ChangeMainButton(false);
        txtUser.setText("");
    }
}