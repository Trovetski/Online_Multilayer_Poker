package com.example.simplecardgame.viewmodels;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplecardgame.models.User;
import com.example.simplecardgame.repos.LobbyFirebaseRepository;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyViewModel extends ViewModel {

    MutableLiveData<List<User>> mUserList;

    MutableLiveData<String> stat;

    MutableLiveData<Integer> nPlayers;

    MutableLiveData<Boolean> showBar;

    LobbyFirebaseRepository repository;

    private String lobbyCode;

    private Random random = new Random();

    private Context context;

    public LiveData<List<User>> getUsers(){
        return mUserList;
    }

    public LiveData<String> getStat(){
        return stat;
    }

    public LiveData<Integer> getNPlayers(){
        return nPlayers;
    }

    public LiveData<Boolean> getShowBar(){
        return showBar;
    }

    public void init(Context mContext){
        if(mUserList != null){
            Log.e("TAG","lobby code: "+lobbyCode);
            return;
        }
        repository = LobbyFirebaseRepository.getInstance();
        startUserEventListener();
        mUserList = new MutableLiveData<>();
        mUserList.setValue(repository.getUsers());

        stat = new MutableLiveData<>();
        stat.setValue("enter code or host a game");

        nPlayers = new MutableLiveData<>();
        nPlayers.setValue(0);

        showBar = new MutableLiveData<>();
        showBar.setValue(false);

        context = mContext;
        Log.e("TAG","lobby code: "+lobbyCode);
    }

    public void onHostClicked(){
        if(lobbyCode==null){
            lobbyCode = String.valueOf(random.nextInt(9000)+1000);
            repository.connectToLobby(lobbyCode);
        }else{
            Toast.makeText(context,"ALREADY IN A LOBBY",Toast.LENGTH_SHORT).show();
        }
    }

    public void onJoinClicked(String code){
        if(lobbyCode == null){
            lobbyCode = code;
            if(lobbyCode.length() !=4){
                Toast.makeText(context,"INVALID CODE",Toast.LENGTH_SHORT).show();
                lobbyCode=null;
                return;
            }
            repository.connectToLobby(code);
            showBar.setValue(true);
        }else{
            Toast.makeText(context,"ALREADY IN A LOBBY",Toast.LENGTH_SHORT).show();
        }
    }

    public void onReadyClicked(){
        if(lobbyCode != null){
            repository.setThisUserReady(lobbyCode);
        }else{
            Toast.makeText(context,"NOT IN A LOBBY",Toast.LENGTH_SHORT).show();
        }
    }

    public void startUserEventListener(){
        repository.setUserEventListener(new LobbyFirebaseRepository.UserEventListener() {

            @Override
            public void onUserAdded(List<User> userList, String name) {
                mUserList.setValue(userList);
                stat.setValue(name + " has joined");
            }

            @Override
            public void onUserRemoved(List<User> userList, String name) {
                mUserList.setValue(userList);
                stat.setValue(name + " has left");
            }

            @Override
            public void onUserReady(List<User> userList, String name) {
                mUserList.setValue(userList);
                stat.setValue(name + " is Ready");
            }

            @Override
            public void onLobbyFull() {
                showBar.setValue(false);
                stat.setValue("Requested lobby is full");
                Toast.makeText(context,"REQUESTED LOBBY IS FULL",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserConnected() {
                showBar.setValue(false);
                stat.setValue("connected to lobby with code: "+ lobbyCode);
            }

            @Override
            public void onAllUsersReady() {
                startEndSequence();
            }
        });
    }

    private void startEndSequence(){
        if(mUserList.getValue().size()<2){return;}
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for(int i=0;i<5;i++){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stat.postValue("Game starts in " + String.valueOf(5-i) + " seconds");
                }
                nPlayers.postValue(mUserList.getValue().size());
            }
        };
        timer.schedule(task,10);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e("TAG","cleared");
        repository.onDisconnect();
    }

    public String getLobbyCode() {
        return lobbyCode;
    }
}
