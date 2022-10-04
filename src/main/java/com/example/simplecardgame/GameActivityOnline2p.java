package com.example.simplecardgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simplecardgame.models.User;
import com.example.simplecardgame.viewmodels.GameViewModel;

import java.util.Random;

public class GameActivityOnline2p extends AppCompatActivity {

    TextView p1name, p2name, p1money, p2money, p2status, stat;
    TextView bid, pot;

    ImageView tableCard1, tableCard2, tableCard3;

    Button blind, raise, btn_pot, fold, showCards;

    ImageView p1turn, p2turn, p2Profile;

    ViewGroup progressView;

    GameViewModel gameViewModel;

    Resource resource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_players);

        String gameCode = getIntent().getExtras().getString("lobbyCode");

        initUI();
        initListeners();

        resource = new Resource(this);

        gameViewModel = ViewModelProviders.of(this).get(GameViewModel.class);
        gameViewModel.init(gameCode, this);

        gameViewModel.getRenderState().observe(this, new Observer<RenderState>() {
            @Override
            public void onChanged(RenderState rs) {
                setUserInfo(rs.userLL.getFirst(),1,rs.turnUID.equals(rs.thisPLayer));
                setUserInfo(rs.userLL.getLast(),2,!rs.turnUID.equals(rs.thisPLayer));
            }
        });

        gameViewModel.getShowBar().observe(this, new Observer<Boolean>() {
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
    }

    public void initUI(){
        p1name = findViewById(R.id.p1namep2);
        p2name = findViewById(R.id.p2namep2);

        p1money = findViewById(R.id.textView11);
        p2money = findViewById(R.id.p2monp2);

        p2status = findViewById(R.id.p2statp2);
        stat = findViewById(R.id.pep2);

        bid = findViewById(R.id.bidp2);
        pot = findViewById(R.id.potp2);

        tableCard1 = findViewById(R.id.tc1p2);
        tableCard2 = findViewById(R.id.tc2p2);
        tableCard3 = findViewById(R.id.tc3p2);

        blind = findViewById(R.id.bldp2);
        raise = findViewById(R.id.rasp2);
        btn_pot = findViewById(R.id.btn_potp2);
        fold = findViewById(R.id.fldp2);
        showCards = findViewById(R.id.button5);

        p1turn = findViewById(R.id.p1turnp2);
        p2turn = findViewById(R.id.p2turnp2);
        p2Profile = findViewById(R.id.imageView5);

        Random random = new Random();
        switch (random.nextInt(4)){
            case 0:
                p2Profile.setImageResource(R.drawable.blank_user_1);
                break;
            case 1:
                p2Profile.setImageResource(R.drawable.blank_user_2);
                break;
            case 2:
                p2Profile.setImageResource(R.drawable.blank_user_3);
                break;
            case 3:
                p2Profile.setImageResource(R.drawable.blank_user_4);
                break;
            default:
                p2Profile.setImageResource(R.drawable.cardback);
                break;
        }
    }

    public void initListeners(){
        blind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameActivityOnline2p.this, "THIS FEATURE HAS NOT BEEN IMPLEMENTED YET", Toast.LENGTH_SHORT).show();
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameViewModel.onRaisePressed();
            }
        });

        btn_pot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameViewModel.onPotPressed();
            }
        });

        fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameViewModel.onFoldPressed();
            }
        });

        showCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameViewModel.onShowPressed();
                Intent intent = new Intent(GameActivityOnline2p.this,ShowCardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("card1",001);
                bundle.putInt("card2",001);
                intent.putExtras(bundle);

                startActivityForResult(intent,0);
            }
        });
    }

    public void setUserInfo(User user, int id, boolean turn){
        switch (id){
            case 1:
                p1name.setText(user.getName());
                p1money.setText(user.getMoneyStr());
                if(turn){p1turn.setImageResource(android.R.drawable.presence_online);}
                else{p1turn.setImageResource(android.R.drawable.presence_invisible);}
                break;
            case 2:
                p2name.setText(user.getName());
                p2money.setText(user.getMoneyStr());
                if(turn){p2turn.setImageResource(android.R.drawable.presence_online);}
                else{p2turn.setImageResource(android.R.drawable.presence_invisible);}
                break;
            default:
                break;
        }
    }

    public void setTableInfo(int bid, int pot, Card t1, Card t2, Card t3, String stat){
        this.bid.setText(bid);
        this.pot.setText(pot);

        if(t1.visible){tableCard1.setImageBitmap(resource.getCard(t1));}
        if(t2.visible){tableCard2.setImageBitmap(resource.getCard(t2));}
        if(t3.visible){tableCard3.setImageBitmap(resource.getCard(t3));}

        this.stat.setText(stat);
    }

    private void doExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivityOnline2p.this);

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
