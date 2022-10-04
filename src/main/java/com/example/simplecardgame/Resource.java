package com.example.simplecardgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Resource {

    public Bitmap cards;
    Context context;

    public Resource(Context context){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        cards = BitmapFactory.decodeResource(context.getResources(), R.drawable.cards1);
        this.context = context;
    }

    public Bitmap getCard(Card card){
        Bitmap cardBitmap = Bitmap.createBitmap(cards);
        int bitmapHeight = cardBitmap.getHeight();
        int bitmapWidth = cardBitmap.getWidth();

        bitmapHeight = bitmapHeight/4;
        bitmapWidth = bitmapWidth/13;

        switch (card.suite){
            case 0:
                cardBitmap = Bitmap.createBitmap(cardBitmap,(card.faceVal-1)*bitmapWidth,0,bitmapWidth,bitmapHeight);
                break;
            case 1:
                cardBitmap = Bitmap.createBitmap(cardBitmap,(card.faceVal-1)*bitmapWidth,bitmapHeight*2,bitmapWidth,bitmapHeight);
                break;
            case 2:
                cardBitmap = Bitmap.createBitmap(cardBitmap,(card.faceVal-1)*bitmapWidth,bitmapHeight*3,bitmapWidth,bitmapHeight);
                break;
            case 3:
                cardBitmap = Bitmap.createBitmap(cardBitmap,(card.faceVal-1)*bitmapWidth,bitmapHeight,bitmapWidth,bitmapHeight);
                break;
        }
        cardBitmap = Bitmap.createScaledBitmap(cardBitmap,66,92,false);
        return cardBitmap;
    }

    public Drawable getBack(){
        Drawable back;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            back = context.getDrawable(R.drawable.cardback);
        }else{
            back = context.getResources().getDrawable(R.drawable.cardback);
        }
        return back;
    }
}