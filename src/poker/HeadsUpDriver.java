package poker;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class HeadsUpDriver extends Thread {

    private HeadsUpPlayer player1;
    private HeadsUpPlayer player2;
    public boolean versusBot;

    public HeadsUpDriver(HeadsUpPlayer player1, HeadsUpPlayer player2, boolean versusBot){
        this.player1 = player1;
        this.player2 = player2;
        this.versusBot = versusBot;
    }

    public void run(){
        try {
            //Initialize new poker game
            HeadsUpPokerGame pokerGame = new HeadsUpPokerGame(2, player1, player2, versusBot);

        }finally {
            System.out.println("Connection closed");
        }
    }

}
