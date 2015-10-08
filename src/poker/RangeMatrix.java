package poker;

import java.util.ArrayList;
import java.util.Arrays;

public class RangeMatrix {

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

    public ArrayList<Card> aces = new ArrayList<>(Arrays.asList(Ah, As, Ac, Ad));
    public ArrayList<Card> kings = new ArrayList<>(Arrays.asList(Kh, Ks, Kc, Kd));
    public ArrayList<Card> queens = new ArrayList<>(Arrays.asList(Qh, Qs, Qc, Qd));
    public ArrayList<Card> jacks = new ArrayList<>(Arrays.asList(Jh, Js, Jc, Jd));
    public ArrayList<Card> tens = new ArrayList<>(Arrays.asList(Th, Ts, Tc, Td));
    public ArrayList<Card> nines = new ArrayList<>(Arrays.asList(h9, s9, c9, d9));
    public ArrayList<Card> eights = new ArrayList<>(Arrays.asList(h8, s8, c8, d8));
    public ArrayList<Card> sevens = new ArrayList<>(Arrays.asList(h7, s7, c7, d7));
    public ArrayList<Card> sixes = new ArrayList<>(Arrays.asList(h6, s6, c6, d6));
    public ArrayList<Card> fives = new ArrayList<>(Arrays.asList(h5, s5, c5, d5));
    public ArrayList<Card> fours = new ArrayList<>(Arrays.asList(h4, s4, c4, d4));
    public ArrayList<Card> threes = new ArrayList<>(Arrays.asList(h3, s3, c3, d3));
    public ArrayList<Card> deuces = new ArrayList<>(Arrays.asList(h2, s2, c2, d2));

    public ArrayList<ArrayList<Card>> cardRanks = new ArrayList<>();

    public Card[][][] matrix;

    //Range matrix only stores one copy of each type of hand (AhAs as opposed to all 6 combos of Aces)
    //This may lead to minor inaccuracies during the equity calculation but allows for easier implementation and
    //faster overall equity calculation
    public RangeMatrix(){
        constructCardRanks();
        matrix = new Card[15][15][2];

        //Fill diagonal with pairs
        for(int i = 0; i < 13; i++){
            matrix[i][i] = new Card[]{cardRanks.get(i).get(0), cardRanks.get(i).get(1)};
        }
        //Fill upper half with suited cards
        for(int j = 0; j < 13; j++){
            for(int k = j+1; k<12; k++){
                matrix[j][k] = new Card[]{cardRanks.get(j).get(0), cardRanks.get(k).get(0)};
            }
        }
        //Fill lower half with unsuited cards
        for(int j = 0; j < 13; j++){
            for(int k = j+1; k<12; k++){
                matrix[k][j] = new Card[]{cardRanks.get(j).get(0), cardRanks.get(k).get(1)};
            }
        }

    }

    public void constructCardRanks(){
        cardRanks.add(aces);
        cardRanks.add(kings);
        cardRanks.add(queens);
        cardRanks.add(jacks);
        cardRanks.add(tens);
        cardRanks.add(nines);
        cardRanks.add(eights);
        cardRanks.add(sevens);
        cardRanks.add(sixes);
        cardRanks.add(fives);
        cardRanks.add(fours);
        cardRanks.add(threes);
        cardRanks.add(deuces);

    }
}
