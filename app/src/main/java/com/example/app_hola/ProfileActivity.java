package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    Button btnEditName, btnEditBirth, btnEditSex;
    TextView txtName, txtBirth, txtSex;
    ImageView imgAvatar;
    FirebaseAuth mAuth;
    DatabaseReference dataRef;
    User user;
    Dialog dialogLoading;
    int REQUEST_CODE_TAKE_IMAGE =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Mapping();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle("Thông tin cá nhân");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialogLoading.show();
        dataRef.child("Users").child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    user = task.getResult().getValue(User.class);
                    loadingProfile();
                    dialogLoading.dismiss();
                }
            }
        });

        eventHandler();

    }

    //Ánh xạ
    private void Mapping(){
        btnEditName = (Button) findViewById(R.id.btn_edit_name);
        btnEditBirth = (Button) findViewById(R.id.btn_edit_birth);
        btnEditSex = (Button) findViewById(R.id.btn_edit_sex);
        txtBirth = (TextView) findViewById(R.id.txt_birth);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtSex = (TextView) findViewById(R.id.txt_sex);
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
            Picasso.get().load(user.getAvatar()).into(imgAvatar);
            txtName.setText(user.getName());
            txtBirth.setText(user.getBirth());
            txtSex.setText(user.getSex());
        }
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
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                }
            }, year, month, day);
            dialog.show();
        }
        else if(type.equals("name"))
        {
            Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.setContentView(R.layout.dialog_edit_name);
            EditText editName = (EditText) dialog.findViewById(R.id.edit_name);
            Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!editName.getText().toString().equals("")) {
                        user.setName(editName.getText().toString());
                        txtName.setText(editName.getText().toString());
                        dataRef.child("Users").child(user.getUserID()).setValue(user);
                        dialog.dismiss();
                    }else
                    {
                        Toast.makeText(ProfileActivity.this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }
        else if(type.equals("sex"))
        {
            Dialog dialog = new Dialog(ProfileActivity.this);
            dialog.setContentView(R.layout.dialog_edit_sex);
            Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
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
                        dialog.dismiss();
                    }
                    else if(rdFemale.isChecked() && !(user.getSex().toString().equals("Nữ"))){
                        user.setSex("Nữ");
                        txtSex.setText("Nữ");
                        dataRef.child("Users").child(user.getUserID()).setValue(user);
                        dialog.dismiss();
                    }
                    else
                        dialog.dismiss();
                }
            });
        }
    }

}
