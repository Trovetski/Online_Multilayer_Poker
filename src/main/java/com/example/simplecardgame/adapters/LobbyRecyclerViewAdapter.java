package com.example.simplecardgame.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplecardgame.R;
import com.example.simplecardgame.models.User;

import java.util.List;

public class LobbyRecyclerViewAdapter extends RecyclerView.Adapter<LobbyRecyclerViewAdapter.ViewHolder> {

    //three array lists for name, money and ready
    private List<User> mUserList;

    //context for some reason
    private Context mContext;

    //this helps in animation somehow
    private int lastPosition = -1;

    public LobbyRecyclerViewAdapter(Context mContext,List<User> userList) {
        this.mUserList = userList;
        this.mContext = mContext;
    }

    public void updateUserList(List<User> userList){
        this.mUserList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create a holder that can "hold" the view
        //similar to setContentLayout in an activity
        //except we get a view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //set the text
        holder.user_name.setText(mUserList.get(position).getName());
        holder.user_money.setText(mUserList.get(position).getMoneyStr());

        if(mUserList.get(position).isReady()){
            holder.user_ready.setTextColor(Color.GREEN);
            holder.user_ready.setText("READY");
        }else{
            holder.user_ready.setTextColor(Color.RED);
            holder.user_ready.setText("NOT READY");
        }

        setAnimation(holder.itemView,position);
    }

    @Override
    public int getItemCount() {
        Log.e("TAG",mUserList.toString());
        return mUserList.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView user_name;
        TextView user_money;
        TextView user_ready;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            user_money = itemView.findViewById(R.id.user_money);
            user_ready = itemView.findViewById(R.id.user_ready);
        }
    }
}
