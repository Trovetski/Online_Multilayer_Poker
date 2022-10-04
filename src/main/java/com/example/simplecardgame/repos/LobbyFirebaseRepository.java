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
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LobbyFirebaseRepository {

    private static LobbyFirebaseRepository instance;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private Map<String, User> userMap;

    private UserEventListener userEventListener;

    private String lobbyCode;

    public static LobbyFirebaseRepository getInstance(){
        if(instance==null){
            instance = new LobbyFirebaseRepository();
        }
        return instance;
    }

    private LobbyFirebaseRepository(){
        userMap = new HashMap<>();
    }

    public String getUID(){
        String output = auth.getUid().substring(0,6);
        if(output==null){
            return "123456";
        }
        return output;
    }

    public List<User> getUsers(){
        return new ArrayList<User>(userMap.values());
    }

    public User getUserByUID(String UID){
        final User user = new User(UID,UID.equals(getUID()));
        root.child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user.setName(snapshot.child("name").getValue(String.class));
                user.setMoney(snapshot.child("money").getValue(Integer.class));

                userEventListener.onUserAdded(getUsers(), user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return user;
    }

    public void setUserEventListener(UserEventListener use){
        userEventListener = use;
    }

    public void setThisUserReady(String lobbyCode){
        root.child("lobby").child(lobbyCode).child(getUID()).setValue(true);
    }

    public void connectToLobby(String lobbyCode){
        root.child("lobby").child(lobbyCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.getChildrenCount()>3){
                    userEventListener.onLobbyFull();
                    return;
                }

                if(!snapshot.exists()){
                    root.child("game").child(lobbyCode).child("cards").setValue(Card.getGameCards());
                    root.child("game").child(lobbyCode).child("cards").onDisconnect().removeValue();
                }

                root.child("lobby").child(lobbyCode).child(getUID()).setValue(false);
                root.child("lobby").child(lobbyCode).child(getUID()).onDisconnect().removeValue();

                startLobbyUpdates(lobbyCode);
                userEventListener.onUserConnected();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void startLobbyUpdates(String lobbyCode){
        this.lobbyCode = lobbyCode;
        root.child("lobby").child(lobbyCode).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String UID = snapshot.getKey();

                //if a user is already in the list don't add them
                if(userMap.keySet().contains(UID)){return;}

                //get the user reference
                User user = getUserByUID(UID);
                user.setReady(snapshot.getValue(Boolean.class));

                //add to the user map and notify
                userMap.put(UID,user);
                userEventListener.onUserAdded(getUsers(),user.getName());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //check if ready and set the value
                boolean isReady = snapshot.getValue(Boolean.class);
                userMap.get(snapshot.getKey()).setReady(isReady);
                userEventListener.onUserReady(getUsers(),userMap.get(snapshot.getKey()).getName());

                for(User user: userMap.values()){
                    if(!user.isReady()||userMap.values().size()<2){
                        return;
                    }
                }

                userEventListener.onAllUsersReady();
                root.child("game").child(lobbyCode).child(getUID()).child("name").setValue("something");
                root.child("game").child(lobbyCode).child(getUID()).child("money").setValue(1000);
                root.child("game").child(lobbyCode).child(getUID()).onDisconnect().removeValue();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                //get the user from list
                User user = userMap.get(snapshot.getKey());

                //remove the user and notify the name
                userMap.remove(snapshot.getKey());
                userEventListener.onUserRemoved(getUsers(),user.getName());
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
        root.child("lobby").child(lobbyCode).child(getUID()).removeValue();
    }

    public interface UserEventListener{

        void onUserAdded(List<User> userList, String name);

        void onUserRemoved(List<User> userList, String name);

        void onUserReady(List<User> userList, String name);

        void onLobbyFull();

        void onUserConnected();

        void onAllUsersReady();

    }
}
