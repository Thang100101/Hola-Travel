package com.example.app_hola;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_hola.ObjectForApp.ImageContent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReadContent extends AppCompatActivity {
    ActionBar actionBar;
    boolean search=false;
    private ViewPager2 viewPager2;
    public TextView txtTitle,txtUser,txtMainContent,txtDate;
    public ImageView imgContent;
    private List<ImageContent> imageContentList;
    private ImageAdapter adapter;
    String[] strings;
    String txtuser,txttitle,txtMaincontent,txtdate,txtLink;
    DatabaseReference reference;
    private Handler slideHandler=new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_content);

        viewPager2 = findViewById(R.id.viewPage2);
        txtTitle = findViewById(R.id.txtTitle);
        txtUser=findViewById(R.id.txtUser);
        txtMainContent=findViewById(R.id.txtMainContent);
        txtDate=findViewById(R.id.txtDate);
        imageContentList = new ArrayList<>();
        adapter = new ImageAdapter(imageContentList, viewPager2);
        viewPager2.setAdapter(adapter);
        getImageContent();
        getInfOfContent();
    }
    public void getInfOfContent(){
        Intent intent=getIntent();
        strings= intent.getStringArrayExtra(HomeActivity.EXTRA_MESSAGE);
        txttitle=strings[0];
        txtTitle.setText(txttitle);
        txtuser=strings[1];
        txtUser.setText(txtuser);
        txtLink=strings[2];
        txtdate=strings[3];
        txtDate.setText(txtdate);
        txtMaincontent=strings[4];
        txtMainContent.setText(txtMaincontent);


    }
    public void getImageContent(){
        reference= FirebaseDatabase.getInstance().getReference("ImageContents");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                imageContentList.add(snapshot.getValue(ImageContent.class));
                adapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_profile:
                Intent profile = new Intent(ReadContent.this, ProfileActivity.class);
                startActivity(profile);
                break;
            case R.id.menu_search:
                if(search==false) {
                    actionBar.setDisplayShowHomeEnabled(false);
                    actionBar.setDisplayUseLogoEnabled(false);
                    actionBar.setDisplayShowCustomEnabled(true);
                    actionBar.setCustomView(R.layout.search_view);
                    EditText editSearch = (EditText) findViewById(R.id.edit_search);
                    editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                            if(i == EditorInfo.IME_ACTION_SEARCH)
                            {
                                Toast.makeText(ReadContent.this, "Search!!", Toast.LENGTH_SHORT).show();
                            }
                            return false;
                        }
                    });
                    search = true;
                }
                else
                {
                    actionBar.setDisplayShowHomeEnabled(true);
                    actionBar.setDisplayUseLogoEnabled(true);
                    actionBar.setDisplayShowCustomEnabled(false);
                    search=false;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
