package poker;

import javax.swing.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HeadsUpHand implements Serializable {

    private static final int NUMBER_OF_SHUFFLES = 3;
    public static final int PRE_FLOP = 9;
    public static final int FLOP = 10;
    public static final int TURN = 11;
    public static final int RIVER = 12;

    private int pot;
    private int allInCounter;
    private ArrayList<HeadsUpPlayer> activePlayers;
    private Card [] board;
    private int startingIndex;
    private HeadsUpPokerGame game;

    //This hand lives inside an array which a PokerGame object has access to.
    //This constructor will create a temporary array of players which will be
    //updated as players fold.
    //This Hand object will have its own deck and the board/burn cards
    //will be pre-loaded in this constructor. They will become visible accordingly
    //as action proceeds.
    public HeadsUpHand(HeadsUpPokerGame game) {
        this.game = game;
        //copy over players from the game class as a shallow copy
        //so we can remove players that folded
        activePlayers = new ArrayList<HeadsUpPlayer>(game.players);

        Deck deck = new Deck();
        for (int shuffleNum = 0; shuffleNum < NUMBER_OF_SHUFFLES; shuffleNum++){
            deck.shuffle();
        }

        //deal 2 cards to each player
        for (int i = 0; i < game.players.size(); i++) {
            game.players.get(i).receiveHand(deck.deal(2));
            //Reset all ins (must be done before hand starts - previously occured at end of hand)
            game.players.get(i).isAllIn = false;
        }

        //pre-load the board
        board = new Card[5];
        for (int i = 0; i < board.length; i++) {
            board[i] = deck.deal(1)[0];
        }

        //pre-burn 3 cards
        deck.deal(3);
        startPreFlop();

    }

    public void printBoard(int street, int handNum, HeadsUpPlayer player) {
        player.addChipsToMessage();
        switch(street) {
            case PRE_FLOP:
                player.addMessage("PREFLOP");
                break;
            case FLOP:
                player.addMessage("FLOP" + Arrays.toString(Arrays.copyOfRange(board, 0, 3)));
                break;
            case TURN:
                player.addMessage("TURN" + Arrays.toString(Arrays.copyOfRange(board, 0, 4)));
                break;
            case RIVER:
                player.addMessage("RIVER" + Arrays.toString(board));
                break;
        }
        player.addMessage("pot" + pot);

    }

    public Card[] getBoard(){
        return board;
    }

    public int getPot(){
        return pot;
    }

    public void addToPot(int amount) {

        pot += amount;

    }

    private void startStreet(int streetIn, int startingIndex) {

        //initialize currentBet according to street.
        int currentBet = (streetIn == PRE_FLOP) ? game.BIG_BLIND : 0;
        int previousBet;
        int tempActionCounter = startingIndex;

        //Reset how much each player has bet on a particular street, and set endAction to false
        for (int j = 0; j < game.players.size(); j++) {
            game.players.get(j).resetStreetMoney();
            game.players.get(j).setEndAction(false);

            if(!game.versusBot){
                game.players.get(j).addChipsToMessage();
                game.players.get(j).displayHoleCards();
            }
            else if(j==0){
                game.players.get(j).addChipsToMessage();
                game.players.get(j).displayHoleCards();
            }
        }

        //If preflop post SB/BB
        if(streetIn == PRE_FLOP){

            if(!game.versusBot){
                game.players.get(game.sbIndex).displayBlind("SB/D");
                game.players.get(game.bbIndex).displayBlind("BB");
            }
            else{
                if(game.sbIndex == 0){
                    game.players.get(0).displayBlind("SB/D");
                }
                else{
                    game.players.get(0).displayBlind("BB");
                }
            }
            //Post sb and set how much sb has bet
            game.players.get(game.sbIndex).postSB();
            game.players.get(game.sbIndex).setStreetMoney(HeadsUpPokerGame.SMALL_BLIND);

            //Post bb and set how much bb has bet
            game.players.get(game.bbIndex).postBB();
            game.players.get(game.bbIndex).setStreetMoney(HeadsUpPokerGame.BIG_BLIND);

            this.addToPot(HeadsUpPokerGame.SMALL_BLIND + HeadsUpPokerGame.BIG_BLIND);
        }

        game.players.get(startingIndex).setEndAction(true);

        //Game mechanics work by first displaying the correct spectator message, and then player action occurs and a bet size
        //is returned. Once a bet has been made, it's stored as playerbet, and previousbet is set as currentbet (starts as 0 or BB if pre)
        //If playerbet is larger than previousbet, we set currentbet to playerbet, and then set that player to where endAction is.
        //This allows us to differentiate calling (which doesn't change currentbet and endaction) and betting, as well as track/compare player bets
        //and what the previous bet size was. Any time a playerbet is larger than the currentbet, the FOR loop is restarted, so that everyone
        //gets another chnage to act. Once tempcounter reaches player behind endAction, after that player acts action ends and it breaks out of WHILE loop.
        //If a player bets all-in, and there is only one player left to act, he is allowed to act first
        //and then allinCounter is incremented so that all players are "considered" all in (hence the check of allinc = activeplayer.size(), not activeplayer.size()-1).
        while(true && game.gameIsLive) {
            for (int i = 0; i < game.players.size(); i++) {
                //If everyone is all in skip till handevaluator
                if(allInCounter == activePlayers.size()){
                    return;
                }
                if(!game.versusBot){
                    //Allow other player to spectate passively
                    if(tempActionCounter==0){
                        game.players.get(1).spectate(this, game, streetIn, "gen" + "Waiting for other player to act");
                        game.players.get(1).setTurnToAct(false);
                    }
                    else{
                        game.players.get(0).spectate(this, game, streetIn, "gen" + "Waiting for other player to act");
                        game.players.get(0).setTurnToAct(false);
                    }
                }
                else{
                    //Allow other player to spectate passively (bot is always second player added)
                    if(tempActionCounter==1){
                        game.players.get(0).spectate(this, game, streetIn, "gen" + "Waiting for other player to act");
                        game.players.get(0).setTurnToAct(false);
                    }
                }

                game.players.get(tempActionCounter).setTurnToAct(true);
                //Allow player to act
                int playerBet = game.players.get(tempActionCounter).act(currentBet, pot, this, game, streetIn);

                //Previous bet is used to gauge whether player action was a
                //bet, call, or check/fold (see end of method)
                previousBet = currentBet;

                //Constantly update who's folded
                removePlayers();
                //End while loop when only one player remains
                if (activePlayers.size() == 1) {
                    for(int l = 0; l < game.players.size(); l++){
                        //Find winning player (for loop allows for players to be removed)
                        if(game.players.get(l).getID() == activePlayers.get(0).getID()){
                            game.players.get(l).winPot(pot);
                            break;
                        }
                    }
                    return;
                }

                //Bet - sets where action ends
                if (playerBet > currentBet) {
                    //update the current bet if someone bet larger
                    currentBet = playerBet;
                    //Set whoever bets as end of action (sets everyone else to false)
                    for (int k = 0; k < game.players.size(); k++) {
                        if(k == tempActionCounter){
                            game.players.get(k).setEndAction(true);
                        }
                        else{
                            game.players.get(k).setEndAction(false);
                        }
                    }
                }
//                System.out.println(game.players.get(0).endAction + " " + game.players.get(0).getPlayerName() + " " + game.players.get(0).isAllIn);
//                System.out.println(game.players.get(1).endAction + " " + game.players.get(1).getPlayerName() + " " + game.players.get(1).isAllIn);

                //Cycle through whose turn it is (different from how many players)
                if (tempActionCounter == game.players.size() - 1) {
                    //If action is on the last player, check if next player (0) is last to act
                    if (game.players.get(0).endAction) {
                        if(allInCounter == activePlayers.size()-1){
                            //If everyone but one person is all in, let him act then ++ to allincounter
                            //Causes if statement at beginning of while loop to skip all later actions
                            allInCounter++;
                        }
                        //Break out of while loop
                        return;
                    }
                    //Otherwise move to next player (0)
                    tempActionCounter = 0;
                } else {
                    //If action is on the last player, check if next player is last to act
                    if (game.players.get(tempActionCounter + 1).endAction) {
                        if(allInCounter == activePlayers.size()-1){
                            //If everyone but one person is all in, let him act then ++ to allincounter
                            //Causes if statement at beginning of while loop to skip all later actions
                            allInCounter++;
                        }
                        //Break out of while loop
                        return;
                    }
                    //Otherwise move to next player
                    tempActionCounter++;
                }

                //If currentBet > previousBet then a bet has been made
                //(as opposed to a check/call), and the for loop is broken
                //Allows tempActioncounter to increase
                if(currentBet > previousBet){
                    break;
                }
            }
        }

    }


    private void startPreFlop() {

        startStreet(PRE_FLOP, game.actionIndex);
        //Only moves to flop if there are still people in pot
        if(activePlayers.size()!=1){
            startFlop();
        }
        //Output folded message
        else{
            foldedMessage(PRE_FLOP);
        }

    }

    private void startFlop(){
        //Flip order of who acts postflop (headsup)
        if(game.totalPlayers==2){
            if(game.actionIndex == 0){
                startingIndex = 1;
            }
            else{
                startingIndex = 0;
            }
        }
        //Player after deal index acts first
        else{
            if(game.dealerIndex == game.players.size() - 1){
                startingIndex = 0;
            }
            else{
                startingIndex = game.dealerIndex + 1;
            }
        }

        startStreet(FLOP, startingIndex);

        //Only moves to turn if there are still people in pot
        if(activePlayers.size()!=1){
            startTurn();
        }
        //Output folded message
        else{
            foldedMessage(FLOP);
        }
    }

    private void startTurn(){

        startStreet(TURN, startingIndex);

        //Only moves to river if there are still people in pot
        if(activePlayers.size()!=1){
            startRiver();
        }
        //Output folded message
        else{
            foldedMessage(TURN);
        }
    }

    private void startRiver(){

        startStreet(RIVER, startingIndex);
        if(activePlayers.size()!=1){
            //Used to ouput whoever is the winner to the screen
            String winnerMessage = "";
            boolean splitPot = false;
            //Only need to evaluate final hand strengths if by the time action
            //is over during the river, there is more than 1 player remaining
            if (activePlayers.size() > 1) {
                ArrayList<Integer> idList = HandEvaluator.evaluateHeadsUpHands(activePlayers, board);
                //Splitpot
                //If non-integer number player closest to dealer gets remainder (not yet implemented)
                if(idList.size()!=1){
                    pot = pot/(idList.size());
                    splitPot = true;
                }
                for(int l = 0; l < game.players.size(); l++){
                    for(int m = 0; m < idList.size(); m++){
                        //Find winning player (for loop allows for players to be removed)
                        if(game.players.get(l).getID() == idList.get(m)){
                            game.players.get(l).winPot(pot);
                            if(splitPot){
                                winnerMessage = "gen" + "Split pot";
                            }
                            else{
                                winnerMessage = "gen" +  game.players.get(l).getPlayerName() + " wins with " +
                                        Arrays.toString(game.players.get(l).getHoleCards());
                            }
                            break;
                        }
                    }
                }
            }
            game.players.get(0).spectate(this, game, RIVER, winnerMessage);
            if(!game.versusBot){
                game.players.get(1).spectate(this, game, RIVER, winnerMessage);
            }

            //Pause game for 5000ms (will either move on to next hand or end)
            try{
                Thread.sleep(5000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }

        //Only retain players that have >$0 and reset allin
        for(int i = 0; i < game.players.size(); i++){
            //Determine if player has been eliminated
            if(game.players.get(i).getMoney()==0){
                //For heads up once a player is removed game is over (or rebuys - requires implementation)
                game.players.get(0).endGameMessage();
                if(!game.versusBot){
                    game.players.get(1).endGameMessage();
                }
                //Pause game for 5000ms
                try{
                    Thread.sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                //Delete players from original group (used to end thread for headsup)
                game.players.remove(i);
            }
        }
    }

    private void removePlayers(){
        //Cycle through all activePlayers and remove them as they fold
        for(int k = 0; k < activePlayers.size(); k++){
            if (activePlayers.get(k).playerFolded()) {
                activePlayers.remove(activePlayers.get(k));
            }
        }

    }

    public void increaseAllInCounter(){
        allInCounter++;
    }

    public void foldedMessage(int streetIn){
        //Gets id of player who hasn't folded
        int winnerID = activePlayers.get(0).getID();
        int loserID;
        loserID = (winnerID==0)? 1 : 0;

        String winnerMessage = "gen" + game.players.get(loserID).getPlayerName() + " folded, "
                + game.players.get(winnerID).getPlayerName() + " is the winner";

        game.players.get(0).spectate(this, game, streetIn, winnerMessage);
        if(!game.versusBot){
            game.players.get(1).spectate(this, game, streetIn, winnerMessage);
        }
        //Pause game for 2500ms
        try{
            Thread.sleep(2500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
