/*
 @author Brian Lee & Dennis Deng (c) 2015
 An immutable class representing an object composed of a unique 
 combination of the enums, rank and suit
*/
 
package poker;

import java.io.Serializable;

public class Card implements Comparable<Card>, Serializable {

	private final Suit suit;    
	private final Rank rank;

	public Card(Rank rank, Suit suit) {

		this.suit = suit;
		this.rank = rank;
		
	}

	public Card(Card other) {
		
		this.suit = other.suit;
		this.rank = other.rank;
		
	}
	
	public Rank getRank() {
		return rank;
	}

	public Suit getSuit() {
		return suit;
	}

	public String toString() {
		String retVal = "";
		retVal += rank.getValue() + suit.getValue();
		return retVal;
		
	}
	
	
	public int compareTo(Card other) {
		
		return this.rank.compareTo(other.rank);
		
	}
	
	public boolean equals(Object o) {
		
		if (!(o instanceof Card)) {
			return false;
		}
		
		Card c = (Card)o;
		return rank.equals(c.rank) && suit.equals(c.suit);
		
	}


}