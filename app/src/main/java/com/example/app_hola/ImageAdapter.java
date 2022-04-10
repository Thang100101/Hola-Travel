package com.example.app_hola;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_hola.ObjectForApp.ImageContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImageContent> imageContentList;
    private ViewPager2 viewPager2;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    public ImageAdapter(List<ImageContent> imageContentList, ViewPager2 viewPager2)
    {
        this.imageContentList=imageContentList;
        this.viewPager2=viewPager2;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scroll_imgae_container,parent,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        storageReference=FirebaseStorage.getInstance().getReference(imageContentList.get(position).getLink());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri){
                Picasso.get().load(uri).into(holder.imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
//        String name=imageContentList.get(position).getLink();
//        File file=new File(name);
//        Bitmap bitmap=BitmapFactory.decodeFile(file.getAbsolutePath());
//        holder.imageView.setImageBitmap(bitmap);
//            holder.imageView.setImageResource(imageContentList.get(position).getContentID());
    }
    @Override
    public int getItemCount() {
        return imageContentList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        RoundedImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.ScrollImageView);
        }
    }
}
