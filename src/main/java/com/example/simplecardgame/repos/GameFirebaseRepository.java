package com.example.simplecardgame.repos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.simplecardgame.Card;
import com.example.simplecardgame.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameFirebaseRepository {

    private static GameFirebaseRepository instance;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private GameEventListener gameEventListener;

    private Map<String, User> userMap;

    private String gameCode;

    public static GameFirebaseRepository getInstance(){
        if(instance == null){
            instance = new GameFirebaseRepository();
        }

        return instance;
    }

    private GameFirebaseRepository(){
        userMap = new HashMap<>();
    }

    public String getUID(){
        return auth.getUid().substring(0,6);
    }

    public Map<String, User> getUsers(){
        return userMap;
    }

    public void setGameEventListener(GameEventListener gme){
        this.gameEventListener = gme;
    }

    public void connectToGame(String gameCode){
        this.gameCode = gameCode;
        root.child("game").child(gameCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){return;}
                String gameData = snapshot.child("cards").getValue(String.class);

                int i=0;
                for(DataSnapshot child:snapshot.getChildren()){
                    String key = child.getKey();
                    if(key.equals("cards")){
                        continue;
                    }

                    User user = new User(key,key.equals(getUID()));
                    user.setName(snapshot.child(key).child("name").getValue(String.class));
                    user.setMoney(snapshot.child(key).child("money").getValue(Integer.class));
                    user.setIsConnected(true);

                    Card c1 = new Card(Integer.valueOf(gameData.substring(9+6*i,12+6*i)));
                    Card c2 = new Card(Integer.valueOf(gameData.substring(12+6*i,15+6*i)));

                    user.setCard1(c1);
                    user.setCard2(c2);

                    userMap.put(key,user);
                }
                gameEventListener.onUsersAdded(getUsers());
                gameEventListener.onGameDataReceived(gameData);
                startGameUpdates();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startGameUpdates(){
        root.child("game").child(gameCode).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("TAG",snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if(snapshot.getKey().equals("cards")){return;}
                //get the user from list
                User user = userMap.get(snapshot.getKey());

                //remove the user and notify the name
                userMap.remove(snapshot.getKey());
                gameEventListener.onUserRemoved(user.getUID());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onDisconnect(){
        instance = null;
        root.child("game").child(gameCode).child(getUID()).removeValue();
    }

    public interface GameEventListener{

        void onUsersAdded(Map<String, User> userMap);

        void onUserRemoved(String userUID);

        void onUserPlays(String UID,String move);

        void onGameDataReceived(String gameData);
    }
}
