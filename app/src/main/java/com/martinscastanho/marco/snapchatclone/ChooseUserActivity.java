package com.martinscastanho.marco.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUserActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> userEmails;
    ArrayList<String> userIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        userEmails = new ArrayList<>();
        userIds = new ArrayList<>();

        listView = findViewById(R.id.chooseUserListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userEmails);
        listView.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                assert dataSnapshot.child("email").getValue() != null;
                userEmails.add((String) dataSnapshot.child("email").getValue());
                userIds.add(dataSnapshot.getKey());
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> snap = new HashMap<>();
                assert FirebaseAuth.getInstance().getCurrentUser() != null;
                Intent intent = getIntent();

                snap.put("from", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                snap.put("imageId", intent.getStringExtra("imageId"));
                snap.put("imageUrl", intent.getStringExtra("imageUrl"));
                snap.put("message", intent.getStringExtra("message"));

                FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(userIds.get(position))
                        .child("snaps")
                        .push() // creates new entry with some uuid
                        .setValue(snap);

                // go back to Snaps Activity
                Intent returnToSnapsIntent = new Intent(getApplicationContext(), SnapsActivity.class);
                returnToSnapsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(returnToSnapsIntent);
            }
        });
    }
}
