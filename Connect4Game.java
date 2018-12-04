import java.util.Arrays;
import java.util.concurrent.TimeUnit;
/**
 * Connect4Game.java
 *
 * Represents the state of the Connect 4 game.
 *
 * @author Delos Chang
 *
 */

public class Connect4Game implements Connect4State{
	private char[][] board;
	private Player [] players;
	private int playerToMoveNum; // 0 or 1 for which player to go

	private int latestRow = -1; // latest row added to by makeMove method
	private int latestCol = -1; // latest column added to by makeMove method

	private int movesDone; // number of moves made

	private int evalValue; // evaluation of unblocked four-in-row for both players


	/**
	 * Constructs game in initial state
	 *
	 * @param playerNum the player whose move it is
	 * @param thePlayers the player objects
	 * @param aView the view in the model-view-controller model
	 */
	public Connect4Game(int playerNum, Player [] thePlayers){
		board = new char[ROWS][COLS];

		// fill board with empty slots
		for (char[] row : board){
			Arrays.fill(row, EMPTY);
		}

		playerToMoveNum = playerNum;
		players = thePlayers;

		movesDone = 0;
		evalValue = 0;
	}

	/**
	 * Construct the game with input states
	 *
	 * @param playerNum the player whose move it is
	 * @param thePlayers the player objects
	 * @param initialBoard the board input with requisite pieces
	 * @param movesMade the number of moves already made
	 * @param unblockedTotal a total int evaluation of unblocked four-in-row for opp and computer
	 */
	public Connect4Game(int playerNum, Player[] thePlayers, char[][] initialBoard, int movesMade, int unblockedTotal){
		// Initialize board with rows and columns
		board = new char[ROWS][COLS];

		// Replicate the board
		for (int row = 0; row < ROWS; row++){
			for (int column = 0; column < COLS; column++){
				board[row][column] = initialBoard[row][column];
			}
		}

		// Replicate the players and moves made etc.
		playerToMoveNum = playerNum;
		players = thePlayers;

		// Replicate the evaluation value
		movesDone = movesMade;
		evalValue = unblockedTotal;
	}


	@Override
	/**
	 * Gets a 2-D array representing the board.
	 * The first subscript is the row number and the second the column number.
	 * The bottom of the board is row 0 and the top is row ROWS-1.
	 * The left side of the board is column 0 and the right side is column COLS-1.
	 *
	 * @return the board
	 */
	public char[][] getBoard() {
		return board;
	}

	@Override
	public Player[] getPlayers() {
		return players;
	}

	@Override
	public int getPlayerNum() {
		return playerToMoveNum;
	}

	/**
	 * Gets number of moves played
	 * @return number of moves played so far
	 */
	public int getMovesPlayed(){
		return movesDone;
	}

	/**
	 * Returns the evaluation value for a given position
	 * @return the evaluation value
	 */
	public int grabEvalValue(){
		return evalValue;
	}

	@Override
	public Player getPlayerToMove() {
		return players[playerToMoveNum];
	}

	@Override
	public boolean isValidMove(int col) {
		// move is valid if the top column isn't full
		if (col >= 0 && col < 7){
			return !isColumnFull(col);
		}else{
			return false;
		}

	}

	/**
	 * Make a move, dropping a checker in the given column
	 * @param col the column to get the new checker
	 */
	@Override
	public void makeMove(int col) {
		// first check if the move is valid
		if (isValidMove(col)){
			int openRow = findOpenRow(col);

			// Switch player
			playerToMoveNum = 1 - playerToMoveNum;

			// Switch evaluation for player and computer
			evalValue = -1 * evalValue;

			// Evaluation steps

			board[openRow][col] = CHECKERS[getPlayerNum()]; // add the checker
			evalValue = ComputerConnect4Player.evaluateBoard(this); // reevaluate with new piece in place

			// Increment moves done
			movesDone++;

			// Update latest row/cols
			latestRow = openRow;
			latestCol = col;
		} else {
			// because it was not a valid move
			throw new IllegalStateException("Column is full!");
		}
	}

	/**
	 * Find the first empty row in a column
	 * -1 if the column is full (no empty row)
	 *
	 * @param col the column to check
	 */
	private int findOpenRow(int col){
		// find the first row that isn't filled
		for (int i = 0; i < ROWS; i++){
			if (board[i][col] == EMPTY){
				return i;
			}
		}

		return -1;
	}

	/**
	 * Finds the first occupied slot of a column
	 *
	 * @param col the column to check
	 * @return the first occupied slot of a column
	 */
	private int findTop(int col){
		// find the top of the closed row
		int row = ROWS - 1;

		while (board[row][col] == EMPTY && row > 0){
			row--;
		}

		return row;

	}

	/**
	 * Undo the move to avoid creating a new state each time
	 *
	 * @param column column to undo
	 * @param stateEval static evaluation at that time
	 */
	public void undoMove(int column, int stateEval){
		int row = this.findTop(column);

		// change back to empty
		board[row][column] = EMPTY;

		// change other parameters to original
		playerToMoveNum = 1 - playerToMoveNum;

		evalValue = stateEval;
		movesDone--;
	}

	/**
	 * Is column full?
	 *
	 * @param col the column to check
	 * @return true if the column is full
	 */
	private boolean isColumnFull(int col) {
		return !(board[ROWS - 1][col] == EMPTY);
	}

	/**
	 * Is the board full?
	 * @return true if the board is full
	 */
	@Override
	public boolean isFull() {
		return (movesDone == ROWS * COLS);
	}


	/**
	 * Evaluates four-in-row for game-over method
	 *
	 * @param row latest row that a move was made on
	 * @param column latest column that a move was made on
	 * @param rowOffset a row offset to calculate different connect 4 possibilities
	 * @param colOffset a row offset to calculate different connect 4 possibilities
	 * @return boolean true if there is a win
	 */
	private boolean checkForFour(int row, int column,
			int rowOffset, int colOffset){

		int winCounter = 0; // counts to 4 for win

		// Find opp ends for the possible Connect 4
		int oppRow = 3 * rowOffset + row;
		int oppColumn = 3 * colOffset + column;

		// conditions where Connect 4 is impossible
		// less than 7 moves (counting both players)
		// adjusted offset for row/col is < 0 or > maximum
		if ( (movesDone < 7 ) || (oppRow >= ROWS) || (oppColumn >= COLS) ||
				(oppRow < 0) || (oppColumn < 0) ||
				(row < 0) || (column < 0) ||
				(row >= ROWS) || (column >= COLS)){
			return false;
		}

		for (int i = 1; i < 5; i++){
			if (board[row][column] == CHECKERS[playerToMoveNum]){

				winCounter++;
			}

			// Adjust offsets and look for the next piece
			// that would lead to a four-in-row.
			row += rowOffset;
			column += colOffset;
		}

		// Got a connect 4!
		return (winCounter == 4);
	}

	/**
	 * Decides if game is over
	 * @return true iff the game is over
	 */
	@Override
	public boolean gameIsOver() {
		// Check if game is complete
		if ( isFull() ){
			return true;
		}

		// Check vertical four-in-row
		if ( checkForFour(latestRow, latestCol, -1, 0)) return true;

		for (int offset = 0; offset < 4; offset++){
			// Check horizontal four-in-row
			if ( checkForFour(latestRow, latestCol - offset, 0, 1)) return true;

			// Check diagonal via lower right
			if ( checkForFour(latestRow - offset, latestCol + offset, 1, -1)) return true;

			// Check diagonal via upper right
			if ( checkForFour(latestRow - offset, latestCol - offset, 1, 1)) return true;
		}

		return false;
	}

	/**
	 * Test function to check the static evaluation function
	 * @param args
	 */
	public static void main(String[] args){
		/*
		Player[] players = new Player[2];
		players[0] = new Connect4HumanPlayer("Test1");
		players[1] = new Connect4HumanPlayer("Test2");

		// Initialize test game
		Connect4Game gameOne = new Connect4Game(0, players);
		Connect4Game gameTwo = new Connect4Game(0, players);
		Connect4View view = new Connect4ViewGraphical();

		while (!gameOne.gameIsOver()){
			int column = gameTwo.getPlayerToMove().getMove(gameTwo, view);
			gameTwo.makeMove(column);

			int evaluation = gameOne.grabEvalValue();
			gameOne.makeMove(column); // make the same move

			gameOne.undoMove(column, evaluation);
			gameOne.makeMove(column);

			int new_eval = gameOne.grabEvalValue();
			int compEval = ComputerConnect4Player.evaluate(gameTwo);

			System.out.println("The following evaluation numbers in the game should match.");
			System.out.println("Player One " + new_eval);
			System.out.println("Player Two " + compEval);
			view.display(gameOne);


		}
		*/
	}

}
