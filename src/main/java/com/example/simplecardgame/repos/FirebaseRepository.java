package com.example.simplecardgame.repos;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.simplecardgame.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepository {

    private static FirebaseRepository instance;

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private List<User> users = new ArrayList<>();

    private boolean allUsersReady = false;

    public static FirebaseRepository getInstance(){
        if(instance == null){
            instance = new FirebaseRepository();
            instance.root.child("users").child(instance.getUID().substring(0,6)).child("isOnline").setValue(true);
            instance.root.child("users").child(instance.getUID().substring(0,6)).child("isOnline").onDisconnect().setValue(false);
            instance.root.child("users").child(instance.getUID().substring(0,6)).child("isReady").onDisconnect().setValue(false);
        }
        return instance;
    }

    private String getUID(){
        return auth.getUid();
    }

    public List<User> getUsers(){
        return users;
    }

    public boolean createLobbyWithCode(String code){
        ArrayList<String> pList = new ArrayList<>();
        pList.add(getUID().substring(0,6));

        root.child("lobby").child(code).setValue(pList);
        root.child("game").child(code).setValue("");

        return true;
    }

    public void joinLobbyWithCode(String code){
        ArrayList<String> pList = new ArrayList<>();
        root.child("lobby").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    pList.addAll((ArrayList<String>)snapshot.getValue());
                    if(pList.size()>=4){
                        return;
                    }
                    pList.add(getUID().substring(0,6));
                    root.child("lobby").child(code).setValue(pList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setThisUserReady(){
        root.child("users").child(getUID().substring(0,6)).child("isReady").setValue(true);
    }

    public void setUserEventListener(FirebaseRepository.UserEventListener userEventListener, String code){

        root.child("lobby").child(code).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String UID = snapshot.getValue(String.class);

                User user = new User(UID,getUID().substring(0,6).equals(UID));
                users.add(user);
                userEventListener.onUserAdded(users, user.getName());

                Log.e("TAG", "prev child name" + previousChildName);

                root.child("users").child(UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean bool = false;
                        for(User user:users){
                            if(user.getUID().equals(snapshot.getKey())){
                                bool = true;
                                break;
                            }
                        }
                        if(!bool){
                            root.child("users").child(snapshot.getKey()).removeEventListener(this);
                        }
                        try{
                            user.setName(snapshot.child("name").getValue(String.class));
                            user.setMoney(snapshot.child("money").getValue(Integer.class));
                            user.setReady(snapshot.child("isReady").getValue(Boolean.class));

                            userEventListener.onUserAdded(users, user.getName());
                            if(users.size()<2){
                                return;
                            }
                            for(User user:users){
                                if(!user.isReady()){
                                    return;
                                }
                            }
                            userEventListener.onAllUsersReady();
                        }catch (NullPointerException e){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("USER CHANGED",snapshot.getValue(String.class) + " and child name " + previousChildName);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String UID = snapshot.getValue(String.class);
                String name = "user name";
                for(User user:users){
                    if(user.getUID().equals(UID)){
                        name = user.getName();
                        users.remove(user);
                        break;
                    }
                }

                userEventListener.onUserRemoved(users,name);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.e("USER MOVED",snapshot.getValue(String.class) + " and child name " + previousChildName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("USER ADDED","FUCKING HELL ITS AN ERROR");
            }
        });
    }

    public interface UserEventListener{

        void onUserAdded(List<User> userList,String name);

        void onUserRemoved(List<User> userList, String name);

        void onAllUsersReady();

    }
}