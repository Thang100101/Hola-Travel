package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.ImageContent;
import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    Button btnEditName, btnEditBirth, btnEditSex, btnEditPass;
    TextView txtName, txtBirth, txtSex, txtEmail, txtPassword, txtCountContent;
    ImageView imgAvatar;
    FirebaseAuth mAuth;
    DatabaseReference dataRef;
    User user;
    Dialog dialogLoading;
    int countContent =0;
    int REQUEST_CODE_TAKE_IMAGE =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        InitActionBar();
        Mapping();

        dialogLoading.show();
        dataRef.child("Users").child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    user = task.getResult().getValue(User.class);
                    txtEmail.setText(user.getUsername());
                    txtPassword.setText(user.getPassword());
                    Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                    if(query!=null) {
                        countContentForuser(query,"count");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialogLoading.dismiss();
                            }
                        }, 1000);
                    }
                    loadingProfile();
                }
            }
        });


        eventHandler();

    }
    private void InitActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("Thông tin cá nhân");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Ánh xạ
    private void Mapping(){
        btnEditName = (Button) findViewById(R.id.btn_edit_name);
        btnEditBirth = (Button) findViewById(R.id.btn_edit_birth);
        btnEditSex = (Button) findViewById(R.id.btn_edit_sex);
        btnEditPass = (Button) findViewById(R.id.btn_edit_pass);
        txtEmail = (TextView) findViewById(R.id.txt_email);
        txtPassword = (TextView) findViewById(R.id.txt_pass);
        txtBirth = (TextView) findViewById(R.id.txt_birth);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtSex = (TextView) findViewById(R.id.txt_sex);
        txtCountContent = (TextView) findViewById(R.id.txt_count_content);
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        mAuth = FirebaseAuth.getInstance();
        dataRef = FirebaseDatabase.getInstance().getReference();
        dialogLoading = new Dialog(this);
        dialogLoading.setContentView(R.layout.dialog_loading);
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

    private void loadingProfile(){
        if(user!=null) {
            Picasso.get().load(user.getAvatar().getLink()).into(imgAvatar);
            txtName.setText(user.getName());
            txtBirth.setText(user.getBirth());
            txtSex.setText(user.getSex());
        }
    }

    ///Đếm số lượng bài viết
    private void countContentForuser(Query query, String type)
    {
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(type.equals("count")) {
                    countContent++;
                    txtCountContent.setText(txtCountContent.getText().toString() + countContent + "");
                }
                else
                {
                    Content content = snapshot.getValue(Content.class);
                    content.setUser(user);
                    dataRef.child("Contents").child(content.getID()).setValue(content);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Bắt sự kiện các View trong Activity
    private void eventHandler() {
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEdit("name");
            }
        });
        btnEditSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEdit("sex");
            }
        });
        btnEditBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEdit("birth");
            }
        });
        btnEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEdit("password");
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_TAKE_IMAGE && resultCode == RESULT_OK && data!=null)
        {
            Picasso.get().load(data.getData()).into(imgAvatar);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    upLoadImg();
                }
            },500);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Upload Avatar
    private void upLoadImg() {
        Dialog dialogLoading = new Dialog(ProfileActivity.this);
        dialogLoading.setContentView(R.layout.dialog_loading);
        dialogLoading.show();
        dialogLoading.setCancelable(false);
        Calendar calendar = Calendar.getInstance();
        String name = user.getUserID() + calendar.getTimeInMillis();
        imgAvatar.setDrawingCacheEnabled(true);
        imgAvatar.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgAvatar.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] dataImg = baos.toByteArray();
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storage.child("imageAvatar/" + name + ".png");
        StorageReference deleteImg = storage.child("imageAvatar/" + user.getAvatar().getName());
        deleteImg.delete();
        UploadTask uploadTask = imgRef.putBytes(dataImg);
        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(task.isSuccessful())
                    return imgRef.getDownloadUrl();
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                ImageContent img = new ImageContent();
                img.setName(name+".png");
                img.setLink(task.getResult().toString());
                user.setAvatar(img);
                dataRef.child("Users").child(user.getUserID()).setValue(user);
                Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                if(query!=null) {
                    countContentForuser(query, "change");
                }
                Toast.makeText(ProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                dialogLoading.dismiss();
            }
        });
    }

    //Show các dialog theo các View
    private void showDialogEdit(String type)
    {
        if(type.equals("birth"))
        {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            DatePickerDialog dialog = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    calendar.set(i, i1, i2);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String birth = simpleDateFormat.format(calendar.getTime());
                    user.setBirth(birth);
                    txtBirth.setText(birth);
                    dataRef.child("Users").child(user.getUserID()).setValue(user);
                    Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                    if(query!=null) {
                        countContentForuser(query, "change");
                    }
                }
            }, year, month, day);
            dialog.show();
        }
        else if(type.equals("name"))
        {
            Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_name);
            InitDialog(dialog);
            EditText editName = (EditText) dialog.findViewById(R.id.edit_name);
            Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!editName.getText().toString().equals("")) {
                        user.setName(editName.getText().toString());
                        txtName.setText(editName.getText().toString());
                        dataRef.child("Users").child(user.getUserID()).setValue(user);
                        Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                        if(query!=null) {
                            countContentForuser(query, "change");
                        }
                        dialog.dismiss();
                    }else
                    {
                        Toast.makeText(ProfileActivity.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        else if(type.equals("sex"))
        {
            Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_sex);
            InitDialog(dialog);
            Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
            RadioGroup Rdg = (RadioGroup) dialog.findViewById(R.id.rdg);
            RadioButton rdMale = (RadioButton) dialog.findViewById(R.id.rd_male);
            RadioButton rdFemale = (RadioButton) dialog.findViewById(R.id.rd_female);
            if (txtSex.getText().equals("Nam"))
                rdMale.setChecked(true);
            else
                rdFemale.setChecked(true);
            dialog.show();
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(rdMale.isChecked() && !(user.getSex().toString().equals("Nam"))) {
                        user.setSex("Nam");
                        txtSex.setText("Nam");
                        dataRef.child("Users").child(user.getUserID()).setValue(user);
                        Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                        if(query!=null) {
                            countContentForuser(query, "change");
                        }
                        dialog.dismiss();
                    }
                    else if(rdFemale.isChecked() && !(user.getSex().toString().equals("Nữ"))){
                        user.setSex("Nữ");
                        txtSex.setText("Nữ");
                        dataRef.child("Users").child(user.getUserID()).setValue(user);
                        Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                        if(query!=null) {
                            countContentForuser(query, "change");
                        }
                        dialog.dismiss();
                    }
                    else
                        dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        else if (type.equals("password")){
            Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_password);
            InitDialog(dialog);
            Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
            EditText editOldPass = (EditText) dialog.findViewById(R.id.edit_old_password);
            EditText editNewPass = (EditText) dialog.findViewById(R.id.edit_new_password);
            EditText editConfirm = (EditText) dialog.findViewById(R.id.edit_confirm_password);

            String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String oldPassword = user.getPassword();

            dialog.show();

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editOldPass.getText().toString().equals(oldPassword)) {
                        Toast.makeText(ProfileActivity.this,
                                "Sai mật khẩu hiện tại!", Toast.LENGTH_LONG).show();
                    }else if (!editNewPass.getText().toString().equals(editConfirm.getText().toString())) {
                        Toast.makeText(ProfileActivity.this,
                                "Xác nhận mật khẩu không chính xác!", Toast.LENGTH_LONG).show();
                    }else if (oldPassword.equals(editNewPass.getText().toString())){
                        Toast.makeText(ProfileActivity.this,
                                "Mật khẩu mới phải khác mật khẩu hiện tại!", Toast.LENGTH_LONG).show();
                    }else if (editNewPass.getText().toString().length() < 8){
                        Toast.makeText(ProfileActivity.this,
                                "Mật khẩu phải đủ 8 kí tự!",Toast.LENGTH_LONG).show();
                    }else {
                        mAuth.getCurrentUser().updatePassword(editNewPass.getText().toString());
                        user.setPassword(editNewPass.getText().toString());
                        dataRef.child("Users").child(user.getUserID()).setValue(user);
                        Query query = dataRef.child("Contents").orderByChild("user/userID").equalTo(user.getUserID());
                        if(query!=null) {
                            countContentForuser(query, "change");
                        }
                        dialog.dismiss();
                        txtPassword.setText(user.getPassword());
                        Toast.makeText(ProfileActivity.this,
                                "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                    }
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
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

}
