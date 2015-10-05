package poker;

import com.sun.tools.javac.util.ArrayUtils;
import org.eclipse.jetty.util.ArrayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Deck {

	private ArrayList<Card> cards;

	public Deck() {
		cards = new ArrayList<Card>();
        int i = 0;
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }
	
	//Copy constructor. Instantiates an aliased new deck of cards. 
	public Deck(Deck other) {
		
		this.cards = new ArrayList<Card>(other.cards.size());
		for (int i = 0; i < cards.size(); i++) {
			this.cards.set(i, other.cards.get(i));
		}
		
	}

	public Card getCardAt(int position) {
		
		return this.cards.get(position);
		
	}

	public int getNumCards() {
		
		return this.cards.size();
	}

	//Does the Fisher-Yates shuffling algorithm on this deck of cards
	public void shuffle() {

		Random rn = new Random();
		
		for (int i = cards.size()-1; i > 0; i--) {
			
			int swapIdx = rn.nextInt(i+1);
			
			Card temp = cards.get(swapIdx);
			cards.set(swapIdx, cards.get(i));
			cards.set(i, temp);
			
		}
		
	}

	//Cuts the deck at a given position. If the position is 4, then the newly
	//cut deck will begin with cards[4] to the end... followed by cards[0] to
	// cards[3]
	public void cut(int position) {
		
		ArrayList<Card> tempDeck = new ArrayList<Card>(this.cards.size());

		int belowCut = position;
		int aboveCut = 0;
		for (int i = 0; i < tempDeck.size(); i++) {
			if (i < tempDeck.size() - position) {
				tempDeck.set(i, this.cards.get(belowCut++));
			} else {
				tempDeck.set(i, this.cards.get(aboveCut++));
			}
		}
		
		for (int i = 0; i < this.cards.size(); i++) {
			tempDeck.set(i, tempDeck.get(i));
		}
		
	}
	
	//Returns an array of cards containing the cards that were dealt.
	//Also updates the current deck of cards to have an appropriate array of
	//cards after dealing numCards. 
	//Re-indexes this deck such that there are no null pointers in the beginning.
	public Card[] deal(int numCards) {

		int copylength = this.cards.size() - numCards;

		Card[] dealtDeck = new Card[copylength];
		Card[] dealtCards = new Card[numCards];

		//Arraycopy(source array, source start point, target array, target start point, length copied
		System.arraycopy(this.cards.toArray(), numCards, dealtDeck, 0, copylength);
		System.arraycopy(this.cards.toArray(), 0, dealtCards, 0, numCards);

		this.cards = new ArrayList<>(Arrays.asList(dealtDeck));
		return dealtCards;
		
	}

	//Just calls this class' deal method to get rid of one card. 
	//The card that was burned can never be accessed.
	public void burn() {
		
		this.deal(1);
		
	}

	public void removeCards(Card[] cardsForRemoval){
		for(int i = 0; i < cardsForRemoval.length; i++){
			for(int j = 0; j < cards.size(); j++){
				if(cardsForRemoval[i].equals(cards.get(j))){
					cards.remove(j);
				}
			}
		}
	}

	public boolean equals(Object other) {
		
		if (!(other instanceof Deck)) {
			return false;
		}
		
		Deck o = (Deck)other;
		for (int i = 0; i < this.getNumCards(); i++) {
			if (this.cards.get(i).equals(o.cards.get(i)) == false) {
				return false;
			}
		}
		
		return this.getNumCards() == o.getNumCards();
	}
	
}
