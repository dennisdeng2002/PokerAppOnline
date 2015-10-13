//The PokerGame class represents a session of poker


package poker;
import java.util.ArrayList;
import java.io.*;

public class HeadsUpPokerGame implements Serializable {

    //Constants
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
    public ArrayList<HeadsUpHand> hands;
    public boolean versusBot;


    //Instantiate this when a fresh new game starts
    public HeadsUpPokerGame(int numOfPlayers, HeadsUpPlayer player1, HeadsUpPlayer player2, boolean versusBot) {
        //Initialize a blank array of hands
        hands = new ArrayList<HeadsUpHand>();
        //Total starting players
        totalPlayers = numOfPlayers;
        this.versusBot = versusBot;
        System.out.println(this.versusBot);

        handNumber = 0;
        sbIndex = 0;
        bbIndex = 1;

        players = new ArrayList<HeadsUpPlayer>();

        player1.isPlaying = true;
        player1.setGame(this);
        player2.isPlaying = true;
        player2.setGame(this);

        players.add(player1);
        players.add(player2);

        //Small blind is first to act headsup
        actionIndex = 0;
        dealerIndex = 1;

        //Initially, game will always be live....
        //until only 1 player is remaining
        gameIsLive = true;

        player1.startGameMessage(player2.getPlayerName());
        if(!versusBot){
            player2.startGameMessage(player1.getPlayerName());
        }


        try{
            Thread.sleep(2500);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        startNewHand();

    }

    public int getBigBlind(){
        return BIG_BLIND;
    }

    public int getSmallBlind(){
        return SMALL_BLIND;
    }


    public void startNewHand() {
        while (gameIsLive) {
            players.get(0).initializePlayerDisplays(players.get(0), players.get(1));
            players.get(0).clearStreetInConsole();
            if(!versusBot){
                players.get(1).initializePlayerDisplays(players.get(1), players.get(0));
                players.get(1).clearStreetInConsole();
            }
            hands.add(new HeadsUpHand(this));

            //End game
            if(players.size()==1){
                break;
            }
            handNumber++;
            //After every hand change indexes
            this.changeIndex();
        }

    }

    public void endCurrentGame(){
        //gameIsLive controls both the main while loop in StartStreet() of the player class
        //and whether new hands are created above in StartNewHand()
        //When a player disconnects (or quits the game - not yet implemented)
        //this method is called, and thus gameIsLive, as well as isPlaying for each player
        //becomes false. Combining this with various breakpoints (based on isPlaying and occurs before actions)
        //allows for the game to end the moment a player disconnects
        gameIsLive = false;
        //Pause to allow disconnect message to register
        try{
            Thread.sleep(2500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        for(int i = 0; i < players.size(); i++){
            players.get(i).isPlaying = false;
            if(players.get(i).isConnected){
                PokerServer.lobby.addPlayerToQueue(players.get(i));
            }
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
