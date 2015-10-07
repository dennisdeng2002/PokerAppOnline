package tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import poker.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class myTests {

	Deck deck = new Deck();

	public Card Ah = new Card(Rank.ACE, Suit.Hearts);
	public Card Kh = new Card(Rank.KING, Suit.Hearts);
	public Card Qh = new Card(Rank.QUEEN, Suit.Hearts);
	public Card Jh = new Card(Rank.JACK, Suit.Hearts);
	public Card Th = new Card(Rank.TEN, Suit.Hearts);
	public Card h9 = new Card(Rank.NINE, Suit.Hearts);
	public Card h8 = new Card(Rank.EIGHT, Suit.Hearts);
	public Card h7 = new Card(Rank.SEVEN, Suit.Hearts);
	public Card h6 = new Card(Rank.SIX, Suit.Hearts);
	public Card h5 = new Card(Rank.FIVE, Suit.Hearts);
	public Card h4 = new Card(Rank.FOUR, Suit.Hearts);
	public Card h3 = new Card(Rank.THREE, Suit.Hearts);
	public Card h2 = new Card(Rank.DEUCE, Suit.Hearts);

	public ArrayList<Card> hearts = new ArrayList<>(Arrays.asList(Ah, Kh, Qh, Jh, Th, h9, h8, h7, h6, h5, h4, h3, h2));

	public Card As = new Card(Rank.ACE, Suit.Spades);
	public Card Ks = new Card(Rank.KING, Suit.Spades);
	public Card Qs = new Card(Rank.QUEEN, Suit.Spades);
	public Card Js = new Card(Rank.JACK, Suit.Spades);
	public Card Ts = new Card(Rank.TEN, Suit.Spades);
	public Card s9 = new Card(Rank.NINE, Suit.Spades);
	public Card s8 = new Card(Rank.EIGHT, Suit.Spades);
	public Card s7 = new Card(Rank.SEVEN, Suit.Spades);
	public Card s6 = new Card(Rank.SIX, Suit.Spades);
	public Card s5 = new Card(Rank.FIVE, Suit.Spades);
	public Card s4 = new Card(Rank.FOUR, Suit.Spades);
	public Card s3 = new Card(Rank.THREE, Suit.Spades);
	public Card s2 = new Card(Rank.DEUCE, Suit.Spades);

	public ArrayList<Card> spades = new ArrayList<>(Arrays.asList(As, Ks, Qs, Js, Ts, s9, s8, s7, s6, s5, s4, s3, s2));

	public Card Ac = new Card(Rank.ACE, Suit.Clubs);
	public Card Kc = new Card(Rank.KING, Suit.Clubs);
	public Card Qc = new Card(Rank.QUEEN, Suit.Clubs);
	public Card Jc = new Card(Rank.JACK, Suit.Clubs);
	public Card Tc = new Card(Rank.TEN, Suit.Clubs);
	public Card c9 = new Card(Rank.NINE, Suit.Clubs);
	public Card c8 = new Card(Rank.EIGHT, Suit.Clubs);
	public Card c7 = new Card(Rank.SEVEN, Suit.Clubs);
	public Card c6 = new Card(Rank.SIX, Suit.Clubs);
	public Card c5 = new Card(Rank.FIVE, Suit.Clubs);
	public Card c4 = new Card(Rank.FOUR, Suit.Clubs);
	public Card c3 = new Card(Rank.THREE, Suit.Clubs);
	public Card c2 = new Card(Rank.DEUCE, Suit.Clubs);

	public ArrayList<Card> clubs = new ArrayList<>(Arrays.asList(Ac, Kc, Qc, Jc, Tc, c9, c8, c7, c6, c5, c4, c3, c2));

	public Card Ad = new Card(Rank.ACE, Suit.Diamonds);
	public Card Kd = new Card(Rank.KING, Suit.Diamonds);
	public Card Qd = new Card(Rank.QUEEN, Suit.Diamonds);
	public Card Jd = new Card(Rank.JACK, Suit.Diamonds);
	public Card Td = new Card(Rank.TEN, Suit.Diamonds);
	public Card d9 = new Card(Rank.NINE, Suit.Diamonds);
	public Card d8 = new Card(Rank.EIGHT, Suit.Diamonds);
	public Card d7 = new Card(Rank.SEVEN, Suit.Diamonds);
	public Card d6 = new Card(Rank.SIX, Suit.Diamonds);
	public Card d5 = new Card(Rank.FIVE, Suit.Diamonds);
	public Card d4 = new Card(Rank.FOUR, Suit.Diamonds);
	public Card d3 = new Card(Rank.THREE, Suit.Diamonds);
	public Card d2 = new Card(Rank.DEUCE, Suit.Diamonds);

	public ArrayList<Card> diamonds = new ArrayList<>(Arrays.asList(Ad, Kd, Qd, Jd, Td, d9, d8, d7, d6, d5, d4, d3, d2));

	@Test
	public void testBasicDeck() {

		Deck deckA = new Deck();
		Deck deckB = new Deck();
		
		assertTrue(deckA.getNumCards() == 52);
		assertTrue(deckA.equals(deckB));
		
		deckA.shuffle();
		assertFalse(deckA.equals(deckB));
		
		deckA.deal(5);
		assertTrue(deckA.getNumCards() == 47);
		
	}

	@Test 
	public void testBasicCard() {
		
		Card aceHeartCopy = new Card(Ah);
		
		assertTrue(Ah.equals(aceHeartCopy));
		assertFalse(Ah.equals(Ac));
		assertTrue(Ah.compareTo(Ac) == 0);
		assertTrue(Ac.compareTo(s2) > 0);
		assertTrue(s2.compareTo(Td) < 0);
		
		ArrayList<Card> temp = new ArrayList<>(Arrays.asList(Ah, Ac, s2, Td));

		System.out.println("Ah, Ac, 2s, Td");
		System.out.println(temp);
	
		HandEvaluator.sort(temp);
		System.out.println("2s, Td, Ah, Ac");
		System.out.println(temp);
		
	}
	
	@Test
	public void testShuffle(){
		
		Deck deckA = new Deck();
	
		System.out.println(deckA);

		deckA.shuffle();
		System.out.println(deckA);

	}
	
	@Test 
	public void testQuad() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(Ah,
				Ad, Ac, As, s2, h7, Td));

		HandEvaluator.sort(temp);
		System.out.println(temp);

		System.out.println("Quad Aces");
		System.out.println(Arrays.toString(HandEvaluator.hasFourOfAKind(temp)));

		ArrayList <Card> temp2 = new ArrayList <Card>(Arrays.asList(Ah,
				Ad, d3, s3, Td, c6, h3));

		HandEvaluator.sort(temp2);
		System.out.println("Not Quads");
		System.out.println(Arrays.toString(HandEvaluator.hasFourOfAKind(temp2)));

	}

	@Test
	public void testFullHouse() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(s7,
				c4, d3, h3, s3, h7, c7));

		HandEvaluator.sort(temp);
		System.out.println("Sevens Full of Threes");
		System.out.println(Arrays.toString(HandEvaluator.hasFullHouse(temp)));

	}

	@Test
	public void testFlush() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(s7,
				c3, s9, h3, s3, s4, s5));

		//Sort by rank, then sort by suit
		HandEvaluator.sort(temp);
		HandEvaluator.sortSuit(temp);
		System.out.println("9 High Flush");
		System.out.println(Arrays.toString(HandEvaluator.hasFlush(temp)));

	}

	@Test
	public void testStraight() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(s7,
				c6, d3, s2, c7, s4, s5));

		//Sort by rank, then sort by suit
		HandEvaluator.sort(temp);
		
		ArrayList <Card> temp2 = new ArrayList <Card>(Arrays.asList(h2,
				c3, s4, h5, c7, c8, c6));

		HandEvaluator.sort(temp2);

		System.out.println("7 High Straight");
		System.out.println(Arrays.toString(HandEvaluator.hasStraight(temp)));

		System.out.println("8 High Straight");
		System.out.println(Arrays.toString(HandEvaluator.hasStraight(temp2)));

		ArrayList<ArrayList<Card>> straights = new ArrayList<ArrayList<Card>>();
		ArrayList<Card> straight = new ArrayList<>();

		int counter = 0;
		while(counter<7){
			for(int i = counter; i<counter+7; i++){
				switch (ThreadLocalRandom.current().nextInt(1,5)){
					case 1:
						straight.add(hearts.get(i));
						break;
					case 2:
						straight.add(clubs.get(i));
						break;
					case 3:
						straight.add(diamonds.get(i));
						break;

					case 4:
						straight.add(spades.get(i));
						break;
				}
			}
			straights.add((ArrayList<Card>) straight.clone());
			straight.clear();
			counter++;
		}

		System.out.println(straights);

		int isStraight;
		int correctValue;
		int evaluatedValue;

		//Test consecutive 7 card straights
		for(int j = 0; j < straights.size(); j++){
			HandEvaluator.sort(straights.get(j));
			System.out.println(straights.get(j).get(6).getRank().getValue() + " High Straight");
			System.out.println(Arrays.toString(HandEvaluator.hasStraight(straights.get(j))));

			isStraight = HandEvaluator.hasStraight(straights.get(j))[0];
			correctValue = straights.get(j).get(6).getRank().getNumeral();
			evaluatedValue = HandEvaluator.hasStraight(straights.get(j))[1];

			assertTrue(isStraight == 18);
			assertTrue(correctValue==evaluatedValue);
		}

	}
	
	
	@Test
	public void testTwoPair() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(s7,
				c6, h7, s2, s3, c3, s5));

		HandEvaluator.sort(temp);

		ArrayList <Card> temp2 = new ArrayList <Card>(Arrays.asList(Ac,
				Ad, h7, c7, s3, c3, s5));
		
		HandEvaluator.sort(temp2);

		System.out.println("Two Pair Sevens and Threes");
		System.out.println(Arrays.toString(HandEvaluator.hasTwoPair(temp)));

		System.out.println("Two Pair Aces and Sevens");
		System.out.println(Arrays.toString(HandEvaluator.hasTwoPair(temp2)));
	}
	
	
	@Test
	public void testStraightFlush() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(s7,
				s6, Ah, Kh, Qh, Jh, Th));
		
		HandEvaluator.sortSuitAndNumeral(temp);
		
		ArrayList <Card> temp2 = new ArrayList <Card>(Arrays.asList(s7,
				s6, s5, s4, s3, Jh, Th));
		
		HandEvaluator.sortSuitAndNumeral(temp2);

		System.out.println("7 High Straight Flush");
		System.out.println(Arrays.toString(HandEvaluator.hasStraightFlush(temp2)));

		ArrayList <Card> temp3 = new ArrayList <Card>(Arrays.asList(Ah,
				h2, h5, h4, h3, Js, Ts));

		HandEvaluator.sortSuitAndNumeral(temp3);
		System.out.println("5 High Straight Flush");
		System.out.println(Arrays.toString(HandEvaluator.hasStraightFlush(temp3)));

		
	}

	@Test
		 public void testhasThreeOfAKind() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(Ac,
				c3, d3, h3, c6, Td, c7));

		HandEvaluator.sort(temp);
		System.out.println(Arrays.toString(HandEvaluator.hasThreeOfAKind(temp)));

	}

	@Test
	public void testhasOnePair() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(Ac,
				c3, c4, h3, c6, Td, c7));

		HandEvaluator.sort(temp);
		System.out.println("Pair of Threes");
		System.out.println(Arrays.toString(HandEvaluator.hasOnePair(temp)));

	}

	@Test
	public void testHandEvaluator() {

		ArrayList <Card> temp = new ArrayList <Card>(Arrays.asList(d3, s8, c5, s4, c2));
		Card[] tempBoard = temp.toArray(new Card[temp.size()]);
		ArrayList <Card> temp1 = new ArrayList <Card>(Arrays.asList(d7, Ts, Td, s4, s5));
		Card[] tempBoard1 = temp1.toArray(new Card[temp1.size()]);
		ArrayList <Card> temp2 = new ArrayList <Card>(Arrays.asList(Qs, Ts, Td, s4, s5));
		Card[] tempBoard2 = temp2.toArray(new Card[temp2.size()]);
		ArrayList <Card> temp3 = new ArrayList <Card>(Arrays.asList(Th, Ts, Td, Tc, Ad));
		Card[] tempBoard3 = temp3.toArray(new Card[temp3.size()]);
		ArrayList <Card> temp4 = new ArrayList <Card>(Arrays.asList(Ad, d4, d7, d3, Td));
		Card[] tempBoard4 = temp4.toArray(new Card[temp4.size()]);
		ArrayList <Card> temp5 = new ArrayList <Card>(Arrays.asList(c2, s8, d7, s2, Td));
		Card[] tempBoard5 = temp5.toArray(new Card[temp5.size()]);

		ArrayList <HeadsUpPlayer> tempPlayers = new ArrayList<HeadsUpPlayer>(4);
		tempPlayers.add(new HeadsUpPlayer("a",200,0,null,1));
		tempPlayers.add(new HeadsUpPlayer("b",200,1,null,0));

		Card [] tempPlayerCards1 = new Card[2];
		tempPlayerCards1[0] = c7;
		tempPlayerCards1[1] = c6;

		Card [] tempPlayerCards2 = new Card[2];
		tempPlayerCards2[0] = c2;
		tempPlayerCards2[1] = s2;

		Card [] tempPlayerCards3 = new Card[2];
		tempPlayerCards3[0] = Ks;
		tempPlayerCards3[1] = c9;

		Card [] tempPlayerCards4 = new Card[2];
		tempPlayerCards4[0] = Kc;
		tempPlayerCards4[1] = Js;

		tempPlayers.get(0).receiveHand(tempPlayerCards1);
		tempPlayers.get(1).receiveHand(tempPlayerCards2);

		/*
		//d3, s8, c5, s4, c2
		//Player 0: c7, c6
		//Player 1: c2, s2
		System.out.println("Player 0 Wins");
		System.out.println(HandEvaluator.evaluateHeadsUpHands(tempPlayers, tempBoard));

		//d7, Ts, Td, s4, s5
		//Player 0: c7, c6
		//Player 1: c2, s2
		System.out.println("Player 0 Wins");
		System.out.println(HandEvaluator.evaluateHeadsUpHands(tempPlayers, tempBoard1));

		//Qs, Ts, Td, s4, s5
		//Player 0: c7, c6
		//Player 1: c2, s2
		System.out.println("Player 1 Wins");
		System.out.println(HandEvaluator.evaluateHeadsUpHands(tempPlayers, tempBoard2));

		//Th, Ts, Td, Tc, Ad
		//Player 0: c7, c6
		//Player 1: c2, s2
		System.out.println("Split Pot");
		System.out.println(HandEvaluator.evaluateHeadsUpHands(tempPlayers, tempBoard3));

		//Ad, d4, d7, d3, Td
		//Player 0: c7, c6
		//Player 1: c2, s2
		System.out.println("Split Pot");
		System.out.println(HandEvaluator.evaluateHeadsUpHands(tempPlayers, tempBoard4));

		//c2, s8, d7, s2, Td
		//Player 0: c7, c6
		//Player 1: c2, s2
		System.out.println("Player 1 Wins");
		System.out.println(HandEvaluator.evaluateHeadsUpHands(tempPlayers, tempBoard5));
		*/

		ArrayList<ArrayList<Card>> boards = new ArrayList<ArrayList<Card>>();
		ArrayList<Card> board = new ArrayList<>();

		int counter = 0;

		while(counter<20){
			for(int i = counter; i<counter+7; i++){
				switch (ThreadLocalRandom.current().nextInt(1,5)){
					case 1:
						board.add(hearts.get(ThreadLocalRandom.current().nextInt(0,13)));
						break;
					case 2:
						board.add(clubs.get(ThreadLocalRandom.current().nextInt(0,13)));
						break;
					case 3:
						board.add(diamonds.get(ThreadLocalRandom.current().nextInt(0,13)));
						break;
					case 4:
						board.add(spades.get(ThreadLocalRandom.current().nextInt(0,13)));
						break;
				}
			}
			boards.add((ArrayList<Card>) board.clone());
			board.clear();
			counter++;
		}

		//Test random set of cards
		for(int k = 0; k < boards.size(); k++){
			HandEvaluator.sort(boards.get(k));
			System.out.println(boards.get(k));
			System.out.println(Arrays.toString(HandEvaluator.determineStrength(boards.get(k))));
		}
		//	For Reference
		//	STRAIGHTFLUSH = 22;
		//	QUAD = 21;
		//	FULLHOUSE = 20;
		//	FLUSH = 19;
		//	STRAIGHT = 18;
		//	TRIPS = 17;
		//	TWOPAIR = 16;
		//	PAIR = 15;
	}

	@Test
	public void testPokerBot() {
		PokerBot bot = new PokerBot("Bot1", 200, 0, 1);

		Card[] holeCards = new Card[]{Ah, Ad};
		Card[] board = new Card[]{Qc, c9, d2, d7, s4};
		bot.receiveHand(holeCards);
		//Testing preflop equity (streetIn = 9)
		System.out.println(bot.calculateEquityTestMethod(board, 9));

		holeCards = new Card[]{Ah, Kd};
		bot.receiveHand(holeCards);
		System.out.println(bot.calculateEquityTestMethod(board, 9));

		holeCards = new Card[]{Jc, Tc};
		bot.receiveHand(holeCards);
		System.out.println(bot.calculateEquityTestMethod(board, 9));

		holeCards = new Card[]{Jc, Tc};
		bot.receiveHand(holeCards);
		System.out.println(bot.calculateEquityTestMethod(board, 10));

		holeCards = new Card[]{Jc, Tc};
		board = new Card[]{Qc, c9, c8, d7, s4};
		bot.receiveHand(holeCards);
		//Testing flop equity (streetIn = 10)
		System.out.println(bot.calculateEquityTestMethod(board, 10));

		holeCards = new Card[]{c7, c6};
		board = new Card[]{c5, Ad, c8, d7, s4};
		bot.receiveHand(holeCards);
		//Testing flop equity (streetIn = 10)
		System.out.println(bot.calculateEquityTestMethod(board, 10));

	}

}
