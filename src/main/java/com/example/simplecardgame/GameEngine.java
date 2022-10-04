package com.example.simplecardgame;
import com.example.simplecardgame.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GameEngine {

    private Stack<User> primaryStack;
    private Stack<User> secondaryStack;

    private List<Card> tableCards;

    private int bid;
    private int pot;

    private int cycle = 0;

    public User winner = null;

    public GameEngine(List<User> userList){
        primaryStack = new Stack<>();
        secondaryStack = new Stack<>();

        for(User user:userList){
            primaryStack.push(user);
        }

        bid = 10;
        pot = 0;

        initData("000000000");
    }

    public void initData(String data){
        tableCards = new ArrayList<>();
        for(int i=0;i<3;i++){
            Card card = new Card(Integer.valueOf(data.substring(3*i,3+3*i)));
            tableCards.add(card);
        }
    }

    public List<Card> getTableCards(){
        return tableCards;
    }

    public void removeUser(String UID){
        User user_left = null;
        for(User user:primaryStack){
            if(user.getUID().equals(UID)){
                user_left = user;
            }
        }
        primaryStack.remove(user_left);
        if(user_left == null){
            for(User user:secondaryStack){
                if(user.getUID().equals(UID)){
                    user_left = user;
                }
            }
            secondaryStack.remove(user_left);
        }
    }

    public int gameloop(String UID, int move){
        //move 0 -> fold
        //move 1 -> pot
        //move 2 -> raise
        if(!primaryStack.peek().getUID().equals(UID)){return 0;}
        if(move==3){move--;}
        switch (move){
            case 0:
                //if player folds just kick him out
                primaryStack.pop();
                break;
            case 1:
                //if he pots he pays
                pot += bid;
                primaryStack.peek().deductMoney(bid);
                secondaryStack.push(primaryStack.pop());
                break;
            case 2:
                //if he raises the bid doubles
                bid *= 2;
                pot += bid;
                primaryStack.peek().deductMoney(bid);
                secondaryStack.push(primaryStack.pop());
                break;
        }
        if(primaryStack.isEmpty()){
            while(!secondaryStack.isEmpty()){
                primaryStack.push(secondaryStack.pop());
            }
            tableCards.get(cycle).visible = true;
            cycle++;

            if(primaryStack.size()==1){
                primaryStack.peek().deductMoney(-1*pot);
                winner = primaryStack.pop();
                //notify that game has ended
                return 1;
            }

            if(cycle==3){
                //notify that game ended
                Map<Integer,User> scoreMap = new HashMap<>();
                int highscore = 0;
                for(User user:primaryStack){
                    int score = Card.getScore(new Card[]{tableCards.get(0), tableCards.get(1), tableCards.get(2), user.getCard1(),user.getCard2()});
                    scoreMap.put(score, user);
                    if(score>highscore){highscore = score;}
                }
                scoreMap.get(highscore).deductMoney(-1*pot);
                winner = scoreMap.get(highscore);
                //notify that scoreMap.get(high score) has won
                return 1;
            }
        }
        return 2;
    }
}
