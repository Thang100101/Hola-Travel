package com.example.app_hola;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.ImageContent;
import com.example.app_hola.ObjectForApp.Location;
import com.example.app_hola.ObjectForApp.Tag;
import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import okio.Timeout;

public class CreateContentActivity extends AppCompatActivity implements View.OnClickListener{

    Content content;
    EditText editTitle, editContent;
    Button btnUpload, btnAddImg, btnAddTag, btnAddLocation, btnLocation;
    Button [] listButtonDelete = new Button[5];
    ImageView [] listImage = new ImageView[5];
    Button [] listButtonTag = new Button[3];
    SharedPreferences prefer;
    SharedPreferences.Editor editPrefer;
    ArrayList<ImageContent> listImgContent;
    HorizontalScrollView hori;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference dataRef;
    Location location=null;
    int REQUEST_CODE_TAKE_IMAGE = 1, REQUEST_CODE_TAKE_LOCATION=2;
    int imgCount=0;
    boolean haveSave=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_review);
        Mapping();
        loadButton();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle(getResources().getString(R.string.new_post));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTitle.setText(prefer.getString("title",""));
        editContent.setText(prefer.getString("content",""));

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadContent();
            }
        });

        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgCount<5) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_TAKE_IMAGE);
                }else
                    Toast.makeText(CreateContentActivity.this, getResources().getString(R.string.max_img), Toast.LENGTH_SHORT).show();
            }
        });

        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialogTag = new Dialog(CreateContentActivity.this);
                dialogTag.setContentView(R.layout.dialog_list_tag);
                dialogTag.show();
                ListView listView = (ListView) dialogTag.findViewById(R.id.list_tag);
                ArrayList<Tag> listTag = new ArrayList<Tag>();
                ArrayAdapter<Tag> adapter = new ArrayAdapter<>(CreateContentActivity.this,
                        android.R.layout.simple_list_item_1,listTag);
                listView.setAdapter(adapter);
                dataRef.child("Tags").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Tag tag = snapshot.getValue(Tag.class);
                        tag.setContext(CreateContentActivity.this);
                        listTag.add(tag);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        for(int j = 0; j<listButtonTag.length; j++)
                        {
                            if(listButtonTag[j].getTag()!=null)
                                if(((Tag)listButtonTag[j].getTag()).toString().equals(listTag.get(i).toString())) {
                                    Toast.makeText(CreateContentActivity.this,
                                            getResources().getString(R.string.already_tag) + listTag.get(i).toString(),
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                        }
                        for(int j = 0; j<listButtonTag.length; j++)
                        {
                            if(((Tag)listButtonTag[j].getTag())==null) {
                                listButtonTag[j].setTag(listTag.get(i));
                                listButtonTag[j].setVisibility(View.VISIBLE);
                                listButtonTag[j].setText(((Tag)listButtonTag[j].getTag()).toString());
                                return;
                            }
                        }
                        Toast.makeText(CreateContentActivity.this, getResources().getString(R.string.max_tag), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateContentActivity.this,GoogleMapActivity.class);
                intent.putExtra("location",location);
                intent.putExtra("status","create");
                startActivityForResult(intent,REQUEST_CODE_TAKE_LOCATION);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_CODE_TAKE_IMAGE && resultCode == RESULT_OK && data!=null) {
            hori.setVisibility(View.VISIBLE);
            listButtonDelete[imgCount].setVisibility(View.VISIBLE);
            Picasso.get().load(data.getData()).into(listImage[imgCount]);
            imgCount++;
        }
        if(requestCode==REQUEST_CODE_TAKE_LOCATION && resultCode == RESULT_OK && data!=null){
            location = (Location) data.getSerializableExtra("location");
            btnLocation.setVisibility(View.VISIBLE);
            btnLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    location=null;
                    btnLocation.setVisibility(View.GONE);
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Mapping(){
        content = new Content();
        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);
        btnUpload = (Button) findViewById(R.id.btn_upload);
        btnAddImg = (Button) findViewById(R.id.btn_add_img);
        btnAddTag = (Button) findViewById(R.id.btn_add_tag);
        btnAddLocation = (Button) findViewById(R.id.btn_add_location);
        prefer = getSharedPreferences("content",MODE_PRIVATE);
        editPrefer = prefer.edit();
        hori = (HorizontalScrollView) findViewById(R.id.hori);
        listImgContent =  new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        dataRef = FirebaseDatabase.getInstance().getReference();
        for(int i = 0; i < listButtonDelete.length ;i++)
        {
            String btnID = "btn_delete_" + (i + 1);
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            listButtonDelete[i] = (Button) findViewById(resID);
            listButtonDelete[i].setOnClickListener(this);
        }
        for(int i = 0; i < listImage.length ;i++)
        {
            String btnID = "img_" + (i+1);
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            listImage[i] = (ImageView) findViewById(resID);
        }
        for(int i =0; i<listButtonTag.length; i++)
        {
            String btnID = "btn_tag_" + (i + 1);
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            listButtonTag[i] = (Button) findViewById(resID);
            listButtonTag[i].setVisibility(View.GONE);
            listButtonTag[i].setOnClickListener(this);
        }
        btnLocation = (Button) findViewById(R.id.btn_location);
        btnLocation.setVisibility(View.GONE);

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
                if(!(editTitle.getText().toString().replace(" ","").equals("")
                        && editContent.getText().toString().replace(" ","").equals("")) && haveSave==false)
                    CreateDialog();
                else
                    CreateContentActivity.this.finish();
                break;
            case R.id.menu_save:
                editPrefer.putString("title",editTitle.getText().toString());
                editPrefer.putString("content",editContent.getText().toString());
                editPrefer.commit();
                haveSave=true;
                Toast.makeText(this, getResources().getString(R.string.post_saved), Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    //Sự kiến bấm nút back trên android
    @Override
    public void onBackPressed() {
        if(!(editTitle.getText().toString().replace(" ","").equals("")
                && editContent.getText().toString().replace(" ","").equals(""))  && haveSave==false)
            CreateDialog();
        else {
            CreateContentActivity.this.finish();
        }
//        super.onBackPressed();
    }

    //Tạo dialog thông báo
    private void CreateDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(CreateContentActivity.this);
        dialog.setTitle(getResources().getString(R.string.wait));
        dialog.setMessage(getResources().getString(R.string.un_save_exit));
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editPrefer.remove("title");
                editPrefer.remove("content");
                editPrefer.commit();
                CreateContentActivity.this.finish();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }


    private void loadButton() {
        hori.setVisibility(View.GONE);
        for(int i = 0; i<listButtonDelete.length; i++)
            listButtonDelete[i].setVisibility(View.GONE);
    }

    //Bắt sự kiện click
    @Override
    public void onClick(View view) {
        String btnDeleteID = view.getResources().getResourceEntryName(view.getId());
        int position = Integer.parseInt(btnDeleteID.substring(btnDeleteID.length() - 1, btnDeleteID.length()));
        if(((Button)view).getTag()==null) {
            position--;

            for (int i = position; i < imgCount - 1; i++) {
                listImage[i + 1].setDrawingCacheEnabled(true);
                listImage[i + 1].buildDrawingCache();
                Bitmap bit = ((BitmapDrawable) listImage[i + 1].getDrawable()).getBitmap();
                listImage[i].setImageBitmap(bit);
            }

            listButtonDelete[imgCount - 1].setVisibility(View.GONE);
            listImage[imgCount - 1].setImageBitmap(null);
            imgCount--;
            if (imgCount == 0)
                hori.setVisibility(View.GONE);
        }
        else
        {
            listButtonTag[position-1].setTag(null);
            listButtonTag[position-1].setVisibility(View.GONE);
        }

    }

    //Xử lí đăng bài viết
    private void uploadContent()
    {
        if(!(editContent.getText().toString().replace(" ","").equals("")
                || editTitle.getText().toString().replace(" ","").equals(""))
         && editContent.getText().toString().length()>= 500 && imgCount>=1) {
            Calendar calendar = Calendar.getInstance();
            String id = calendar.getTimeInMillis()+currentUser.getUid();
            Dialog dialogLoading = new Dialog(CreateContentActivity.this);
            dialogLoading.setContentView(R.layout.dialog_loading);
            dialogLoading.show();
            dialogLoading.setCancelable(false);

            ///Thêm thuộc tính cho content


            dataRef.child("Users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful()) {
                        User user = task.getResult().getValue(User.class);
                        Content content = new Content();
                        addImgForContent();
                        ArrayList<Tag> listTag = new ArrayList<>();
                        for(int i=0; i<listButtonTag.length; i++)
                            if(listButtonTag[i].getTag()!=null)
                            {
                                Tag tag = (Tag)listButtonTag[i].getTag();
                                listTag.add(tag);
                            }

                        //
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                content.setUser(user);
                                content.setListImage(listImgContent);
                                content.setImageContent(listImgContent.get(0));
                                content.setID(id);
                                content.setMainContent(editContent.getText().toString());
                                content.setDate(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                                content.setTitle(editTitle.getText().toString());
                                content.setListTag(listTag);
                                if(location!=null)
                                    content.setLocation(location);
                            }
                        },4000*imgCount);
                        //upload content lên database
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dataRef.child("Contents").child(id).setValue(content).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CreateContentActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                                            dialogLoading.dismiss();
                                            CreateContentActivity.this.finish();
                                        } else
                                            Toast.makeText(CreateContentActivity.this, getResources().getString(R.string.erro), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        },4000*imgCount+1000);

                    }
                }
            });


        }
        else if (imgCount<1){
            Toast.makeText(this, getResources().getString(R.string.min_img), Toast.LENGTH_SHORT).show();
        }
        else if(editContent.getText().toString().length()<500)
            Toast.makeText(this, getResources().getString(R.string.min_char), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, getResources().getString(R.string.invalid_post), Toast.LENGTH_SHORT).show();
    }
    //Nạp danh sách ảnh vào content
    private void addImgForContent()
    {
        int i =0;
        for(i=0; i<imgCount; i ++)
        {
            Calendar calendar = Calendar.getInstance();
            String name = currentUser.getUid() + calendar.getTimeInMillis();
            listImage[i].setDrawingCacheEnabled(true);
            listImage[i].buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) listImage[i].getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            StorageReference storage = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = storage.child("imageForContent/" + name + ".png");
            UploadTask uploadTask = imgRef.putBytes(data);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        return imgRef.getDownloadUrl();
                    }
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        ImageContent img = new ImageContent();
                        img.setName(name+".png");
                        img.setLink(task.getResult().toString());
                        listImgContent.add(img);
                        }
                    }
            });
        }
    }


}