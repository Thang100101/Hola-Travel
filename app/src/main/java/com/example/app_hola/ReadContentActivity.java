package com.example.app_hola;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_hola.ObjectForApp.Comment;
import com.example.app_hola.ObjectForApp.Content;
import com.example.app_hola.ObjectForApp.ImageContent;
import com.example.app_hola.ObjectForApp.Like;
import com.example.app_hola.ObjectForApp.Location;
import com.example.app_hola.ObjectForApp.NotificationContent;
import com.example.app_hola.ObjectForApp.Tag;
import com.example.app_hola.ObjectForApp.User;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReadContentActivity extends AppCompatActivity implements View.OnClickListener{
    ActionBar actionBar;
    private ViewPager2 viewPager2;
    public TextView txtTitle,txtUser,txtMainContent,txtDate, txtLikeCount, txtCmtCount;
    Button btnLike, btnCmt, btnLocation;
    Button [] listButtonTag = new Button[3];
    public ImageView imgAvatar;
    private ArrayList<ImageContent> imageContentList;
    private ImageAdapter adapter;
    CommentAdapter adapterCMT;
    DatabaseReference dataRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Content content;
    ArrayList<Comment> listCmt;
    ArrayList<Like> listLike;
    ArrayList<Tag> listTag;
    Like like;
    Query queryLike,queryCMT;
    User user;
    Location location;
    private Handler slideHandler=new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_content);

        Mapping();
        customActionBar();
        resetContent();
        adapter = new ImageAdapter(content.getListImage(), viewPager2);
        viewPager2.setAdapter(adapter);
        getImageContent();
        getInfOfContent();
        //Sự kiện click nút like
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(like!=null)
                {
                    listLike.remove(like);
                    like=null;
                    content.setListLike(listLike);
                    dataRef.child("Contents").child(content.getID()).setValue(content).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            btnLike.setBackgroundResource(R.drawable.icon_unlike);
                        }
                    });
                    if(listLike.size()>0) {
                        //Create Noti
                        Calendar calendar = Calendar.getInstance();
                        NotificationContent noti = new NotificationContent(content.getUser().getUserID(), content.getID(),
                                user.getName(), "like", listLike.size(), false);
                        noti.setImg(content.getImageContent());
                        noti.setID(content.getID() + "like");
                        noti.setMainContent("like_type_1");
                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss");
                        noti.setDate(simpleDateFormat1.format(calendar.getTime()));
                        dataRef.child("Notifications").child(content.getID() + "like").setValue(noti);
                        dataRef.child("Users").child(content.getUser().getUserID()).child("haveNotification").setValue(true);
                    }
                    else if(listLike.size()==0)
                        dataRef.child("Notifications").child(content.getID() + "like").removeValue();

                }else
                {
                    like = new Like(content.getID()+currentUser.getUid(),currentUser.getUid(), content.getID());
                    listLike.add(like);
                    content.setListLike(listLike);
                    dataRef.child("Contents").child(content.getID()).setValue(content).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            btnLike.setBackgroundResource(R.drawable.icon_like);
                        }
                    });
                    //Create Noti
                    Calendar calendar = Calendar.getInstance();
                    NotificationContent noti = new NotificationContent(content.getUser().getUserID(),content.getID(),
                            user.getName(),"like", listLike.size(), false);
                    noti.setImg(content.getImageContent());
                    noti.setID(content.getID() + "like");
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss");
                    noti.setDate(simpleDateFormat1.format(calendar.getTime()));
                    noti.setMainContent("like_type_2");
                    dataRef.child("Notifications").child(content.getID() + "like").setValue(noti);
                    dataRef.child("Users").child(content.getUser().getUserID()).child("haveNotification").setValue(true);
                }
            }
        });

        //Sự kiện click nút comment
        btnCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogCmt();
            }
        });

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadContentActivity.this, YourContentActivity.class);
                intent.putExtra("userID", content.getUser().getUserID());
                intent.putExtra("userName", content.getUser().getName());
                startActivity(intent);
                finish();
            }
        });

        txtUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadContentActivity.this, YourContentActivity.class);
                intent.putExtra("userID", content.getUser().getUserID());
                intent.putExtra("userName", content.getUser().getName());
                startActivity(intent);
                finish();
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadContentActivity.this, GoogleMapActivity.class);
                intent.putExtra("location",location);
                intent.putExtra("status","read");
                startActivity(intent);
            }
        });

    }

    ///Ánh xạ
    private void Mapping(){
        viewPager2 = findViewById(R.id.viewPage2);
        txtTitle = findViewById(R.id.txtTitle);
        txtUser=findViewById(R.id.txtUser);
        txtMainContent=findViewById(R.id.txtMainContent);
        txtDate=findViewById(R.id.txtDate);
        btnLike = (Button) findViewById(R.id.btn_like);
        btnCmt = (Button) findViewById(R.id.btn_cmt);
        txtLikeCount = (TextView) findViewById(R.id.txt_like_count);
        txtCmtCount = (TextView) findViewById(R.id.txt_cmt_count);
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);

        Intent intent = getIntent();
        content = (Content) intent.getSerializableExtra("content");
        dataRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        listCmt = new ArrayList<>();
        listLike = content.getListLike();
        listTag = content.getListTag();
        imageContentList = new ArrayList<>();
        adapterCMT = new CommentAdapter(listCmt, ReadContentActivity.this);
        queryCMT = dataRef.child("Comments").orderByChild("contentID").equalTo(content.getID());
//        queryLike = dataRef.child("Contents").orderByChild("Likes/").equalTo(content.getID());
        if(currentUser==null)
        {
            btnLike.setVisibility(View.GONE);
            btnCmt.setVisibility(View.GONE);
        }
        for(int i =0; i<listTag.size(); i++)
        {
            String btnID = "btn_tag_" + (i + 1);
            listTag.get(i).setContext(ReadContentActivity.this);
            int resID = getResources().getIdentifier(btnID, "id", getPackageName());
            listButtonTag[i] = (Button) findViewById(resID);
            listButtonTag[i].setVisibility(View.VISIBLE);
            listButtonTag[i].setOnClickListener(this);
            listButtonTag[i].setTag(listTag.get(i));
            listButtonTag[i].setText(listTag.get(i).toString());
        }
        btnLocation = (Button) findViewById(R.id.btn_location);
        if(currentUser!=null)
            dataRef.child("Users").child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    user= task.getResult().getValue(User.class);
                }
            });
    }

    ///Load thông tin bài viết
    public void getInfOfContent(){
        Dialog dialogLoading = new Dialog(ReadContentActivity.this);
        dialogLoading.setContentView(R.layout.dialog_loading);
        dialogLoading.show();
        dialogLoading.setCancelable(false);
        dataRef.child("Contents").child(content.getID()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                content = task.getResult().getValue(Content.class);
                listCmt.clear();
                txtTitle.setText(content.getTitle());
                User user = content.getUser();
                if(user.getName().equals("Chưa có tên"))
                    txtUser.setText(getResources().getString(R.string.name_unknow));
                else
                    txtUser.setText(user.getName());
                Picasso.get().load(user.getAvatar().getLink()).into(imgAvatar);
                txtDate.setText(content.getDate());
                txtMainContent.setText(content.getMainContent());
                if(content.getLocation()!=null) {
                    location = content.getLocation();
                    btnLocation.setVisibility(View.VISIBLE);
                }
                loadLikeCmt();
                //Load lượt like, cmt
                loadCountLC();
                dialogLoading.dismiss();
            }
        });


        //Load lượt like, cmt
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2000);
    }
    //Load lượt like, cmt
    private void loadCountLC()
    {
        txtLikeCount.setText(listLike.size()+"");
        txtCmtCount.setText(listCmt.size()+"");
        if(currentUser!=null)
            for(int i = 0; i< listLike.size(); i++)
            {
                if(listLike.get(i).getUserID().equals(currentUser.getUid()))
                {
                    btnLike.setBackgroundResource(R.drawable.icon_like);
                    like = listLike.get(i);
                }
            }
    }

    //Load danh sách like, cmt
    private void loadLikeCmt()
    {
        queryCMT.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue(Comment.class)!=null) {
                    listCmt.add(snapshot.getValue(Comment.class));
                    adapterCMT.notifyDataSetChanged();
                    loadCountLC();
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

    //Realtime
    private void resetContent()
    {
        dataRef.child("Contents").child(content.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                content = snapshot.getValue(Content.class);
                listLike = content.getListLike();
                loadCountLC();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Lấy danh sách ảnh
    public void getImageContent(){

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer transformer=new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r=1-Math.abs(position);
                page.setScaleY(0.85f+r*0.14f);
            }
        });
        viewPager2.setPageTransformer(transformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(sliderRunnable);
                slideHandler.postDelayed(sliderRunnable,2000);
            }
        });
    }

    //Tạo dialog list comment
    private void createDialogCmt()
    {
        Dialog dialog = new Dialog(ReadContentActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_comment);
        InitDialog(dialog);
        dialog.show();
        ListView listViewComment = (ListView) dialog.findViewById(R.id.list_cmt);
        EditText editCmt = (EditText) dialog.findViewById(R.id.edit_cmt);
        Button btnSend = (Button) dialog.findViewById(R.id.btn_send);
        Button btnBack = (Button) dialog.findViewById(R.id.btn_back);
        listViewComment.setAdapter(adapterCMT);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editCmt.getText().toString().equals("")) {
                    //upLoad comment
                    Calendar calendar = Calendar.getInstance();
                    Comment comment = new Comment();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    comment.setContentID(content.getID());
                    comment.setDate(simpleDateFormat.format(calendar.getTime()));
                    comment.setUserID(currentUser.getUid());
                    comment.setMainContent(editCmt.getText().toString());
                    editCmt.setText("");
                    dataRef.child("Comments").push().setValue(comment);

                    //Create Noti
                    NotificationContent noti = new NotificationContent(content.getUser().getUserID(),content.getID(),
                            user.getName(),"comment", listCmt.size(), false);
                    noti.setImg(content.getImageContent());
                    noti.setID(content.getID() + "comment");
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss");
                    noti.setDate(simpleDateFormat1.format(calendar.getTime()));
                    noti.setMainContent("comment");
                    dataRef.child("Notifications").child(content.getID() + "comment").setValue(noti);

                    dataRef.child("Users").child(content.getUser().getUserID()).child("haveNotification").setValue(true);

                }
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                loadCountLC();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private Runnable sliderRunnable=new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(sliderRunnable,2000);
    }

    //Tạo thanh menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentUser!=null) {
            getMenuInflater().inflate(R.menu.menu_read, menu);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(user.isHaveNotification())
                        menu.getItem(1).setIcon(R.drawable.icon_bell_noti);
                }
            },2000);
            dataRef.child("Users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    user = snapshot.getValue(User.class);
                    if(user.isHaveNotification()){
                        menu.getItem(1).setIcon(R.drawable.icon_bell_noti);
                    }
                    else
                        menu.getItem(1).setIcon(R.drawable.icon_bell);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
            getMenuInflater().inflate(R.menu.menu_read_without_signin,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Bắt sự kiện chọn item của menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_profile:
                Intent profile = new Intent(ReadContentActivity.this, ProfileActivity.class);
                startActivity(profile);
                break;
            case R.id.menu_search:
                break;

            case R.id.menu_exit:
                acceptOut();
                break;
            case R.id.menu_signin:
                Intent intent = new Intent(getApplicationContext(), WellcomeActivity.class);
                intent.putExtra("signin",true);
                startActivity(intent);
                finish();
                break;
            case R.id.menu_signout:
                Intent intent2 = new Intent(getApplicationContext(), WellcomeActivity.class);
                intent2.putExtra("signout",true);
                mAuth.signOut();
                startActivity(intent2);
                finish();
                break;
            case R.id.menu_another:
                Intent intent3 = new Intent(getApplicationContext(), WellcomeActivity.class);
                intent3.putExtra("another",true);
                mAuth.signOut();
                startActivity(intent3);
                finish();

            case android.R.id.home:
                finish();
                break;
            case R.id.menu_noti:
                dataRef.child("Users").child(user.getUserID()).child("haveNotification").setValue(false);
                item.setIcon(R.drawable.icon_bell);
                Intent intent4 = new Intent(getApplicationContext(),NotificationActivity.class);
                startActivity(intent4);
        }
        return super.onOptionsItemSelected(item);
    }

    ///Xác nhận thoát app
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

    //
    private void customActionBar()
    {
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getDrawable(R.drawable.background_actionbar));
        actionBar.setTitle(content.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        Tag tag = (Tag) ((Button)view).getTag();
        Intent intent = new Intent(ReadContentActivity.this,HomeActivity.class);
        intent.putExtra("tagid",tag.getID());
        startActivity(intent);
    }
    private void InitDialog(Dialog dialog) {
        //Init Dialog
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
    }
}
