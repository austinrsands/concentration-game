/**
 * Represents a board in the game of concentration
 * 
 * @author Austin Sands
 */
public class Board {
	/*
	 * The area of the board must be positive, even, and greater than or equal to 2,
	 * and the board will scale accordingly
	 */
	private static final int DEFAULT_COLUMNS = 6;
	private static final int DEFAULT_ROWS = 6;

	// the minimum board area
	private static final int MIN_BOARD_SIZE = 2;

	/*
	 * Defines the default size of a card, neither should be smaller than 4 and both
	 * should be even
	 */
	private static final int DEFAULT_CARD_WIDTH = 12;
	private static final int DEFAULT_CARD_HEIGHT = 4;

	/*
	 * rows * columns must be even and greater than or equal to 2, and the board
	 * will scale accordingly
	 */
	private int columns;
	private int rows;

	// Symbols for displaying the board
	private static final char INTERSECTION_SYMBOL = '+';
	private static final char VERTICAL_SEPARATOR_SYMBOL = '|';
	private static final char HORIZONTAL_SEPARATOR_SYMBOL = '-';
	private static final char REMOVED_SYMBOL = '-';
	private static final char EMPTY_CHAR = ' ';

	// default names to appear on the cards
	private static final String[] DEFAULT_NAMES = { "Alice", "Bob", "Charlie", "David", "Ellen", "Frank", "Gerry",
			"Hanna", "Ian" };

	// possible names to appear on the cards
	private String[] names;

	// the chosen names to appear on the cards
	private String[] chosenNames;

	// the group of cards
	private Card[][] cards;

	// the string representing the board
	private StringBuilder boardString = new StringBuilder();

	// the "width" of the board string, from first index to first '\n'
	private int boardWidth;

	// the "height" of the board string
	private int boardHeight;

	// the number of possible matches
	private int possibleMatches;

	// whether the board has been initialized
	private boolean initialized = false;

	/**
	 * Constructs a board with the given columns, rows, and names.
	 * 
	 * @param columns number of columns
	 * @param rows    number of rows
	 * @param names   names to appear on cards
	 */
	public Board(int columns, int rows, String[] names) {
		this.columns = columns;
		this.rows = rows;
		this.names = names;
		this.possibleMatches = (columns * rows) / 2;
	}

	/**
	 * Constructs a board with the given columns, rows, and names.
	 * 
	 * @param columns number of columns
	 * @param rows    number of rows
	 * @param names   names to appear on cards
	 */
	public Board(int columns, int rows) {
		this(columns, rows, DEFAULT_NAMES);
	}

	/**
	 * Constructs a board with the given names.
	 * 
	 * @param names possible names to appear on the cards
	 */
	public Board(String[] names) {
		// call other constructor to reduce duplicate code
		this(DEFAULT_COLUMNS, DEFAULT_ROWS, names);
	}

	/**
	 * Constructs a board with default properties.
	 */
	public Board() {
		// call other constructor to reduce duplicate code
		this(DEFAULT_COLUMNS, DEFAULT_ROWS, DEFAULT_NAMES);
	}

	/**
	 * Prepares the board for the start of the game.
	 */
	public void setup() {
		// check preconditions
		checkBoardParameters();

		// create and add cards
		chooseNames();
		scrambleNames();
		generateCards();
		populateBoard();

		// set initialized tag
		initialized = true;
	}

	/*
	 * Picks the pairs of names for the board
	 */
	private void chooseNames() {
		chosenNames = new String[rows * columns];
		// loops through NAMES array, choosing pairs of names until chosenNames is full
		for (int i = 0, j = 0; i < chosenNames.length - 1; i += 2, j++) {

			// if there are not enough unique names, reuse pairs
			if (j >= names.length) {
				j %= names.length;
			}

			// add pair of names to chosenNames
			String name = names[j];
			chosenNames[i] = name;
			chosenNames[i + 1] = name;
		}
	}

	/*
	 * Scrambles the pairs of names
	 */
	private void scrambleNames() {
		for (int i = 0; i < chosenNames.length; i++) {
			int newIndex = (int) (Math.random() * chosenNames.length);
			String replacement = chosenNames[newIndex];
			chosenNames[newIndex] = chosenNames[i];
			chosenNames[i] = replacement;
		}
	}

	/*
	 * Creates the cards with proper names
	 */
	private void generateCards() {
		cards = new Card[rows][columns];
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				int index = r * columns + c;
				cards[r][c] = new Card(chosenNames[index]);
			}
		}
	}

	/*
	 * Populates the board with the string representations of the cards and row and
	 * column numbering
	 */
	private void populateBoard() {
		boardString = new StringBuilder();

		// the width and height of the string from the perspective of a grid
		boardWidth = (DEFAULT_CARD_WIDTH / 2) + columns * DEFAULT_CARD_WIDTH + 2; // + 2 accounts for \n and last column
		boardHeight = (DEFAULT_CARD_HEIGHT / 2) + rows * DEFAULT_CARD_HEIGHT;

		// add grid to boardString
		addGrid();

		// add cards to boardString
		addCardNames();

		// add column numbering to boardString
		addColumnNumbers();

		// add row numbering to boardString
		addRowNumbers();

	}

	/*
	 * Adds grid characters to the board
	 */
	private void addGrid() {
		// loops through board, adding grid characters
		for (int i = -DEFAULT_CARD_HEIGHT / 2; i <= rows * DEFAULT_CARD_HEIGHT; i++) {
			for (int j = -DEFAULT_CARD_WIDTH / 2; j <= columns * DEFAULT_CARD_WIDTH; j++) {
				if (i < 0 || j < 0) {
					// fill in empty spaces where numbers will go
					boardString.append(EMPTY_CHAR);
				} else if (i % DEFAULT_CARD_HEIGHT == 0 && j % DEFAULT_CARD_WIDTH == 0) {
					// fill in intersections
					boardString.append(INTERSECTION_SYMBOL);
				} else if (i % DEFAULT_CARD_HEIGHT == 0) {
					// fill in row separators
					boardString.append(HORIZONTAL_SEPARATOR_SYMBOL);
				} else if (j % DEFAULT_CARD_WIDTH == 0) {
					// fill in column separators
					boardString.append(VERTICAL_SEPARATOR_SYMBOL);
				} else {
					// fill in empty spaces within cards
					boardString.append(EMPTY_CHAR);
				}
			}
			boardString.append('\n');
		}
	}

	/*
	 * Adds Card names to the board
	 */
	private void addCardNames() {
		// loops through board, adding card names
		for (int i = DEFAULT_CARD_WIDTH, c = 0; i < boardWidth; i += DEFAULT_CARD_WIDTH, c++) {
			for (int j = DEFAULT_CARD_HEIGHT, r = 0; j < boardHeight; j += DEFAULT_CARD_HEIGHT, r++) {
				// the current card
				Card card = cards[r][c];

				// the original name of the card
				String name = card.getName();
				// shorten name too fit in card, if necessary
				name = name.substring(0, Math.min(name.length(), DEFAULT_CARD_WIDTH - 1));

				// reset name if shortened
				if (card.getName().length() != name.length()) {
					card.setName(name);
				}
			}
		}
	}

	/*
	 * Adds column numbering to the board
	 */
	private void addColumnNumbers() {
		// go through top row, adding numbers above the center of each column
		for (int i = (boardWidth * (DEFAULT_CARD_HEIGHT / 4))
				+ DEFAULT_CARD_WIDTH, j = 0; j < columns; i += DEFAULT_CARD_WIDTH, j++) {
			String label = Integer.toString(j);

			// store the index of the center of the numbering column, taking into account
			// the length of the number
			int start = i - label.length() / 2;
			int end = start + label.length();

			boardString.replace(start, end, label);
		}
	}

	/*
	 * Adds row numbering to the board
	 */
	private void addRowNumbers() {
		// go down the rows, adding numbers to the left of the center of each row
		for (int i = DEFAULT_CARD_HEIGHT, j = 0; i < boardHeight; i += DEFAULT_CARD_HEIGHT, j++) {
			String label = Integer.toString(j);

			// store the index of the center of the numbering row, taking into account the
			// length of the number
			int start = (i * boardWidth + DEFAULT_CARD_WIDTH / 4) - label.length() / 2;
			int end = start + label.length();

			boardString.replace(start, end, label);
		}
	}

	/**
	 * Updates the state of the cards in the board.
	 */
	public void update() {
		// make sure board has been initialized
		checkForInitialization();

		// loop through board
		for (int c = 0; c < columns; c++) {
			for (int r = 0; r < rows; r++) {
				Card card = cards[r][c];
				// the index of center of the card in boardString
				int index = ((r + 1) * DEFAULT_CARD_HEIGHT) * boardWidth + ((c + 1) * DEFAULT_CARD_WIDTH);

				if (card.isFlipped()) {
					// show the name
					replaceName(index, card.getName(), true);
				} else if (card.isPaired()) {
					// if card is paired, remove it from board by filling in the inside of the card
					fillInCard(index);
				} else {
					// hide the name
					replaceName(index, card.getName(), false);
				}
			}
		}
	}

	/*
	 * Replaces a part of the board string, either showing or hiding a name
	 */
	private void replaceName(int index, String name, boolean show) {
		// the index of the name of the card in boardString
		int nameIndex = index - name.length() / 2;
		int endingNameIndex = nameIndex + name.length();

		if (show) {
			// show name on the board
			boardString.replace(nameIndex, endingNameIndex, name);
		} else {
			// removes name from board
			for (int i = nameIndex; i < endingNameIndex; i++) {
				boardString.setCharAt(i, EMPTY_CHAR);
			}
		}
	}

	/*
	 * Fills in a card in the board string
	 */
	private void fillInCard(int index) {
		// the index of the top left of the card
		int cardIndex = index - (boardWidth * (DEFAULT_CARD_HEIGHT / 2)) - DEFAULT_CARD_WIDTH / 2;

		// the location of the top left inside corner of the card
		int innerCardIndex = cardIndex + boardWidth + 1;

		// fill in all rows of the card
		for (int i = 0; i < DEFAULT_CARD_HEIGHT - 1; i++) {

			// the location of the beginning and end of the inside of the card in
			// boardString
			int startingInnerCardIndex = innerCardIndex + (boardWidth * i);
			int endingInnerCardIndex = startingInnerCardIndex + DEFAULT_CARD_WIDTH - 1;

			// fill in inside row of card
			for (int j = startingInnerCardIndex; j < endingInnerCardIndex; j++) {
				boardString.setCharAt(j, REMOVED_SYMBOL);
			}
		}
	}

	/*
	 * Determines if the board parameters are valid, throws IllegalStateException if
	 * they aren't
	 */
	private void checkBoardParameters() {
		// check board dimensions
		if (columns < 0 || rows < 0 || columns * rows < MIN_BOARD_SIZE || columns * rows % 2 != 0) {
			throw new IllegalStateException(
					"The number of rows and columns must be positive, and their product must an even number.");
		}

		// check names
		if (names.length == 0) {
			throw new IllegalStateException("The number of names must be greater than 0.");
		}
	}

	/*
	 * Determines if board has been initialized, throws IllegalStateException if it
	 * hasn't.
	 */
	private void checkForInitialization() {
		if (!initialized) {
			throw new IllegalStateException("Board must be initialized with setup() before invoking this method");
		}
	}

	/**
	 * Gets the card at the given position
	 * 
	 * @param row    the row index of the card
	 * @param column the column index of the card
	 * @return the Card at the given location, null if there is no card
	 */
	public Card getCard(int row, int column) {
		if (isValidPosition(row, column)) {
			return cards[row][column];
		}
		return null;
	}

	/**
	 * Hides all unmatched cards.
	 */
	public void hideCards() {
		for (Card[] row : cards) {
			for (Card card : row) {
				if (card.isFlipped()) {
					card.setFlipped(false);
				}
			}
		}
	}

	/*
	 * Determines if the given position corresponds to a valid card index
	 */
	private boolean isValidPosition(int row, int column) {
		return (column >= 0 && column < columns) && (row >= 0 && row < rows);
	}

	/**
	 * Gets the number of possible matches
	 * 
	 * @return the number of possible matches
	 */
	public int getPossibleMatches() {
		return possibleMatches;
	}

	/**
	 * Gets the number of rows
	 * 
	 * @return the number of rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Gets the number of columns
	 * 
	 * @return the number of columns
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * Returns the String representation of the board. The String will be empty if
	 * the board has not been initialized
	 * 
	 * @return String representation of board
	 */
	public String toString() {
		return boardString.toString();
	}
}