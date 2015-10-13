package poker;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

//PokerBot works by calculating its equity and determining the best action based on its EV
public class PokerBot extends HeadsUpPlayer{

    private Deck deck;
    private Card[] opponentCards = new Card[2];
    private Card[] board = new Card[5];
    private ArrayList<Integer> winners;
    //Scores are used to tally up games won in order to calculate equity
    private double playerScore;
    private double opponentScore;
    private double equity;
    private int betSize;
    private RangeMatrix matrix;
    Card[] cardsInRange;
    private int limit;
    private double botAggression;
    private double opponentAggression;
    private double percentPot;
    private double range;
    private int minimumBet;
    private int pot;
    private HeadsUpHand hand;
    private HeadsUpPokerGame game;
    private int streetIn;

    public PokerBot(String name, int money, int id, int otherPlayerID){
        this.name = name;
        this.money = money;
        this.id = id;
        holeCards = new Card[2];
        folded = false;
        isAllIn = false;
        turnToAct = true;
        this.otherPlayerID = otherPlayerID;
        matrix = new RangeMatrix();
        //Aggression stats are currently set to default (0-1.0 range)
        botAggression = 0.5;
        opponentAggression = 0.5;
    }

    public synchronized int act(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn){
        this.minimumBet = minimumBet;
        this.pot = pot;
        this.hand = hand;
        this.game = game;
        this.streetIn = streetIn;
        //PREFLOP = 9, FLOP = 10, TURN = 11, RIVER = 12
        switch(streetIn){
            case 9:
                betSize = actionPreFlop();
                break;
            case 10:
                betSize = actionFlop();
                break;
            case 11:
                betSize = actionTurn();
                break;
            case 12:
                betSize = actionRiver();
                break;
        }
        System.out.println(Arrays.toString(holeCards));
        System.out.println(betSize);

        return betSize;
    }

    public int actionPreFlop(){
        equity = calculateEquity();

        //Raise
        if(equity >= 0.5 || equity <= 0.2){
            if(ThreadLocalRandom.current().nextDouble(0.0, 1.0) <= 0.7){
                betSize = bet();
            }
            else{
                //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
                if(minimumBet == game.getBigBlind() && game.bbIndex == id){
                    betSize = check();
                }
                else{
                    betSize = foldBot();
                }
            }
        }
        //Check/Call
        else if (equity < 0.50 && equity > 0.2){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == game.getBigBlind() && game.bbIndex == id){
                betSize = check();
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call();
            }
        }
        //Fold
        else{
            if(minimumBet == game.getBigBlind() && game.bbIndex == id){
                betSize = check();
            }
            else{
                betSize = foldBot();
            }
        }
        return betSize;
    }

    public int actionFlop(){
        equity = calculateEquity();

        //Raise
        if(equity >= 0.5 || equity <= 0.2){
            if(ThreadLocalRandom.current().nextDouble(0.0, 1.0) <= 0.7){
                betSize = bet();
            }
            else{
                //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
                if(minimumBet == 0){
                    betSize = check();
                }
                else{
                    betSize = foldBot();
                }
            }
        }
        //Check/Call
        else if (equity < 0.50 && equity > 0.2){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == 0){
                betSize = check();
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call();
            }
        }
        //Fold
        else{
            if(minimumBet == 0){
                betSize = check();

            }
            else{
                betSize = foldBot();

            }
        }
        return betSize;
    }

    public int actionTurn(){
        equity = calculateEquity();

        //Raise
        if(equity >= 0.5 || equity <= 0.2){
            if(ThreadLocalRandom.current().nextDouble(0.0, 1.0) <= 0.7){
                betSize = bet();
            }
            else{
                //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
                if(minimumBet == 0){
                    betSize = check();
                }
                else{
                    betSize = foldBot();
                }
            }
        }
        //Check/Call
        else if (equity < 0.50 && equity > 0.2){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == 0){
                betSize = check();
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call();
            }
        }
        //Fold
        else{
            if(minimumBet == 0){
                betSize = check();
            }
            else{
                betSize = foldBot();
            }
        }
        return betSize;
    }

    public int actionRiver(){
        equity = calculateEquity();

        //Raise
        if(equity >= 0.5 || equity <= 0.2){
            if(ThreadLocalRandom.current().nextDouble(0.0, 1.0) <= 0.7){
                betSize = bet();
            }
            else{
                //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
                if(minimumBet == 0){
                    betSize = check();
                }
                else{
                    betSize = foldBot();
                }
            }
        }
        //Check/Call
        else if (equity < 0.50 && equity > 0.2){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == 0){
                betSize = check();
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call();
            }
        }
        //Fold
        else{
            if(minimumBet == 0){
                betSize = check();
            }
            else{
                betSize = foldBot();
            }
        }
        return betSize;
    }

    public int bet(){
        int betSize;
        //Randomly generate betsize
        if(minimumBet == 0){
            betSize = (int)(ThreadLocalRandom.current().nextDouble(.5, 1.25) * hand.getPot());
        }
        else{
            betSize = (int)(ThreadLocalRandom.current().nextDouble(1.25, 2.5) * minimumBet);
        }

        if(betSize >= money){
            betSize = money;
            game.players.get(otherPlayerID).addMessage(name + " is all in");
        }
        else if(betSize >= game.players.get(otherPlayerID).money){
            betSize = game.players.get(otherPlayerID).money;
            game.players.get(otherPlayerID).addMessage(name + " puts you all in");
        }
        else{
            game.players.get(otherPlayerID).addMessage(this.name + " bet " + betSize);
        }
        hand.addToPot(betSize);
        spendMoney(betSize);
        return betSize;
    }

    public int call(){
        hand.addToPot(minimumBet - streetMoney);
        spendMoney(minimumBet - streetMoney);
        if(streetIn!=12){
            game.players.get(otherPlayerID).addMessage(this.name + " called " + minimumBet);
        }
        return minimumBet;
    }

    public int check(){
        if(streetIn!=12){
            game.players.get(otherPlayerID).addMessage(this.name + " checked");
        }
        return 0;
    }

    public int foldBot(){
        this.fold();
        return 0;
    }

    //Calculates equity by simulating results over 100000n^2 hands, where n = limit
    //Worst case run-time is around 3s (100000 * 16^2 hand simulations)
    public double calculateEquity(){
        percentPot = (double)minimumBet/(pot - minimumBet);
        range = opponentAggression / percentPot + ThreadLocalRandom.current().nextDouble(0.01, 0.1);

        playerScore = 0;
        opponentScore = 0;
        //Limit is used to determine how many cards are included in the simulation for the opponent
        limit = (int)Math.ceil(Math.sqrt(range*169));
        //Limit is constrained between 1 and 13 (inclusive)
        if(limit==0){limit=1;}
        else if(limit>13){limit=13;}

        for(int i = 0; i<10000; i++){
            //Create a new deck every simulation, remove holeCards from deck, and then shuffle
            deck = new Deck();
            deck.removeCards(holeCards);
            deck.shuffle();

            switch (streetIn){
                case 9:
                    //Randomly initialize board
                    board = deck.deal(5);
                    break;
                case 10:
                    deck.removeCards(new Card[]{hand.getBoard()[0], hand.getBoard()[1], hand.getBoard()[2]});
                    //Set first three cards as board from current Hand
                    board[0] = hand.getBoard()[0]; board[1] = hand.getBoard()[1]; board[2] = hand.getBoard()[2];
                    //Randomly initialize rest of board
                    board[3] = deck.deal(1)[0]; board[4] = deck.deal(1)[0];
                    break;
                case 11:
                    deck.removeCards(new Card[]{hand.getBoard()[0], hand.getBoard()[1], hand.getBoard()[2], hand.getBoard()[3]});
                    //Set first three cards as board from current Hand
                    board[0] = hand.getBoard()[0]; board[1] = hand.getBoard()[1]; board[2] = hand.getBoard()[2];
                    board[2] = hand.getBoard()[3];
                    //Randomly initialize rest of board
                    board[4] = deck.deal(1)[0];
                    break;
                case 12:
                    deck.removeCards(new Card[]{hand.getBoard()[0], hand.getBoard()[1], hand.getBoard()[2], hand.getBoard()[3], hand.getBoard()[4]});
                    //Set first three cards as board from current Hand
                    board[0] = hand.getBoard()[0]; board[1] = hand.getBoard()[1]; board[2] = hand.getBoard()[2];
                    board[2] = hand.getBoard()[3]; board[4] = hand.getBoard()[4];
                    break;
            }
            //This loops through all cards in range and evaluates them versus current simulated board
            //Outer loop at the top iterates through different boards, thus allowing 100000 * limit^2 simulations
            for(int j = 0; j < limit; j++){
                for(int k = 0; k < limit; k++){
                    //Range double represents percentage of hands (i.e. 0.01 -> top 1% hands = AA, KK)
                    opponentCards = getCardsInRange(j, k);
                    deck.removeCards(opponentCards);
                    //Increment winner based on hand evaluation
                    winners = HandEvaluator.evaluateWinner(holeCards, opponentCards, board);
                    //Split pot (tie)
                    if(winners.size()==2){
                        playerScore++;
                        opponentScore++;
                    }
                    //If winner is first player returned (pokerBot) ++
                    else if(winners.get(0)==0){
                        playerScore++;
                    }
                    else{
                        opponentScore++;
                    }
                }
            }
        }
        System.out.println((playerScore) / (playerScore + opponentScore));
        return (playerScore) / (playerScore + opponentScore);
    }

    public double calculateEquityTestMethod(Card[] testBoard, double range, int streetIn){
        playerScore = 0;
        opponentScore = 0;
        //Limit is used to determine how many cards are included in the simulation for the opponent
        limit = (int)Math.ceil(Math.sqrt(range*169));
        if(limit==0){limit=1;}
        else if(limit>13){limit=13;}

        for(int i = 0; i<10000; i++){
            //Create a new deck every simulation
            deck = new Deck();
            deck.removeCards(holeCards);
            deck.shuffle();

            switch (streetIn){
                case HeadsUpHand.PRE_FLOP:
                    //Randomly initialize board
                    board = deck.deal(5);
                    break;
                case HeadsUpHand.FLOP:
                    deck.removeCards(new Card[]{testBoard[0], testBoard[1], testBoard[2]});
                    //Set first three cards as board from current Hand
                    board[0] = testBoard[0]; board[1] = testBoard[1]; board[2] = testBoard[2];
                    //Randomly initialize rest of board
                    board[3] = deck.deal(1)[0]; board[4] = deck.deal(1)[0];
                    break;
                case HeadsUpHand.TURN:
                    deck.removeCards(new Card[]{testBoard[0], testBoard[1], testBoard[2], testBoard[3]});
                    //Set first four cards as board from current Hand
                    board[0] = testBoard[0]; board[1] = testBoard[1]; board[2] = testBoard[2];
                    board[3] = testBoard[3];
                    //Randomly initialize rest of board
                    board[4] = deck.deal(1)[0];
                    break;
                case HeadsUpHand.RIVER:
                    deck.removeCards(new Card[]{testBoard[0], testBoard[1], testBoard[2], testBoard[3], testBoard[4]});
                    //Set all five cards as board from current Hand
                    board[0] = testBoard[0]; board[1] = testBoard[1]; board[2] = testBoard[2];
                    board[3] = testBoard[3]; board[4] = testBoard[4];
                    break;
            }
            //This loops through all cards in range and evaluates them versus current simulated board
            //Outer loop at the top iterates through different boards, thus allowing 100000 * limit^2 simulations
            for(int j = 0; j < limit; j++){
                for(int k = 0; k < limit; k++){
                    //Range double represents percentage of hands (i.e. 0.01 -> top 1% hands = AA, KK)
                    opponentCards = getCardsInRange(j, k);
                    deck.removeCards(opponentCards);
                    //Increment winner based on hand evaluation
                    winners = HandEvaluator.evaluateWinner(holeCards, opponentCards, board);
                    //Split pot (tie)
                    if(winners.size()==2){
                        playerScore++;
                        opponentScore++;
                    }
                    //If winner is first player returned (pokerBot) ++
                    else if(winners.get(0)==0){
                        playerScore++;
                    }
                    else{
                        opponentScore++;
                    }
                }
            }
        }
        //return calculated equity = hands won by player / total hands
        return (playerScore) / (playerScore + opponentScore);
    }

    public Card[] getCardsInRange(int j, int k){
        //cardsInRange are returned from a rectangular matrix based on the limit = sqrt(range * 169)
        //Ex. a range value of 0.025 represents roughly 1/40 of all 169 card combinations, or a 2x2 cutout of the
        //top-left end of the matrix (AA, AKo, AKs, KK). While this doesn't represent how a typical range functions,
        //it serves as a starting representation of how pokerBot will evaluate his hand against a range.
        cardsInRange = new Card[]{matrix.matrix[j][k][0], matrix.matrix[j][k][1]};
        return cardsInRange;
    }

}
