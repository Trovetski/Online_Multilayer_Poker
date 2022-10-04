package com.example.simplecardgame.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.simplecardgame.GameEngine;
import com.example.simplecardgame.RenderState;
import com.example.simplecardgame.models.User;
import com.example.simplecardgame.repos.GameFirebaseRepository;

import java.util.ArrayList;
import java.util.Map;

public class GameViewModel extends ViewModel {

    private MutableLiveData<RenderState> renderState;

    private GameEngine gameEngine;

    private MutableLiveData<Boolean> showBar;

    GameFirebaseRepository repository;

    private boolean isPlayingBlind = true;

    private String gameCode;

    private Context context;

    public void init(String gameCode, Context context){
        if(renderState != null){
            return;
        }
        this.gameCode = gameCode;

        renderState = new MutableLiveData<>();

        repository = GameFirebaseRepository.getInstance();
        startGameEventListener();
        repository.connectToGame(gameCode);

        this.context = context;

        showBar = new MutableLiveData<>();
        showBar.setValue(false);
    }

    public LiveData<RenderState> getRenderState(){
        return renderState;
    }

    public LiveData<Boolean> getShowBar(){
        return showBar;
    }


    public void onPotPressed(){}

    public void onFoldPressed(){}

    public void onRaisePressed(){}

    public void onBlindPressed(){
        if(!isPlayingBlind){
            Toast.makeText(context, "CANNOT PLAY BLIND", Toast.LENGTH_SHORT).show();
        }
    }

    public void onShowPressed(){
        isPlayingBlind = false;
    }

    public void startGameEventListener(){
        repository.setGameEventListener(new GameFirebaseRepository.GameEventListener() {
            @Override
            public void onUsersAdded(Map<String, User> userMap) {
                gameEngine = new GameEngine(new ArrayList<>(userMap.values()));
                showBar.setValue(false);
                renderState.setValue(new RenderState(userMap,repository.getUID(),gameEngine.getTableCards()));
            }

            @Override
            public void onUserRemoved(String userUID) {
                gameEngine.removeUser(userUID);
                RenderState rs = renderState.getValue();
                for(User user:rs.userLL){
                    if(user.getUID().equals(userUID)){
                        user.setIsConnected(false);
                    }
                }
                renderState.setValue(rs);
            }

            @Override
            public void onUserPlays(String UID, String moveRaw) {
                int move = Integer.valueOf(moveRaw.substring(0,1));
                int result = gameEngine.gameloop(UID, move);

                switch (result){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onGameDataReceived(String gameData) {
                gameEngine.initData(gameData);
                RenderState rs = renderState.getValue();
                rs.setTableCards(gameEngine.getTableCards());
                renderState.setValue(rs);
            }
        });
    }
}
