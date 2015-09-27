package poker;

public enum Rank {

	DEUCE("2",2),
	THREE("3",3),
	FOUR("4",4),
	FIVE("5",5),
	SIX("6",6),
	SEVEN("7",7),
	EIGHT("8",8),
	NINE("9",9),
	TEN("T",10),
	JACK("J",11),
	QUEEN("Q",12),
	KING("K",13),
	ACE("A",14),;

	private final String value;
	private int numeral;

	Rank(String value, int numeral) {
		this.value = value;
		this.numeral = numeral;
	}

	public String getValue() {
		return value;
	}
	
	public int getNumeral() {
		return numeral;
	}


}
