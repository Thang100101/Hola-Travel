package com.example.app_hola;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ReadContent extends AppCompatActivity {
    ActionBar actionBar;
    private ViewPager2 viewPager2;
    public TextView txtTitle,txtUser,txtMainContent,txtDate, txtLikeCount, txtCmtCount;
    Button btnLike, btnCmt;
    public ImageView imgAvatar;
    private ArrayList<ImageContent> imageContentList;
    private ImageAdapter adapter;
    CommentAdapter adapterCMT;
    DatabaseReference dataRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    Content content;
    ArrayList<Like> listLike;
    ArrayList<Comment> listCmt;
    Like like;
    private Handler slideHandler=new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_content);

        Mapping();
        customActionBar();

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
                    dataRef.child("Likes").child(like.getID()).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            btnLike.setBackgroundResource(R.drawable.icon_unlike);
                            listLike.remove(like);
                            like=null;
                            txtLikeCount.setText(listLike.size()+"");
                        }
                    });
                }else
                {
                    like = new Like(content.getID()+currentUser.getUid(),currentUser.getUid(), content.getID());
                    dataRef.child("Likes").child(like.getID()).setValue(like);
                    btnLike.setBackgroundResource(R.drawable.icon_like);
                    listLike.add(like);
                    txtLikeCount.setText(listLike.size()+"");
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

    }

    ///Ánh xạ
    private void Mapping(){
        viewPager2 = findViewById(R.id.viewPage2);
        txtTitle = findViewById(R.id.txtTitle);
        txtUser=findViewById(R.id.txtUser);
        txtMainContent=findViewById(R.id.txtMainContent);
        txtDate=findViewById(R.id.txtDate);
        imageContentList = new ArrayList<>();
        Intent intent = getIntent();
        content = (Content) intent.getSerializableExtra("content");
        dataRef = FirebaseDatabase.getInstance().getReference();
        btnLike = (Button) findViewById(R.id.btn_like);
        btnCmt = (Button) findViewById(R.id.btn_cmt);
        txtLikeCount = (TextView) findViewById(R.id.txt_like_count);
        txtCmtCount = (TextView) findViewById(R.id.txt_cmt_count);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        listLike = new ArrayList<>();
        listCmt = new ArrayList<>();
        imgAvatar = (ImageView) findViewById(R.id.img_avatar);
        if(currentUser==null)
        {
            btnLike.setVisibility(View.GONE);
            btnCmt.setVisibility(View.GONE);
        }
    }

    ///Load thông tin bài viết
    public void getInfOfContent(){
        Dialog dialogLoading = new Dialog(ReadContent.this);
        dialogLoading.setContentView(R.layout.dialog_loading);
        dialogLoading.show();
        dialogLoading.setCancelable(false);
        listLike.clear();
        listCmt.clear();
        txtTitle.setText(content.getTitle());
        txtUser.setText(content.getUser().getName());
        Picasso.get().load(content.getUser().getAvatar()).into(imgAvatar);
        txtDate.setText(content.getDate());
        txtMainContent.setText(content.getMainContent());

        //Load lượt like, cmt
        Query queryLike = dataRef.child("Likes").orderByChild("contentID").equalTo(content.getID());
        queryLike.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue(Like.class)!=null)
                    listLike.add(snapshot.getValue(Like.class));
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
        Query queryCmt = dataRef.child("Comments").orderByChild("contentID").equalTo(content.getID());
        queryCmt.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.getValue(Comment.class)!=null)
                    listCmt.add(snapshot.getValue(Comment.class));
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                txtLikeCount.setText(listLike.size()+"");
                txtCmtCount.setText(listCmt.size()+"");
                for(int i = 0; i< listLike.size(); i++)
                {
                    if(listLike.get(i).getUserID() == currentUser.getUid())
                    {
                        btnLike.setBackgroundResource(R.drawable.icon_like);
                        like = listLike.get(i);
                    }
                }
                dialogLoading.dismiss();
            }
        },2000);


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
        Dialog dialog = new Dialog(ReadContent.this);
        dialog.setContentView(R.layout.dialog_list_comment);
        dialog.show();
        ListView listViewComment = (ListView) dialog.findViewById(R.id.list_cmt);
        EditText editCmt = (EditText) dialog.findViewById(R.id.edit_cmt);
        Button btnSend = (Button) dialog.findViewById(R.id.btn_send);
        ArrayList<Comment> listCmtDialog = listCmt;
        adapterCMT = new CommentAdapter(listCmtDialog,ReadContent.this);
        listViewComment.setAdapter(adapterCMT);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editCmt.getText().toString().equals("")) {
                    Calendar calendar = Calendar.getInstance();
                    Comment comment = new Comment();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    comment.setContentID(content.getID());
                    comment.setDate(simpleDateFormat.format(calendar.getTime()));
                    comment.setUserID(currentUser.getUid());
                    comment.setMainContent(editCmt.getText().toString());
                    listCmtDialog.add(comment);
                    adapterCMT.notifyDataSetChanged();
                    editCmt.setText("");
                    dataRef.child("Comments").push().setValue(comment);
                }
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                getInfOfContent();
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
        if(currentUser!=null)
            getMenuInflater().inflate(R.menu.menu_read,menu);
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
                Intent profile = new Intent(ReadContent.this, ProfileActivity.class);
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
        }
        return super.onOptionsItemSelected(item);
    }

    ///Xác nhận thoát app
    private void acceptOut()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Khoan đã!!");
        dialog.setMessage("Bạn có chắc chắn muốn thoát không?");
        dialog.setIcon(R.drawable.icon_crying);
        dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
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
        dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
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
}
