/**
 * Represents a card in the game of concentration
 *
 * @author Austin Sands
 */
public class Card {

	// true if card is flipped up, false otherwise
	private boolean flipped;
	// true if card is paired, false otherwise
	private boolean paired;
	// name on the card
	private String name;

	/**
	 * Constructs a card with the given name.
	 * 
	 * @param name the name on the card
	 */
	public Card(String name) {
		this.flipped = false;
		this.paired = false;
		this.name = name;
	}

	/**
	 * Gets the name on the card.
	 * 
	 * @return the name on the card
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name on the card.
	 * 
	 * @param name the name on the card
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Determines if the card is flipped up.
	 * 
	 * @return true if card is flipped up, false otherwise
	 */
	public boolean isFlipped() {
		return this.flipped;
	}

	/**
	 * Flips the card up or down.
	 * 
	 * @param whether the card is flipped up
	 */
	public void setFlipped(boolean up) {
		this.flipped = up;
	}

	/**
	 * Determines if the card is paired.
	 * 
	 * @return true if the card is paired, false otherwise
	 */
	public boolean isPaired() {
		return this.paired;
	}

	/**
	 * Pairs the card.
	 * 
	 * @param paired whether the card is paired
	 */
	public void setPaired(boolean paired) {
		this.paired = paired;
	}

	/**
	 * Determines if cards are equal.
	 * 
	 * @param other another card to compare
	 * @return true if the cards are equal, false otherwise
	 */
	public boolean equals(Card other) {
		return this.getName().equals(other.getName());
	}

	/**
	 * Gets the String representation of the card.
	 * 
	 * @return String representation of card
	 */
	public String toString() {
		return "Card [flipped=" + flipped + ", paired=" + paired + ", name=" + name + "]";
	}
}