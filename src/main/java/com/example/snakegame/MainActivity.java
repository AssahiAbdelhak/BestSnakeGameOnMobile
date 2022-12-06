package com.example.snakegame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    int nbApplesEaten = 0;
    int[][] coordinates = new int[200][2];
    RelativeLayout rl;
    View apple;
    ImageButton up,down,left,right;
    TextView score;
    int direction;
    Boolean startGame = false;
    List<View> list = new ArrayList<>();
    int xApple,yApple;
    Timer timer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = findViewById(R.id.score);
        rl = findViewById(R.id.relativeLayout);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        apple = findViewById(R.id.apple);
        apple.setVisibility(View.VISIBLE);

        coordinates[0][0] = 200;
        coordinates[0][1] = 200;

        startGame();

        left.setOnClickListener((v) -> moveToLeft());
        right.setOnClickListener((v) -> moveToRight());
        up.setOnClickListener((v) -> moveToUp());
        down.setOnClickListener((v) -> moveToDown());



    }

    private void createView(int i){
        score.setText("Score : "+i);
//        System.out.println(list.size());
        View view = new View(MainActivity.this);
        view.setLayoutParams(new ViewGroup.LayoutParams(50,50));
        view.setBackgroundColor(Color.YELLOW);
        if(i==0){
            coordinates[0][0] = 800;
            coordinates[0][1] = 200;
        }else{
            coordinates[i][0] = coordinates[i-1][0] - 20;
            coordinates[i][1] = coordinates[i-1][1];
        }
        view.setTranslationX(coordinates[i][0]);
        view.setTranslationY(coordinates[i][1]);
//        System.out.println("here");
        list.add(view);
//        System.out.println(list.size());
//        System.out.println("here");
        rl.addView(view);
    }

    private void startGame() {
        createView(nbApplesEaten);
        putRandomApple();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(startGame){
                    System.out.println(xApple+"//"+yApple);
                    System.out.println(coordinates[0][0]+"//"+coordinates[0][1]);
                    move();
                    if(coordinates[0][0]>=xApple&&coordinates[0][0]<=xApple+20&&coordinates[0][1]>=yApple&&coordinates[0][1]<=yApple+20){
                        nbApplesEaten++;
                        putRandomApple();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createView(nbApplesEaten);
                                moveOthers();
                            }
                        });

                    }else{
                        moveOthers();
                    }

//                    System.out.println(list.size());

                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,200);
    }

    private void putRandomApple() {
        Random rand = new Random();
        xApple = rand.nextInt(50)*20;
        yApple = rand.nextInt(50)*20;
        apple.setTranslationX(xApple);
        apple.setTranslationY(yApple);
    }

    private void move() {
        switch (direction){
            case 0:
                moveToUp();
                break;
            case 1:
                moveToDown();
                break;
            case 2:
                moveToRight();
                break;
            case 3:
                moveToLeft();
                break;
        }
        checkCollision();
        if(coordinates[0][0]>1040||coordinates[0][1]>1300||coordinates[0][0]<0||coordinates[0][1]<0||checkCollision()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timer.cancel();
                    gameOver();
                }
            });
        }
    }

    private boolean checkCollision() {
        for(int i = 1 ; i < nbApplesEaten;i++){
            if(coordinates[i][0]==coordinates[0][0]&&coordinates[i][1]==coordinates[0][1]){
                return true;
            }
        }
        return false;
    }

    //    780
//    20
    private void moveToUp() {
        startGame = true;
        if(direction==1)
            return;
        coordinates[0][1] -= 20;
        direction = 0;
        list.get(0).setTranslationY(coordinates[0][1]);
    }

    private void gameOver(){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("You Lose, Do you want to try Again");

        // Set Alert Title
        builder.setTitle("Game Over");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            dialog.cancel();
            list.clear();
            nbApplesEaten=0;
            startGame();
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            finish();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }

    private void moveToDown() {
        if(direction==0)
            return;
        startGame = true;
        coordinates[0][1] += 20;
        direction = 1;
        list.get(0).setTranslationY(coordinates[0][1]);
    }

    private void moveOthers() {
//        System.out.println("start");
//        System.out.println("test");
//        System.out.println(list.size());
        for(int i = 1 ; i<= nbApplesEaten ; i++){
//            System.out.println(coordinates[i][0]+" => "+coordinates[i][1]);
//            System.out.println("");
            list.get(i).setTranslationX(coordinates[i][0]);
            list.get(i).setTranslationY(coordinates[i][1]);
        }
//        System.out.println("end");
//        System.out.println(nbApplesEaten);
        for(int i = nbApplesEaten ; i > 0 ; i--){
//            System.out.println("translating");
//            System.out.println(coordinates[i][0]+"//"+coordinates[i][1]);
            coordinates[i][0] = coordinates[i-1][0];
            coordinates[i][1] = coordinates[i-1][1];
        }
    }

    private void moveToRight() {
        if(direction==3)
            return;
        startGame = true;
        coordinates[0][0] += 20;
        direction = 2;
        list.get(0).setTranslationX(coordinates[0][0]);
    }

    private void moveToLeft() {
        if(direction==2)
            return;
        startGame = true;
        coordinates[0][0] -= 20;
        direction = 3;
        list.get(0).setTranslationX(coordinates[0][0]);
    }

}