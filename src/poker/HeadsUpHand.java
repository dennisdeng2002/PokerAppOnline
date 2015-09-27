package poker;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class HeadsUpHand implements Serializable {

    private static final int NUMBER_OF_SHUFFLES = 3;
    private static final int PRE_FLOP = 9;
    private static final int FLOP = 10;
    private static final int TURN = 11;
    private static final int RIVER = 12;

    private int pot;
    private int allInCounter;
    private ArrayList<HeadsUpPlayer> activePlayers;
    private Card [] board;
    private int startingIndex;

    //This hand lives inside an array which a PokerGame object has access to.
    //This constructor will create a temporary array of players which will be
    //updated as players fold.
    //This Hand object will have its own deck and the board/burn cards
    //will be pre-loaded in this constructor. They will become visible accordingly
    //as action proceeds.
    public HeadsUpHand(HeadsUpPokerGame game) {
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
        }

        //pre-load the board
        board = new Card[5];
        for (int i = 0; i < board.length; i++) {
            board[i] = deck.deal(1)[0];
        }

        //pre-burn 3 cards
        deck.deal(3);

        startPreFlop(game);
    }

    public void printBoard(HeadsUpPokerGame game, int street, int handNum, HeadsUpPlayer player) {

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
        player.addMessage("Pot: $" + pot);

    }


    public void addToPot(int amount) {

        pot += amount;

    }

    private void startStreet(HeadsUpPokerGame game, int streetIn, int startingIndex) {

        //initialize currentBet according to street.
        int currentBet = (streetIn == PRE_FLOP) ? game.BIG_BLIND : 0;
        int previousBet;
        int tempActionCounter = startingIndex;

        //Reset how much each player has bet on a particular street, and set endAction to false
        for (int j = 0; j < game.players.size(); j++) {
            game.players.get(j).resetStreetMoney();
            game.players.get(j).setEndAction(false);
        }

        //If preflop post SB/BB
        if(streetIn == PRE_FLOP){
            //Post sb and set how much sb has bet
            game.players.get(game.sbIndex).postSB();
            game.players.get(game.sbIndex).setStreetMoney(HeadsUpPokerGame.SMALL_BLIND);

            //Post bb and set how much bb has bet
            game.players.get(game.bbIndex).postBB();
            game.players.get(game.bbIndex).setStreetMoney(HeadsUpPokerGame.BIG_BLIND);

            this.addToPot(HeadsUpPokerGame.SMALL_BLIND + HeadsUpPokerGame.BIG_BLIND);
        }

        game.players.get(startingIndex).setEndAction(true);

        while(true) {
            for (int i = 0; i < game.players.size(); i++) {
                //If everyone is all in skip till handevaluator
                if(allInCounter == activePlayers.size()){
                    return;
                }
                //Allow other player to spectate passively
                if(tempActionCounter==0){
                    game.players.get(1).spectate(this, game, streetIn, "Waiting for other player to act");
                    game.players.get(1).setTurnToAct(false);
                }
                else{
                    game.players.get(0).spectate(this, game, streetIn, "Waiting for other player to act");
                    game.players.get(1).setTurnToAct(false);
                }

                game.players.get(tempActionCounter).setTurnToAct(true);
                //Allow player to act
                int playerBet = game.players.get(tempActionCounter).act(currentBet, pot, this, game, streetIn);

                //Previous bet is used to gauge whether player action was a
                //bet, call, or check/fold (see end of method)
                previousBet = currentBet;

                //Constantly update who's folded
                removePlayers(game);
                //End while loop when only one player remains
                if (activePlayers.size() == 1) {
                    for(int l = 0; l < game.players.size(); l++){
                        //Find winning player (for loop allows for players to be removed)
                        if(game.players.get(l).id == activePlayers.get(0).id){
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


    private void startPreFlop(HeadsUpPokerGame game) {
        startStreet(game, PRE_FLOP, game.actionIndex);
        //Only moves to flop if there are still people in pot
        if(activePlayers.size()!=1){
            startFlop(game);
        }
        //Output folded message
        else{
            foldedMessage(game, PRE_FLOP);
        }

    }

    private void startFlop(HeadsUpPokerGame game){
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

        startStreet(game, FLOP, startingIndex);
        //Only moves to turn if there are still people in pot
        if(activePlayers.size()!=1){
            startTurn(game);
        }
        //Output folded message
        else{
            foldedMessage(game, FLOP);
        }

    }

    private void startTurn(HeadsUpPokerGame game){

        startStreet(game, TURN, startingIndex);
        //Only moves to river if there are still people in pot
        if(activePlayers.size()!=1){
            startRiver(game);
        }
        //Output folded message
        else{
            foldedMessage(game, TURN);
        }

    }

    private void startRiver(HeadsUpPokerGame game){

        startStreet(game, RIVER, startingIndex);
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
                    if(game.players.get(l).id == idList.get(m)){
                        game.players.get(l).winPot(pot);
                        if(splitPot){
                            winnerMessage = "Split pot";
                        }
                        else{
                            winnerMessage = game.players.get(l).getPlayerName() + " wins with " + Arrays.toString(game.players.get(l).getHoleCards());
                            System.out.println(winnerMessage);
                        }
                        break;
                    }
                }
            }
        }

        game.players.get(0).spectate(this, game, RIVER, winnerMessage);
        game.players.get(1).spectate(this, game, RIVER, winnerMessage);
        //Pause game for 5000ms (will either move on to next hand or end)
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        //Only retain players that have >$0 and reset allin
        for(int i = 0; i < game.players.size(); i++){
            //Reset all ins
            game.players.get(i).isAllIn = false;
            //Determine if player has been eliminated
            if(game.players.get(i).getMoney()==0){
                //For heads up once a player is removed game is over (or rebuys - requires implementation)
                game.players.get(0).endGameMessage();
                game.players.get(1).endGameMessage();
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

    private void removePlayers(HeadsUpPokerGame game){
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

    public void foldedMessage(HeadsUpPokerGame game, int streetIn){
        //Gets id of player who hasn't folded
        int winnerID = activePlayers.get(0).id;
        int loserID;
        if(winnerID == 0){
            loserID = 1;
        }
        else{
            loserID = 0;
        }
        String winnerMessage = game.players.get(loserID).getPlayerName() + " folded, "
                + game.players.get(winnerID).getPlayerName() + " is the winner";

        game.players.get(0).spectate(this, game, streetIn, winnerMessage);
        game.players.get(1).spectate(this, game, streetIn, winnerMessage);
        //Pause game for 5000ms
        try{
            Thread.sleep(5000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
