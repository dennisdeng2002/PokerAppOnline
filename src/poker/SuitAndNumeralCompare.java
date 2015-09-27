package poker;

import java.util.Comparator;

public class SuitAndNumeralCompare implements Comparator<Card> {

	//Suit has precedence over numeral
	@Override
	public int compare(Card o1, Card o2) {
		
		//if o1's suit is greater than o2's suit
		if (o1.getSuit().compareTo(o2.getSuit()) > 0) {
			return 1;
			
		//if o1's suit is less than o2's suit	
		} else if (o1.getSuit().compareTo(o2.getSuit()) < 0) {
			return -1;
		} else { //suits are the same, compare numerals
			return o1.getRank().compareTo(o2.getRank());
		}
		
		
	}

}
