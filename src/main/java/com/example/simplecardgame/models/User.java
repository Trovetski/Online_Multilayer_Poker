package com.example.simplecardgame.models;

import com.example.simplecardgame.Card;

public class User {

    //User credentials
    private String UID;
    private String name = "user_name";

    //is the user ready to enter the game
    private boolean isReady = false;

    //is the user operating the device
    private boolean isThisUser;

    //is the user connected
    private boolean isConnected = false;

    //user money
    private int money = 1000;

    //user cards
    private Card card1, card2;

    public User(String UID, boolean isThisUser) {
        this.UID = UID;
        this.isThisUser = isThisUser;
    }

    public User(){
        this.UID = "test12";
        this.isThisUser = false;
    }

    public void setName(String name) { this.name = name; }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getUID() {
        return UID;
    }

    public String getName() {
        return name;
    }

    public boolean isReady() {
        return isReady;
    }

    public boolean isThisUser() {
        return isThisUser;
    }

    public int getMoney() {
        return money;
    }

    public String getMoneyStr(){
        return String.valueOf(money);
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean connected) {
        isConnected = connected;
    }

    public void deductMoney(int amount){
        money -= amount;
    }
}