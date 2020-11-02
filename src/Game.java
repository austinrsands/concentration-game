import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the game of concentration, a card matching game.
 * 
 * The game starts by placing a set of cards face down on a playing field.
 * During each turn, two cards are flipped face up. If they match, then they are
 * removed from the field. If they do not match, then they are placed face down
 * again and the player gets another turn. The goal of the game is to match all
 * of the cards in the least number of moves.
 * 
 * @author Austin Sands
 */
public class Game {

	// the player
	private Player player;

	// the instance of the board
	private Board board;

	// scanner used for getting user input
	private Scanner scanner;

	// Messages to user, common among all instances of the class
	private static final String EXIT_KEYWORD = "quit";
	private static final String INPUT_INSTRUCTIONS = "Enter the row-column pair of two cards to flip, or type \""
			+ EXIT_KEYWORD + "\" to end the game.";
	private static String INPUT_EXAMPLE = "Example: \"(0, 0) (0, 1)\" or \"0 0 0 1\"";
	private static final String INPUT_SUGGESTION = "Please choose two different, available cards as an ordered pair.";
	private static final String MATCH_MADE = "You made a match! This pair will be removed.";
	private static final String NO_MATCH = "No match found. Please try again or type \"" + EXIT_KEYWORD + "\" to exit.";
	private static final String GAME_OVER_MESSAGE = "Game Over.";
	private static final String GAME_WON_MESSAGE = "You Won!";
	private static final String AFFIRMATIVE_RESPONSE = "yes";
	private static final String NEGATIVE_RESPONSE = "no";
	private static final String REPLAY_PROMPT = "Would you like to play again? \"" + AFFIRMATIVE_RESPONSE + "\" or \""
			+ NEGATIVE_RESPONSE + "\"";
	private static final String INPUT_PROMPT = "Input: ";
	private static final String ANSWER_PROMPT = "Anwser: ";
	private static final String ALREADY_PAIRED_MESSAGE = "One or more of the given cards has already been paired.";
	private static final String OUT_OF_BOUNDS_MESSAGE = "The given positions aren't on the board.";
	private static final String DUPLICATE_CARD_MESSAGE = "The given positions must be different.";
	private static final String INVALID_INPUT_MESSAGE = "Invalid input!";

	// regular expression for getting safe input, matches integers
	private static final String INPUT_PATTERN = "[0-9][0-9]*";
	private Pattern inputPattern = Pattern.compile(INPUT_PATTERN);
	private Matcher inputMatcher;

	// the number of values to be inputed
	private static final int INPUT_NUMS_SIZE = 4;

	// whether a match has been found
	private boolean matchFound;

	// whether a move is valid
	private boolean validMove;

	/**
	 * Constructs a Game with the given columns, rows, and names.
	 * 
	 * @param columns number of columns
	 * @param rows    number of rows
	 * @param names   names to appear on cards
	 */
	public Game(int columns, int rows, String[] names) {
		board = new Board(columns, rows, names);
		player = new Player();
	}

	/**
	 * Constructs a Game with the given columns, rows, and names.
	 * 
	 * @param columns number of columns
	 * @param rows    number of rows
	 * @param names   names to appear on cards
	 */
	public Game(int columns, int rows) {
		board = new Board(columns, rows);
		player = new Player();
	}

	/**
	 * Constructs a Game with the given names.
	 * 
	 * @param names possible names to appear on the cards
	 */
	public Game(String[] names) {
		board = new Board(names);
		player = new Player();
	}

	/**
	 * Constructs a Game with default properties.
	 */
	public Game() {
		board = new Board();
		player = new Player();
	}

	/**
	 * Begins the matching game.
	 */
	public void start() {
		board.setup();
		resetGameState();
		enterGameLoop();
	}

	/*
	 * Resets the number of moves, matches, and whether match is found
	 */
	private void resetGameState() {
		player.reset();
		matchFound = false;
	}

	/*
	 * Runs throughout lifetime of game, gets input from user
	 */
	private void enterGameLoop() {
		// initialize scanner for input
		scanner = new Scanner(System.in);

		// loop while game is not over
		while (player.getMatches() != board.getPossibleMatches()) {

			// display the board
			System.out.println(board);

			// Tell user if they made match or need instruction
			printMatchMessage();

			// get input
			handleInput();
		}

		// game is won
		endGame(true);
	}

	/*
	 * Prompts the user for input and flips cards accordingly
	 */
	private void handleInput() {
		validMove = false;

		// Prompt the user for input until it is valid
		while (!validMove) {
			System.out.print(INPUT_PROMPT);

			// get input
			String input = scanner.nextLine();

			// if user quits, end the game
			if (input.toLowerCase().equals(EXIT_KEYWORD.toLowerCase())) {
				// prepare the board and quit the game
				board.hideCards();
				board.update();
				endGame(false);

				// break out of the loop
				break;

			} else {
				// create array to hold input values
				int[] sanitizedInput = getSanitizedInput(input);

				// flip the cards with the given input, if possible
				flipCards(sanitizedInput);
			}
		}
	}

	/*
	 * Attempts to return array of sanitized input, returns null if input could not
	 * be sanitized.
	 */
	private int[] getSanitizedInput(String input) {
		int[] inputNums = new int[INPUT_NUMS_SIZE];
		// prepare input for pattern matching
		inputMatcher = inputPattern.matcher(input);

		// try to get as many numbers as needed
		for (int i = 0; i < inputNums.length; i++) {
			// if a number is found, then try to parse it and add it to inputNums
			if (inputMatcher.find()) {
				// If number cannot be parsed, it will throw a NumberFormatException
				try {
					inputNums[i] = Integer.parseInt(inputMatcher.group());
				} catch (NumberFormatException e) {
					return null;
				}
			} else {
				return null;
			}
		}
		return inputNums;
	}

	/*
	 * Determines if the given input numbers correspond to valid and flippable
	 * cards.
	 */
	private boolean inputIsValid(int[] inputNums) {
		if (inputNums == null || inputNums.length != INPUT_NUMS_SIZE) {
			// input cannot be sanitized
			printInputError(INVALID_INPUT_MESSAGE);
			return false;
		} else {
			Card card1 = board.getCard(inputNums[0], inputNums[1]);
			Card card2 = board.getCard(inputNums[2], inputNums[3]);

			// if cards aren't the same
			if (inputNums[0] == inputNums[2] && inputNums[1] == inputNums[3]) {
				// at least one card is already paired
				printInputError(DUPLICATE_CARD_MESSAGE);
				return false;
			} else if (card1 == null || card2 == null) {
				// positions not on board
				printInputError(OUT_OF_BOUNDS_MESSAGE);
				return false;
			} else if (card1.isPaired() || card2.isPaired()) {
				// positions are the same
				printInputError(ALREADY_PAIRED_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/*
	 * Flips the given cards, if possible
	 */
	private void flipCards(int[] inputNums) {
		if (inputIsValid(inputNums)) {
			Card card1 = board.getCard(inputNums[0], inputNums[1]);
			Card card2 = board.getCard(inputNums[2], inputNums[3]);

			// hide previously flipped cards
			board.hideCards();

			// flip given cards
			card1.setFlipped(true);
			card2.setFlipped(true);
			board.update();

			// display message
			System.out.printf("\nFlipping cards at (%d, %d) and (%d, %d)...\n", inputNums[0], inputNums[1],
					inputNums[2], inputNums[3]);

			// if cards match
			if (card1.equals(card2)) {
				// pair cards
				card1.setPaired(true);
				card2.setPaired(true);

				// increment matches
				player.addMatch();
				matchFound = true;
			}

			// increment moves
			player.addMove();
			validMove = true;
		}
	}

	/*
	 * Prints an input error with the specified messsage
	 */
	private void printInputError(String message) {
		System.out.printf("\n%s\n%s\n%s\n\n", message, INPUT_SUGGESTION, INPUT_EXAMPLE);
	}

	/*
	 * Tells user if match was made or shows instructions
	 */
	private void printMatchMessage() {
		// tell user if they made a match or not
		if (matchFound) {
			System.out.println(MATCH_MADE);
			matchFound = false;
		} else if (player.getMoves() > 0) {
			System.out.println(NO_MATCH);
		}

		// print instructions on how to play game
		if (player.getMoves() == 0) {
			System.out.printf("%s\n%s\n", INPUT_INSTRUCTIONS, INPUT_EXAMPLE);
		}
	}

	/*
	 * Called after game is won or quit. Prompts the user to play again
	 */
	private void endGame(boolean won) {

		// display the board one last time
		System.out.println(board);

		// grammar check
		String moveString = player.getMoves() == 1 ? "move" : "moves";
		String matchString = player.getMatches() == 1 ? "match" : "matches";

		// print ending message
		System.out.println(won ? GAME_WON_MESSAGE : GAME_OVER_MESSAGE);
		System.out.printf("You made %d %s and %d %s.\n", player.getMoves(), moveString, player.getMatches(),
				matchString);

		// ask to play again. If yes, restart game. If no, quit.
		if (won) {
			askForReplay();
		} else {
			// player chose to quit, exit the program
			exitGame();
		}
	}

	/*
	 * Asks the user to play again or quit
	 */
	private void askForReplay() {
		System.out.println(REPLAY_PROMPT);
		String answer = "";
		while (!(answer.equals(AFFIRMATIVE_RESPONSE.toLowerCase()) || answer.equals(NEGATIVE_RESPONSE.toLowerCase()))) {
			System.out.print(ANSWER_PROMPT);
			answer = scanner.nextLine().toLowerCase();
		}

		// If yes, start again
		if (answer.equals(AFFIRMATIVE_RESPONSE)) {
			start();
		} else {
			// exit the program
			exitGame();
		}
	}

	/*
	 * exits the game
	 */
	private void exitGame() {
		scanner.close();
		System.exit(0);
	}
}
