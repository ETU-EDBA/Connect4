public class ComputerConnect4Player extends Player {
	private int depth;  // depth to search at
	public static boolean hChoice=false; //heuristic choice

	// my weights
	// need to be public/package for testing static evaluation func in Connect4Game.java

	// the closer a piece is to the center, the more 4-in-row permutations available.
	// i.e.., generally center piece is most valuable
	private static final int[] movesByCol = { 3, 4, 2, 5, 1, 6, 0 };
	private static final int WEIGTH_OF_THREAT = 10^3;
	private static final int WEIGTH_OF_WINNING = 10^5;

	/**
	 * Create a computer player with a given name
	 * @param name name of computer player
	 */
	public ComputerConnect4Player(String name, int depth){
		super(name);
		this.depth = depth;
	}

	@Override
	public int getMove(Connect4State state, Connect4View view) {
		// First copy the game instance
		Connect4Game stateCopy = new Connect4Game(state.getPlayerNum(), state.getPlayers(), state.getBoard(), evaluateBoard(state), movesDone(state));

		// pick the move
		// start alpha-beta with neg and pos infinities
		Connect4Move chosenMoveObj = pickMove(stateCopy, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE, view);
		int chosenMove = chosenMoveObj.move;

		//view.reportMove(chosenMove, state.getPlayerToMove().getName());

		return chosenMove;
	}

	/**
	 * Uses game tree search with alpha-beta pruning to pick player's move
	 * low and high define the current range for the best move
	 *
	 * @param state the current state of the game
	 * @param depth the number of moves to look ahead in game tree search
	 * @param low a value that the player can achieve by some other move
	 * @param high a value that the opponent can force by a different line of play
	 * @param view view for testing purposes
	 *
	 * @return the move chosen
	 */
	private  Connect4Move pickMove(Connect4Game state, int depth, int low, int high, Connect4View view){
		Connect4Move[] movesArray; // order of moves
		// grab the available moves, sorted by value
		movesArray = checkMoves(state);
		// dummy move that will be replaced with evaluation
		Connect4Move bestMove = new Connect4Move(-Integer.MAX_VALUE, movesArray[0].move);
		// Use alpha-beta pruning to pick the move
		for (int i = 0; i < 7 && bestMove.value < high; i++){
			// grab the move from list
			int column = movesArray[i].move;
			if (state.isValidMove(column)){
				Connect4Move currentMove;
				// grab value of current position to restore later
				int evalValue = state.grabEvalValue();

				state.makeMove(column);
				if (canPrint){
					System.out.println("===============");
					System.out.println("Position Eval # :" + evaluateBoard(state));
					System.out.println("===============");
				}

				if (state.gameIsOver()){
					// Is game over because board is full?
					if (state.isFull()){
						currentMove = new Connect4Move(0, column); // assign value of 0
					}

					// if it's comp's turn, then this must be a win scenario
					currentMove = new Connect4Move(WEIGTH_OF_WINNING, column);
				}
				// keep going if depth available
				else if (depth >= 1){
					// Switch player perspective
					// Reduce depth by 1
					currentMove = pickMove(state, depth - 1, -high, -low, view);
					// transfer values back while changing perspective
					currentMove.value = (currentMove.value * -1);
					currentMove.move = column;
				} else {
					currentMove = new Connect4Move(state.grabEvalValue(), column);
				}
				// Is the current move better than what we've found so far?
				if (currentMove.value > bestMove.value){
					bestMove = currentMove; // replace
					low = Math.max(bestMove.value, low); // update the achievable lower bound value
				}
				// undo move before trying next move
				state.undoMove(column, evalValue);
			}
		}
		return bestMove;
	}


	/**
	 * Check the move list for their associated values
	 * Then sort them by value
	 *
	 * @param state the current state of the game
	 * @return an array of moves sorted by their values
	 */
	private static Connect4Move[] checkMoves(Connect4Game state){
		int stateEval; // evaluation of current state based on unblocked 4 in rows
		Connect4Move[] movesArray = new Connect4Move[Connect4Game.COLS];

		stateEval = state.grabEvalValue();

		// go through each column in move list
		for (int i = 0; i < Connect4Game.COLS; i++){
			int theMove = movesByCol[i];

			movesArray[i] = new Connect4Move(-Integer.MAX_VALUE, theMove);
			if (state.isValidMove(theMove)){
				// try the move
				state.makeMove(theMove);

				// now evaluate the new state and store value to check against later
				movesArray[i].value = state.grabEvalValue();

				// undo the state before checking again
				state.undoMove(theMove, stateEval);
			}
		}

		// sort the move lists by values
		for (int i = 1; i < Connect4Game.COLS; i++){
			for (int compare = i; (compare >=1 && movesArray[compare].value >
			movesArray[compare - 1].value);
					compare--){
				// placeholder to prevent clobbering
				Connect4Move placeholder = movesArray[compare];
				movesArray[compare] = movesArray[compare - 1];
				movesArray[compare - 1] = placeholder;
			}

		}

		// new set of moves with updated values
		return movesArray;
	}

	/**
	 * Helper method that counts the moves made
	 *
	 * @param state the input state of the board
	 * @return the number of moves already made
	 */
	private static int movesDone(Connect4State state){
		// count the pieces

		int counter = 0;
		for (int row = 0; row < Connect4Game.ROWS; row++){
			for (int column = 0; column < Connect4Game.COLS; column++){
				if (state.getBoard()[row][column] != Connect4Game.EMPTY) counter++;
			}
		}

		return counter;
	}


		/**
		 * Evalueates the current board
		 *
		 * @return a new evaluation value
		 */
		public static int evaluateBoard(Connect4State state){
			// grab the players
			char opponent = Connect4State.CHECKERS[(1 - state.getPlayerNum())];
			char mainPlayer = Connect4State.CHECKERS[state.getPlayerNum()];

			int horizValue=0;
			int vertValue=0;
			int diagPValue=0;
			int diagNValue=0;

			for(int i=0;i<Connect4State.ROWS-3;i++){
				for(int j=0;j<Connect4State.COLS;j++){
					vertValue += checkPos(state, mainPlayer, opponent, i, i+3, j, j, false);
				}
			}

			for(int i=0;i<Connect4State.ROWS;i++){
				for(int j=0;j<Connect4State.COLS-3;j++){
					horizValue += checkPos(state, mainPlayer, opponent, i, i, j, j+3, false);
				}
			}
			for(int i=0;i<Connect4State.ROWS-3;i++){
				for(int j=0;j<Connect4State.COLS-3;j++){
					diagPValue += checkPos(state, mainPlayer, opponent, i, i+3, j, j+3, false);
					diagNValue += checkPos(state, mainPlayer, opponent, i+3, i, j, j+3, true);
				}
			}
			// now return the total value of horizontal, vertical and diagonals
			int sum = horizValue + vertValue + diagPValue + diagNValue;

			return sum;
		}

		/**
		 * Evaluates the possibilities for diagonal and horizontal connect fours
		 *
		 * @param state state
		 * @param mainPlayer the main player
		 * @param opponent the other player
		 * @param i1 column left bound
		 * @param i2 column right bound
		 * @param j1 row left bound
		 * @param j2 row right bound
		 * @param horizMode horizontal mode switch
		 * @return the weigth value for these pieces
		 */
		private static int checkPos(Connect4State state, char mainPlayer, char opponent, int i1, int i2, int j1, int j2, boolean horizMode){
			int opponentCount = 0;
			int playerCount = 0;
			char[][] board= state.getBoard();
			if(horizMode==true){
				int j=j1;
				for(int i=i1;i>=i2 && j<=j2; i--, j++){

					if (board[i][j] == opponent){
						opponentCount++;
					} else if (board[i][j] == mainPlayer){
						playerCount++;
					}

				}
			}
			else{
				int j=j1;
				for(int i=i1;i<=i2 && j<=j2; i++,j++){

					if (board[i][j] == opponent){
						opponentCount++;
					} else if (board[i][j] == mainPlayer){
						playerCount++;
					}

				}
			}
			return applyWeights(playerCount, opponentCount);
		}



			/**
			 * Public helper method to apply weights after looking at Connect 4
			 * possibilities
			 *
			 * @param playerCount the number of pieces player has in the connect 4 line
			 * @param opponentCount the number of pieces opponent has in the connect 4 line
			 * @param sum the weighted sum so far
			 * @return the new sum after applying the weights.
			 */
			public static int applyWeights(int playerCount, int opponentCount){
				// apply the weights based on the previous connect 4 possibilities
				int sum=0;

				if(hChoice){
					if (playerCount == 0 && opponentCount == 3){
						sum += WEIGTH_OF_THREAT;
					}
					else if (opponentCount == 0 && playerCount ==3) {
						sum += WEIGTH_OF_WINNING;
					}
				}

				if (opponentCount == 0) {
					sum ++;
				}
				return sum;
			}


}
