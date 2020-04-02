package com.martinscastanho.marco.snapchatclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewSnapActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    ImageView snapImageView;
    TextView messageTextView;

    String snapId;
    String imageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        snapImageView = findViewById(R.id.snapImageView);
        messageTextView = findViewById(R.id.messageTextView);
        messageTextView.setText(intent.getStringExtra("message"));
        downloadImage(intent.getStringExtra("imageUrl"));

        snapId = intent.getStringExtra("snapId");
        imageId = intent.getStringExtra("imageId");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // delete the snap
        // database reference on user and the actual image from storage
        assert firebaseAuth.getCurrentUser() !=  null;
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("snaps")
                .child(snapId)
                .removeValue();

        FirebaseStorage.getInstance().getReference()
                .child("images")
                .child(imageId)
                .delete();
    }

    public void downloadImage(String url){
        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;
        try {
            myImage = task.execute(url).get();
            snapImageView.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
