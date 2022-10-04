package com.example.simplecardgame;

import java.util.ArrayList;
import java.util.Random;


public class Card {
    //the cards include the standard deck
    //four suits, 13 cards, no jokers
    //hearts = 0 | diamond = 1 | club = 2 | spades = 3
    //faceVal go from 2-10 for all number cards
    //ace = 1 | jack = 11 | queen = 12 | king = 13
    int suite;
    int faceVal;

    boolean visible = false;

    public Card(int suite,int faceVal){
        this.faceVal = faceVal;
        this.suite = suite;
    }

    public Card(int data){
        this.faceVal = data%100;
        this.suite = data/100;
    }

    public static String getGameCards(){
        ArrayList<Integer> cards = new ArrayList<>();
        Random random = new Random();
        String out = "";

        int i=0;
        while(i<11){
            int card = random.nextInt(4)*100 + random.nextInt(13)+1;
            if(cards.contains(card)){
                continue;
            }
            out += card/100;
            out += (card%100)/10;
            out += card%10;
            cards.add(card);
            i++;
        }

        return out;
    }

    public static int getScore(Card[] c){
        int score = 0;

        /*
        changing the ace to 14
        and changing the suite to
        match the priority in case
        of a clash. why did I not
        do it at start? IDK, I'm
        stupid, don't ask questions.
         */
        for(Card card:c){
            if(card.faceVal==1){card.faceVal=14;}
            if(card.suite==0){card.suite=2;}
            else if(card.suite==1){card.suite=0;}
            else if(card.suite==2){card.suite=1;}
        }

        //sorting the array with magic
        for(int i=1;i<c.length;i++){
            Card current = c[i];
            int j = i - 1;
            while((j>=0)&&(c[j].faceVal>current.faceVal)){
                c[j+1] = c[j--];
                c[j+1] = current;
            }
        }

        //now we will check for each of the case

        /*
        Checking for Four of a kind
        like [A A A A 2]
         */
        boolean isFourOfKind = true;

        for(int i=1;i<3;i++){
            if(c[i].faceVal != c[i+1].faceVal){isFourOfKind=false;break;}
        }
        //check for the first and fifth card
        if(isFourOfKind){
            if((c[0].faceVal!=c[1].faceVal)&&
                    (c[4].faceVal!=c[3].faceVal)){isFourOfKind = false;}
        }

        if(isFourOfKind){
            score = 10000000;
            for(int i=0;i<c.length;i++){
                score += (long)(c[i].faceVal*Math.pow(13,i));
            }
            return score;
        }

        /*
        Checking for Flush and Combo
        and using them to check for

        1. Combo only
        2. Flush only
        3. Flush Combo
         */
        boolean isCombo = true;
        boolean isFlush = true;

        for(int i=0;i<c.length-1;i++){
            if(c[i].faceVal+1 != c[i+1].faceVal){isCombo=false;break;}
        }

        //special case if it is a [A 2 3 4 5]
        if((c[0].faceVal==2)&&
                (c[1].faceVal==3)&&
                (c[2].faceVal==4)&&
                (c[3].faceVal==5)&&
                (c[4].faceVal==14)){
            isCombo = true;
        }

        for(int i=0;i<c.length-1;i++){
            if(c[i].suite != c[i+1].suite){isFlush = false;break;}
        }

        if((isFlush)&&(!isCombo)){
            score = 2500000;
            for(int i=0;i<c.length;i++){
                score += (long)(c[i].faceVal*Math.pow(13,i));
            }
            return score;
        }

        if((isCombo)&&(!isFlush)){
            score = 1500000;
            for(int i=0;i<c.length;i++){
                score += (long)(c[i].faceVal*Math.pow(13,i));
            }
            return score;
        }

        if((isCombo)&&(isFlush)){
            score = 20000000;
            for(int i=0;i<c.length;i++){
                score += (long)(c[i].faceVal*Math.pow(13,i));
            }
            return score;
        }

        /*
        Checking three of a kind
        and two of a kind at same time
         */


        if((c[0].faceVal==c[1].faceVal)&&(c[1].faceVal==c[2].faceVal)&&(c[3].faceVal==c[4].faceVal)){
            score = 7000000;
            score += c[2].faceVal*2197;
            score += c[3].faceVal*13;

            return score;
        }
        if((c[0].faceVal==c[1].faceVal)&&(c[2].faceVal==c[3].faceVal)&&(c[3].faceVal==c[4].faceVal)){
            score = 7000000;
            score += c[2].faceVal*2197;
            score += c[0].faceVal*13;

            return score;
        }

        /*
        checking is there are three of a kind only
         */

        if((c[0].faceVal==c[1].faceVal)&&(c[1].faceVal==c[2].faceVal)){
            score = 600000;
            score += c[2].faceVal*2197;
            score += c[3].faceVal*13;
            score += c[4].faceVal*169;

            return score;
        }
        else if((c[1].faceVal==c[2].faceVal)&&(c[2].faceVal==c[3].faceVal)){
            score = 600000;
            score += c[2].faceVal*2197;
            score += c[0].faceVal*13;
            score += c[4].faceVal*169;

            return score;
        }
        else if((c[2].faceVal==c[3].faceVal)&&(c[3].faceVal==c[4].faceVal)){
            score = 600000;
            score += c[2].faceVal*2197;
            score += c[0].faceVal*13;
            score += c[1].faceVal*169;

            return score;
        }


        /*
        checking if there are two pairs
         */

        if((c[0].faceVal==c[1].faceVal)&&(c[3].faceVal==c[4].faceVal)){
            score = 500000;
            score += 2197*c[3].faceVal;
            score += 169*c[1].faceVal;
            score += 13*c[2].faceVal;

            return score;
        }
        else if((c[0].faceVal==c[1].faceVal)&&(c[2].faceVal==c[3].faceVal)){
            score = 500000;
            score += 2197*c[3].faceVal;
            score += 169*c[1].faceVal;
            score += 13*c[4].faceVal;

            return score;
        }
        else if((c[1].faceVal==c[2].faceVal)&&(c[3].faceVal==c[4].faceVal)){
            score = 500000;
            score += 2197*c[3].faceVal;
            score += 169*c[1].faceVal;
            score += 13*c[0].faceVal;

            return score;
        }

        /*
        checking if there is only one pair
         */

        if(c[0].faceVal==c[1].faceVal){
            score = 371293;
            score += c[0].faceVal*Math.pow(13,3);
            score += c[2].faceVal*Math.pow(13,0);
            score += c[3].faceVal*Math.pow(13,1);
            score += c[4].faceVal*Math.pow(13,2);
            return score;
        } else if(c[1].faceVal==c[2].faceVal){
            score = 371293;
            score += c[1].faceVal*Math.pow(13,3);
            score += c[0].faceVal*Math.pow(13,0);
            score += c[3].faceVal*Math.pow(13,1);
            score += c[4].faceVal*Math.pow(13,2);
            return score;
        } else if(c[2].faceVal==c[3].faceVal){
            score = 371293;
            score += c[2].faceVal*Math.pow(13,3);
            score += c[0].faceVal*Math.pow(13,0);
            score += c[1].faceVal*Math.pow(13,1);
            score += c[4].faceVal*Math.pow(13,2);
            return score;
        } else if(c[3].faceVal==c[4].faceVal){
            score = 371293;
            score += c[3].faceVal*Math.pow(13,3);
            score += c[0].faceVal*Math.pow(13,0);
            score += c[1].faceVal*Math.pow(13,1);
            score += c[2].faceVal*Math.pow(13,2);
            return score;
        }

        for(int i=0;i<c.length;i++){
            score += (long)(c[i].faceVal*Math.pow(13,i));
        }

        return score;
    }
}