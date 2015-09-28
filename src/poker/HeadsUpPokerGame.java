//The PokerGame class represents a session of poker


package poker;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.*;

public class HeadsUpPokerGame implements Serializable {

    //Constants
    public static final int STARTING_CASH = 200;
    public static final int BIG_BLIND = 2;
    public static final int SMALL_BLIND = 1;

    //variables
    public boolean gameIsLive;
    public int handNumber;
    public int totalPlayers;
    public int sbIndex;
    public int bbIndex;
    public int actionIndex;
    public int dealerIndex;
    public ArrayList<HeadsUpPlayer> players;
    public ArrayList<HeadsUpHand> hand;


    //Instantiate this when a fresh new game starts
    public HeadsUpPokerGame(int numOfPlayers, HeadsUpPlayer player1, HeadsUpPlayer player2) {
        //Initialize a blank array of hands
        hand = new ArrayList<HeadsUpHand>();
        //Total starting players
        totalPlayers = numOfPlayers;

        handNumber = 0;
        sbIndex = 0;
        bbIndex = 1;

        players = new ArrayList<HeadsUpPlayer>();

        players.add(player1);
        players.add(player2);

        //Small blind is first to act headsup
        actionIndex = 0;
        dealerIndex = 1;

        //Initially, game will always be live....
        //until only 1 player is remaining
        gameIsLive = true;

        player1.startGameMessage(player2.getPlayerName());
        player2.startGameMessage(player1.getPlayerName());

        try{
            Thread.sleep(5000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        startNewHand();

    }


    public void startNewHand() {

        while (gameIsLive) {
            hand.add(new HeadsUpHand(this));
            //End game
            if(players.size()==1){
                break;
            }
            handNumber++;
            //After every hand change indexes
            this.changeIndex();
        }

    }

    private void changeIndex(){
        //Change action index for next hand
        actionIndex = (actionIndex == players.size()-1)? 0 : actionIndex+1;

        //Change sb index for next hand
        sbIndex = (sbIndex == players.size()-1)? 0 : sbIndex+1;

        //Change bb index for next hand
        bbIndex = (bbIndex == players.size()-1)? 0 : bbIndex+1;

        //Change dealer index for next hand
        dealerIndex = (dealerIndex == players.size()-1)? 0 : dealerIndex+1;

    }

}
