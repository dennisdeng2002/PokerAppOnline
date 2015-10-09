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
    private Random random;
    private RangeMatrix matrix;
    Card[] cardsInRange;
    private int limit;

    public PokerBot(String name, int money, int id, int otherPlayerID){
        this.name = name;
        this.money = money;
        this.id = id;
        holeCards = new Card[2];
        folded = false;
        isAllIn = false;
        turnToAct = true;
        this.otherPlayerID = otherPlayerID;
        random = new Random();
        matrix = new RangeMatrix();
    }

    public synchronized int act(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn){
        //PREFLOP = 9, FLOP = 10, TURN = 11, RIVER = 12
        switch(streetIn){
            case 9:
                betSize = actionPreFlop(minimumBet, pot, hand, game, streetIn);
                break;
            case 10:
                betSize = actionFlop(minimumBet, pot, hand, game, streetIn);
                break;
            case 11:
                betSize = actionTurn(minimumBet, pot, hand, game, streetIn);
                break;
            case 12:
                betSize = actionRiver(minimumBet, pot, hand, game, streetIn);
                break;
        }
        System.out.println(Arrays.toString(holeCards));
        System.out.println(betSize);

        return betSize;
    }

    public int actionPreFlop(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn){
        equity = calculateEquity(hand, 0.3, streetIn);

        //Raise
        if(equity >= 0.5){
            betSize = bet(minimumBet, hand, game);
        }
        //Check/Call
        else if (equity < 0.50 && equity>= 0.4){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == game.getBigBlind() && game.bbIndex == id){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call(minimumBet, hand, game);
            }
        }
        //Fold
        else{
            if(minimumBet == game.getBigBlind() && game.bbIndex == id){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            else{
                betSize = 0;
                this.fold();
            }
        }
        return betSize;
    }

    public int actionFlop(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn){
        equity = calculateEquity(hand, 0.3, streetIn);

        //Raise
        if(equity >= 0.5){
            betSize = bet(minimumBet, hand, game);
        }
        //Check/Call
        else if (equity < 0.50 && equity>= 0.4){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == 0){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call(minimumBet, hand, game);
            }
        }
        //Fold
        else{
            if(minimumBet == 0){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            else{
                betSize = 0;
                this.fold();
            }
        }
        return betSize;
    }

    public int actionTurn(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn){
        equity = calculateEquity(hand, 0.3, streetIn);

        //Raise
        if(equity >= 0.5){
            betSize = bet(minimumBet, hand, game);
        }
        //Check/Call
        else if (equity < 0.50 && equity>= 0.4){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == 0){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call(minimumBet, hand, game);
            }
        }
        //Fold
        else{
            if(minimumBet == 0){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            else{
                betSize = 0;
                this.fold();
            }
        }
        return betSize;
    }

    public int actionRiver(int minimumBet, int pot, HeadsUpHand hand, HeadsUpPokerGame game, int streetIn){
        equity = calculateEquity(hand, 0.3, streetIn);

        //Raise
        if(equity >= 0.5){
            betSize = bet(minimumBet, hand, game);
        }
        //Check/Call
        else if (equity < 0.50 && equity>= 0.4){
            //Determine if player called (minimum bet will equal the BB) and whether pokerBot is the BB
            if(minimumBet == 0){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            //Otherwise player must have raised/bet
            else{
                betSize = call(minimumBet, hand, game);
            }
        }
        //Fold
        else{
            if(minimumBet == 0){
                betSize = minimumBet;
                game.players.get(otherPlayerID).addMessage(this.name + " checked");
            }
            else{
                betSize = 0;
                this.fold();
            }
        }
        return betSize;
    }

    public int bet(int minimumBet, HeadsUpHand hand, HeadsUpPokerGame game){
        if(minimumBet == 0){
            betSize = (int)(ThreadLocalRandom.current().nextDouble(.25, 1.25) * hand.getPot());
        }
        else{
            betSize = (int)(ThreadLocalRandom.current().nextDouble(1.25, 2.5) * minimumBet);
        }
        hand.addToPot(betSize);
        spendMoney(betSize);
        game.players.get(otherPlayerID).addMessage(this.name + " bet " + betSize);
        return betSize;
    }

    public int call(int minimumBet, HeadsUpHand hand, HeadsUpPokerGame game){
        System.out.println(minimumBet);
        betSize = minimumBet;
        hand.addToPot(minimumBet - streetMoney);
        spendMoney(minimumBet - streetMoney);
        game.players.get(otherPlayerID).addMessage(this.name + " called " + betSize);
        return betSize;
    }

    //Calculates equity by simulating results over 100000n^2 hands, where n = limit
    //Worst case run-time is around 3s (100000 * 16^2 hand simulations)
    public double calculateEquity(HeadsUpHand hand, double range, int streetIn){
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
