package com.martinscastanho.marco.snapchatclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> emails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);

        firebaseAuth = FirebaseAuth.getInstance();

        emails = new ArrayList<>();

        listView = findViewById(R.id.snapsListView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emails);
        listView.setAdapter(arrayAdapter);

        assert firebaseAuth.getCurrentUser() !=  null;
        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("snaps")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                assert dataSnapshot.child("from").getValue() != null;
                emails.add((String) dataSnapshot.child("from").getValue());
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.snaps_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.create_snap){
            Intent createSnapIntent = new Intent(getApplicationContext(), CreateSnapActivity.class);
            startActivity(createSnapIntent);
        }
        else if(item.getItemId() == R.id.log_out){
            firebaseAuth.signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        firebaseAuth.signOut();
    }
}
