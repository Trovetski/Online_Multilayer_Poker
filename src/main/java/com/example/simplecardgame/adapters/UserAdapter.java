package com.example.simplecardgame.adapters;

import com.example.simplecardgame.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter {

    private List<User> mUserList;

    public UserAdapter(){
        mUserList = new ArrayList<>();
    }

    public void addUser(User user){}

    public void removeUser(User user){}

    public void removeUser(int index){}

    public User top(){
        return mUserList.get(0);
    }

    public User get(int i){
        if(i>=mUserList.size()){return null;}
        return mUserList.get(i);
    }
}
