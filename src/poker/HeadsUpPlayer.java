package poker;

import org.eclipse.jetty.websocket.api.Session;

import java.io.*;
import java.util.*;

public class HeadsUpPlayer extends Thread implements Serializable{

    protected String name;
    protected int money;
    protected Card [] holeCards;
    protected boolean folded;
    protected int streetMoney;
    public boolean endAction;
    public boolean isAllIn;
    //ID is essentially their seat number
    protected int id;
    protected int otherPlayerID;
    protected Session session;
    protected ArrayList<String> messages;
    protected String recievedMessage;
    protected String response;
    protected boolean turnToAct;
    public boolean isPlaying;
    public boolean isConnected;
    public boolean versusBot;
    protected HeadsUpPokerGame game;

    //Default Constructor
    public HeadsUpPlayer(){}

    public HeadsUpPlayer(String name, int money, int id, Session session, int otherPlayerID) {

        this.name = name;
        this.money = money;
        this.id = id;
        holeCards = new Card[2];
        folded = false;
        isAllIn = false;
        this.session = session;
        messages = new ArrayList<String>();
        turnToAct = true;
        this.otherPlayerID = otherPlayerID;
        isConnected = true;
        isPlaying = false;

    }

    //Runs only once when player connects (might be unnecessary to start Thread just to add name)
    public void run(){
        if(this.name==null){
            addMessage("Enter player name");
            send();
            clearMessages();
            this.name = receive();
        }
        waitingMessage();
    }

    public String getPlayerName(){
        return name;
    }

    public Card[] getHoleCards(){
        return holeCards;
    }

    public int getMoney() {

        return money;

    }

    public void receiveHand(Card[] hand) {

        holeCards[0] = hand[0];
        holeCards[1] = hand[1];
        folded = false;

    }

    public void postBB() {

        if (money == 1) {
            money -= 1;
        } else {
            money -= HeadsUpPokerGame.BIG_BLIND;
        }

    }

    public void postSB() {

        money -= HeadsUpPokerGame.SMALL_BLIND;

    }


    protected void spendMoney(int amount) {

        money -= amount;

    }

    public boolean playerFolded() {

        return this.folded;

    }

    protected void fold() {

        folded = true;

    }

    public int getID(){
        return this.id;
    }

    public int getOtherPlayerID(){
        return this.otherPlayerID;
    }

    public void setID(int id){
        this.id = id;
    }

    public void setOtherPlayerID(int id){
        this.otherPlayerID = id;
    }

    public void setGame(HeadsUpPokerGame game){
        this.game = game;
    }

    public HeadsUpPokerGame getGame(){
        return this.game;
    }

    //This method gets called from a method in the Hand object (startStreet())
    //In that method, each player is looped through to act();
    public synchronized int act(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn) {

        boolean isCorrect = false;
        String action;
        int betSize = minimumBet;
        if(!this.folded && !this.isAllIn) {
            while(!isCorrect && isPlaying){
                //Output board
                hand.printBoard(streetIn, game.handNumber, this);
                // Output hand and player stats
                addMessage(this.toString(game));
                addMessage("Bet/Check/Call/Fold");
                send();
                clearMessages();
                if(!isPlaying){
                    break;
                }
                action = receive();
                // Checks what action user inputs
                if(action.equalsIgnoreCase("Bet")) {
                    while(true){
                        //Output board
                        hand.printBoard(streetIn, game.handNumber, this);
                        // Output hand and player stats
                        addMessage(this.toString(game));
                        try{
                            addMessage("Size");
                            send();
                            clearMessages();
                            //Requires exception?
                            try{
                                if(!isPlaying){
                                    break;
                                }
                                betSize = Integer.parseInt(receive());
                            }catch(NumberFormatException e){
                                e.printStackTrace();
                            }

                            //For headsup this is fine since there isn't a scenario where your betsize is
                            //larger than how much the other player has and also less that what you have
                            if (betSize > game.players.get(otherPlayerID).getMoney()){
                                //Only allow player to bet how much other play has
                                betSize = game.players.get(otherPlayerID).getMoney();
                                //Any additional bet is total (don't have to remember previous bet)
                                this.spendMoney(betSize - streetMoney);
                                hand.addToPot(betSize-streetMoney);
                                //Total streetmoney becomes betsize
                                streetMoney = betSize;
                                isCorrect = true;
                                if(!versusBot){
                                    game.players.get(otherPlayerID).addMessage(name + " puts you all in");
                                }
                                //Increase all in counter so that
                                //when other player calls further actions are skipped
                                hand.increaseAllInCounter();
                            }
                            else if (money <= betSize) {
                                //If betsize is greater than money, player is all in
                                betSize = money;
                                this.spendMoney(betSize);
                                hand.addToPot(betSize);
                                streetMoney = betSize;
                                isAllIn = true;
                                hand.increaseAllInCounter();
                                isCorrect = true;
                                if(!versusBot){
                                    game.players.get(otherPlayerID).addMessage(name + " is all in");
                                }
                            } else if(betSize < 2*minimumBet || betSize == 0) {
                                addMessage("Illegal bet size");
                                //Reset betsize to what was previously bet (miniumum bet)
                                betSize = minimumBet;
                            } else {
                                //Any additional bet is total (don't have to remember previous bet)
                                this.spendMoney(betSize - streetMoney);
                                hand.addToPot(betSize-streetMoney);
                                //Total streetmoney becomes betsize
                                streetMoney = betSize;
                                isCorrect = true;
                                if(!versusBot){
                                    game.players.get(otherPlayerID).addMessage(name + " bet " + betSize);
                                }
                            }
                            break;
                        }
                        catch(InputMismatchException e) {
                            addMessage("Not a number");
                            continue;
                        }
                    }
                }
                //we need a way for BB to check b/c minbet is still > 0 for him
                else if(action.equalsIgnoreCase("Check")) {
                    if(minimumBet - streetMoney > 0){
                        addMessage("You cannot check when the pot is raised");
                    } else{
                        isCorrect = true;
                        betSize = 0;
                        if(!versusBot){
                            game.players.get(otherPlayerID).addMessage(name + " checked");
                        }
                    }
                }
                else if(action.equalsIgnoreCase("Call")) {
                    if(minimumBet == 0 || minimumBet - streetMoney == 0){
                        addMessage("You cannot call when there is no bet");
                    }
                    else if(money <= minimumBet){
                        betSize = money;
                        this.spendMoney(betSize);
                        hand.addToPot(betSize);
                        isAllIn = true;
                        //If you call all in then hand must end
                        //Add 1 to AllInCounter in order to skip further actions == players.size() in Hand class
                        //Betting all in is different since other player still has to act
                        hand.increaseAllInCounter();
                        isCorrect = true;
                        if(streetIn!=12){
                            if(!versusBot){
                                game.players.get(otherPlayerID).addMessage(name + " is all in");
                            }
                        }
                    }
                    else{
                        //Otherwise calling subtracts the betsize minus the money you already put in the point
                        //Allows the returned bet-size to stay the same, thus indicating a call in the Hand class
                        this.spendMoney(minimumBet - streetMoney);
                        hand.addToPot(minimumBet - streetMoney);
                        isCorrect = true;
                        betSize = minimumBet;
                        if(!versusBot){
                            game.players.get(otherPlayerID).addMessage(name + " called " + betSize);
                        }
                    }
                }
                else if(action.equalsIgnoreCase("Fold")) {
                    this.fold();
                    isCorrect = true;
                    betSize = 0;
                }
                else {
                    addMessage("Incorrect action, please try again");
                }
            }
        }
        else{
            betSize = 0;
        }
        return betSize;
    }

    public synchronized void spectate(HeadsUpHand hand, HeadsUpPokerGame game, 
    									int streetIn, String message) {
        clearMessages();
        addMessage(message);
        //Output board
        hand.printBoard(streetIn, game.handNumber, this);
        // Output hand and player stats
        addMessage(this.toString(game));
        send();
        clearMessages();

    }

        public void winPot(int amount) {

        money += amount;

    }

    public void setStreetMoney(int amount){
        streetMoney += amount;
    }

    public void resetStreetMoney(){
        streetMoney = 0;
    }

    public void setEndAction(boolean bool){
        endAction = bool;
    }

    public void endGameMessage(){
        addMessage("Game is over");
        send();
        clearMessages();
    }

    public void addMessage(String message){
        //Add message to queue
        messages.add(message);
    }

    private void clearMessages(){
        //Clear messages (usually called after send)
        messages.clear();
        //Used in the client side javascript to determine when to clear previous messages
        messages.add("new");
    }

    private void send(){
        try {
            for(int i = 0; i < messages.size(); i++){
                //Send all messages one by one
                session.getRemote().sendString(messages.get(i));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private String receive(){
        //Response is the message actually used for input by this player
        //receivedMessage is the message that is received when client presses enter (see method receiveMessage())
        //This method allows for the player class to continuously check for messages until the incoming message is received
        response = null;
        try {
            while(response == null){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(recievedMessage!=null){
                    response = recievedMessage;
                }
            }
        }catch(NoSuchElementException e){
            e.printStackTrace();
        }
        receiveMessage(null);
        return response;
    }

    public void receiveMessage(String message){
        if(turnToAct){
            recievedMessage = message;
        }
        else{
            recievedMessage = null;
        }
    }

    public void startGameMessage(String opponent){
        addMessage("Game has started, your opponent is " + opponent);
        send();
        clearMessages();
    }

    public void waitingMessage(){
        addMessage("Waiting for player to connect");
        send();
        clearMessages();
    }

    public void sendOpponentMessage(String message){
        game.players.get(otherPlayerID).addMessage(message);
        game.players.get(otherPlayerID).send();
        game.players.get(otherPlayerID).clearMessages();
    }

    public void setTurnToAct(boolean bool){
        turnToAct = bool;
    }

    public String toString(HeadsUpPokerGame game) {
        String retVal = "";
        String position;
        if(id==game.bbIndex){
            position = "BB";
        }
        else{
            position = "SB/D";
        }
        retVal += name + ": " + "$" + money + "--" +
                holeCards[0] + holeCards[1] + "--" + position;
        return retVal;

    }

}
