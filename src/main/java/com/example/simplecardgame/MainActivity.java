package com.example.simplecardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    /*
    CARD SIZE = 225x315
     */

    Button btn_online, btn_wifi, btn_play;

    FirebaseAuth auth;


    //get UID
    String UID;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        btn_online = findViewById(R.id.btn_online);
        btn_wifi = findViewById(R.id.btn_wifi);
        btn_play = findViewById(R.id.btn_play);

        btn_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if already logged in
                if(auth.getCurrentUser()==null){
                    startActivityForResult((((AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig[] {
                                            (new AuthUI.IdpConfig.EmailBuilder()).build(), (new AuthUI.IdpConfig.GoogleBuilder())
                                            .build() })))).setIsSmartLockEnabled(false)).build(), 123);

                }else{

                    UID = auth.getUid().substring(0,6);
                    root.child("users").child(UID).child("isOnline").setValue(true);
                    root.child("users").child(UID).child("isOnline").onDisconnect().setValue(false);
                    Intent intent = new Intent(MainActivity.this,LobbyActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if (paramInt1 == 123) {
            Toast.makeText(this,"LOGIN SUCCESSFUL",Toast.LENGTH_SHORT).show();


            UID = auth.getUid().substring(0,6);

            //create user
            root.child("users").child(UID).child("name").setValue(auth.getCurrentUser().getDisplayName());
            root.child("users").child(UID).child("money").setValue(1000);
            root.child("users").child(UID).child("isOnline").setValue(true);
            root.child("users").child(UID).child("isOnline").onDisconnect().setValue(false);

            //go to lobby
            Intent intent = new Intent(MainActivity.this,LobbyActivity.class);
            startActivity(intent);
        }
    }
}
