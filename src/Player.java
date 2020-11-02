/**
 * Represents a player in the game of concentration.
 * 
 * @author Austin Sands
 */
public class Player {

	// number of moves the player has made
	private int moves;

	// number of matches the player has made
	private int matches;

	/**
	 * Constructs a player
	 */
	public Player() {
		moves = 0;
		matches = 0;
	}

	/**
	 * Resets the player.
	 */
	public void reset() {
		moves = 0;
		matches = 0;
	}

	/**
	 * Gets the number of moves the player has made
	 * 
	 * @return the number of moves the player has made
	 */
	public int getMoves() {
		return moves;
	}

	/**
	 * Increments the number of moves the player has made
	 */
	public void addMove() {
		moves++;
	}

	/**
	 * Gets the number of matches the player has made
	 * 
	 * @return the number of matches the player has made
	 */
	public int getMatches() {
		return matches;
	}

	/**
	 * Increments the number of matches the player has made
	 */
	public void addMatch() {
		matches++;
	}

	/**
	 * Gets the String representation of the player.
	 * 
	 * @return String representation of player
	 */
	public String toString() {
		return "Player [moves=" + moves + ", matches=" + matches + "]";
	}
}