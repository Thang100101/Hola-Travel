package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.ImageContent;
import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class WellcomeActivity extends AppCompatActivity implements Serializable{
    //Khai báo
    private FirebaseAuth mAuth;
    Button btnExit, btnAccess, btnSignin, btnRegist;
    TextView txtTile;
    LinearLayout mainLayout;
    Animation anim_bot_to_top,alpha;
    Dialog dialogSignin, dialogRegist, dialogLoading;
    int check=1;
    int REQUEST_CODE = 1;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);
        Mapping();
        mAuth = FirebaseAuth.getInstance();

//        CheckHaveSignin();

        Intent intent = getIntent();
        boolean signout = intent.getBooleanExtra("signout",false);
        boolean signin = intent.getBooleanExtra("signin",false);
        boolean anotherUser = intent.getBooleanExtra("another", false);
        if(signout)
            Signout();
        else if(signin)
            Signin(WellcomeActivity.this);
        else if(anotherUser)
        {
            Signout();
            Signin(this);
        }
        else
        {
            Intent intentLogo = new Intent(getApplicationContext(), LogoActivity.class);
            startActivityForResult(intentLogo, REQUEST_CODE);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtTile.setText(getResources().getString(R.string.wellcome_1));
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptOut();
            }
        });

        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(WellcomeActivity.this, HomeActivity.class);
                startActivity(home);
                overridePendingTransition(R.anim.alpha_type_2,R.anim.alpha_type_2);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Signin(WellcomeActivity.this);
            }
        });

        btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Regist();
            }
        });



        //Chạy chữ chào mừng
        runWellcome();
        startAnim();

    }
    //Ánh xạ
    private void Mapping()
    {
//        btnAnotherUser = (Button) findViewById(R.id.btn_anotheruser) ;
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnAccess = (Button) findViewById(R.id.btn_access);
        btnSignin = (Button) findViewById(R.id.btn_signin);
        btnRegist = (Button) findViewById(R.id.btn_regist);
        txtTile = (TextView) findViewById(R.id.txt_title);
        mainLayout = (LinearLayout) findViewById(R.id.main_layout);
        anim_bot_to_top = AnimationUtils.loadAnimation(this,R.anim.bot_to_top_alpha);
        alpha = AnimationUtils.loadAnimation(this,R.anim.alpha);
        mAuth = FirebaseAuth.getInstance();

        dialogSignin = new Dialog(WellcomeActivity.this);
        dialogSignin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSignin.setContentView(R.layout.dialog_signin);
        InitDialog(dialogSignin);

        dialogRegist = new Dialog(WellcomeActivity.this);
        dialogRegist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogRegist.setContentView(R.layout.dialog_regist);
        InitDialog(dialogRegist);

        dialogLoading = new Dialog(WellcomeActivity.this);
        dialogLoading.setContentView(R.layout.dialog_loading);
    }

    private void InitDialog(Dialog dialog) {
        //Init Dialog
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
    }

    //Chạy animation
    private void startAnim()
    {
        btnAccess.startAnimation(anim_bot_to_top);
        btnRegist.startAnimation(anim_bot_to_top);
        btnSignin.startAnimation(anim_bot_to_top);
    }

    //Dialog thoát
    private void acceptOut()
    {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.wait));
        dialog.setMessage(getResources().getString(R.string.want_exit));
        dialog.setIcon(R.drawable.icon_crying);
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), WellcomeActivity.class);
                startActivity(intent);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                finish();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
    private void Regist(){
        dialogRegist.show();
        //Kiểm tra đăng kí
        EditText editUser = (EditText) dialogRegist.findViewById(R.id.edit_user);
        EditText editPass = (EditText) dialogRegist.findViewById(R.id.edit_pass);
        EditText editConfirm = (EditText) dialogRegist.findViewById(R.id.edit_confirm);
        Button btnConfirm = (Button) dialogRegist.findViewById(R.id.btn_submit);
        Button btnBack = (Button) dialogRegist.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRegist.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editUser.getText().toString();
                String pass = editPass.getText().toString();
                String confirm = editConfirm.getText().toString();
                if(CheckRegist(email, pass, confirm))
                {
                    //Tiến hành đăng kí
                    dialogLoading.show();
                    dialogRegist.hide();
                    mAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(WellcomeActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(WellcomeActivity.this, getResources().getString(R.string.regist_success),
                                                Toast.LENGTH_SHORT).show();

                                        ///Tạo User
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        Calendar calendar = Calendar.getInstance();
                                        String birth = simpleDateFormat.format(calendar.getTime());
                                        User user = new User(mAuth.getCurrentUser().getUid(),email,pass);
                                        user.setBirth(birth);
                                        user.setName("Chưa có tên");
                                        user.setSex("Nam");
                                        user.setHaveNotification(false);
                                        ImageContent img = new ImageContent();
                                        img.setLink("https://firebasestorage.googleapis.com/v0/b/hola-travel.appspot" +
                                                ".com/o/avatar.png?alt=media&token=7733012b-0e01-4bcf-8e7b-cad46b2ef22c");
                                        user.setAvatar(img);
                                        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("Users");
                                        dataRef.child(user.getUserID()).setValue(user);
                                        dialogRegist.dismiss();
                                        dialogLoading.dismiss();
                                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                        startActivity(intent);
                                        return;
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(WellcomeActivity.this,
                                                getResources().getString(R.string.regist_invalid),
                                                Toast.LENGTH_SHORT).show();
                                        dialogLoading.dismiss();
                                        dialogRegist.show();
                                        return;
                                    }
                                }
                            });
                }
            }
        });
    }
    private boolean CheckRegist(String user, String pass, String confirm)
    {
        if(user.replace(" ","").isEmpty() || pass.replace(" ","").isEmpty() ||
                confirm.replace(" ","").isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.empty_user_pass), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!pass.equals(confirm)){
            Toast.makeText(WellcomeActivity.this, getResources().getString(R.string.regist_confirm_false), Toast.LENGTH_LONG).show();
            return false;
        }else if (pass.length() < 8) {
            Toast.makeText(WellcomeActivity.this, getResources().getString(R.string.regist_length_pass), Toast.LENGTH_LONG).show();
            return false;
        }else {
            return true;
        }
    }


    //Dialog đăng nhập
    public void Signin(Context context)
    {
        dialogSignin.show();
        //Mapping
        EditText editUser = (EditText) dialogSignin.findViewById(R.id.edit_user);
        EditText editPass = (EditText) dialogSignin.findViewById(R.id.edit_pass);
        Button btnSubmit = (Button) dialogSignin.findViewById(R.id.btn_submit);
        Button btnBack = (Button) dialogSignin.findViewById(R.id.btn_back);
        // Kiểm tra đăng nhập

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSignin.dismiss();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(editUser.getText().toString().replace(" ","").equals("")
                ||editPass.getText().toString().replace(" ","").equals("")))
                    CheckSignin(editUser.getText().toString(),
                            editPass.getText().toString());
                else
                    Toast.makeText(context, getResources().getString(R.string.empty_user_pass), Toast.LENGTH_SHORT).show();
            }
        });

    }
    ///Kiểm tra đăng nhập, đăng kí
    private void CheckSignin(String email, String password)
    {
        dialogLoading.show();
        dialogSignin.hide();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            ChangeMainButton(true);
                            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(intent);
                            dialogLoading.dismiss();
                            dialogSignin.dismiss();
                            return;
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(WellcomeActivity.this, getResources().getString(R.string.user_pass_not_correct),
                                    Toast.LENGTH_SHORT).show();
                            dialogLoading.dismiss();
                            dialogSignin.show();
                            return;
                        }
                    }
                });
    }

    //Kiểm tra đã đăng nhập từ trước
//    private void CheckHaveSignin()
//    {
//        if(mAuth.getCurrentUser()!=null)
//        {
//            ChangeMainButton(true);
//        }
//        else
//            ChangeMainButton(false);
//    }
    //thay đổi button theo trạng thái
    private void ChangeMainButton(boolean signin)
    {
        if(signin)
        {
            btnSignin.setVisibility(View.GONE);
            btnRegist.setVisibility(View.GONE);
        }
        else
        {
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
                    txtTile.setText(getResources().getString(R.string.wellcome_1));
                    txtTile.startAnimation(alpha);
                    mainLayout.setBackgroundResource(R.drawable.background_type_1);
                    check=1;
                }
                else
                {
                    txtTile.setText(getResources().getString(R.string.wellcome_2));
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
        ChangeMainButton(false);
        mAuth.signOut();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            if(data.getBooleanExtra("havesignin",false)==true)
            {
                ChangeMainButton(true);
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }
            else
            {
                ChangeMainButton(false);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}