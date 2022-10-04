package com.example.simplecardgame;

import com.example.simplecardgame.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RenderState {

    public LinkedList<User> userLL;

    public String turnUID;

    public String thisPLayer;

    public int bid, pot;

    public String stat = "I Don't know Something stat";

    public Card t1, t2, t3;

    public Card p1, p2;

    public RenderState(Map<String, User> userMap, String thisPLayer, List<Card> tableCards){
        userLL = new LinkedList<>(userMap.values());
        turnUID = userLL.getFirst().getUID();
        while(!userLL.getFirst().getUID().equals(thisPLayer)){
            userLL.addLast(userLL.pop());
        }

        this.thisPLayer = thisPLayer;
        this.bid = 10;
        this.pot = 0;

        setTableCards(tableCards);
    }

    public void setTableCards(List<Card> tableCards){
        this.t1 = tableCards.get(0);
        this.t2 = tableCards.get(1);
        this.t3 = tableCards.get(2);
    }

    public void setPlayerCards(){

    }
}
