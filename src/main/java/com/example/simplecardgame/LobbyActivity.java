package com.example.simplecardgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simplecardgame.adapters.LobbyRecyclerViewAdapter;
import com.example.simplecardgame.models.User;
import com.example.simplecardgame.viewmodels.LobbyViewModel;

import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    Button host, join, ready;
    EditText editText;
    TextView stat;

    RecyclerView recyclerView;

    LobbyViewModel lobbyViewModel;

    ViewGroup progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        lobbyViewModel = ViewModelProviders.of(this).get(LobbyViewModel.class);

        lobbyViewModel.init(this);

        lobbyViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                ((LobbyRecyclerViewAdapter)recyclerView.getAdapter()).updateUserList(users);
            }
        });

        lobbyViewModel.getStat().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                stat.setText(s);
            }
        });

        lobbyViewModel.getNPlayers().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer<2){return;}

                String code = lobbyViewModel.getLobbyCode();
                Bundle bundle = new Bundle();
                bundle.putString("lobbyCode",code);

                switch (integer){
                    case 2:
                        Intent intent = new Intent(LobbyActivity.this,GameActivityOnline2p.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        break;
                    case 3:
                        Intent intent1 = new Intent(LobbyActivity.this,GameActivityOnline3p.class);
                        intent1.putExtras(bundle);
                        startActivity(intent1);
                        finish();
                        break;
                    case 4:
                        Intent intent2 = new Intent(LobbyActivity.this,GameActivityOnline4p.class);
                        intent2.putExtras(bundle);
                        startActivity(intent2);
                        finish();
                        break;
                }
            }
        });

        lobbyViewModel.getShowBar().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    progressView = (ViewGroup) getLayoutInflater().inflate(R.layout.progress_view, null);
                    View v = findViewById(android.R.id.content).getRootView();
                    ViewGroup viewGroup = (ViewGroup) v;
                    viewGroup.addView(progressView);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else{
                    View v = findViewById(android.R.id.content).getRootView();
                    ViewGroup viewGroup = (ViewGroup) v;
                    viewGroup.removeView(progressView);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            }
        });

        initViewAndAdapter();

        initListeners();
    }

    public void initViewAndAdapter(){
        host = findViewById(R.id.btn_host);
        join = findViewById(R.id.btn_join);
        ready = findViewById(R.id.btn_ready);

        editText = findViewById(R.id.editText);

        stat = findViewById(R.id.lobby_stat);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new LobbyRecyclerViewAdapter(this,lobbyViewModel.getUsers().getValue()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    public void initListeners(){
        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lobbyViewModel.onHostClicked();
                editText.setEnabled(false);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editText.getText().toString();
                lobbyViewModel.onJoinClicked(code);
            }
        });

        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lobbyViewModel.onReadyClicked();
            }
        });
    }

    private void doExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LobbyActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialog.setNegativeButton("No", null);

        alertDialog.setMessage("Do you want to exit the lobby?");
        alertDialog.setTitle("SimpleCardGame");
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        doExit();
    }
}
