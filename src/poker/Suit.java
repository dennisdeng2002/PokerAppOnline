package poker;

public enum Suit {

	Spades("s"),
	Clubs("c"),
	Hearts("h"),
	Diamonds("d"),;

	private final String value;

	Suit(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
