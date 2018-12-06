public class ComputerConnect4Player extends Player {
	private int depth;  // depth degeri
	public static boolean hChoice=false; //heuristic secimi

	private static final int[] movesByCol = { 3, 4, 2, 5, 1, 6, 0 };
	private static final int WEIGTH_OF_THREAT = 1000;
	private static final int WEIGTH_OF_WINNING = 100000;

	/**
	 * Verilmis isim ile bir computer player olusturulur.
	 * @param name computer ismi
	 */
	public ComputerConnect4Player(String name, int depth){
		super(name);
		this.depth = depth;
	}

	@Override
	public int getMove(Connect4State state, Connect4View view) {
		// Game instance'dan yeni bir state kopyasi olustur.
		Connect4Game stateCopy = new Connect4Game(state.getPlayerNum(), state.getPlayers(), state.getBoard(), evaluateBoard(state), movesDone(state));

		// alpha-beta pruningli minimaxa negatif ve pozitif sonsuz sinirlarindan baslatir.
		Connect4Move chosenMoveObj = pickMove(stateCopy, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE, view);
		int chosenMove = chosenMoveObj.move;

		return chosenMove;
	}

	/**
	 * Game agacini alpha-beta pruningli minimax algoritmasi kullanarak arar ve secilmis olunan move'i doner.
	 *
	 * @param state mevcut oyun state'i
	 * @param depth agac derinligi
	 * @param low minimax algoritmasi lower boundu
	 * @param high minimax algoritmasi higher boundu
	 *
	 * @return secilmis move
	 */
	private  Connect4Move pickMove(Connect4Game state, int depth, int low, int high, Connect4View view){
		Connect4Move[] movesArray;
		movesArray = checkMoves(state);
		Connect4Move bestMove = new Connect4Move(-Integer.MAX_VALUE, movesArray[0].move);
		// Alpha beta pruningli minimax algoritmasi kullanilarak hamle secilir.
		for (int i = 0; i < 7 && bestMove.value < high; i++){
			// oncelikli move listesinden bir move secer.
			int column = movesArray[i].move;
			if (state.isValidMove(column)){
				Connect4Move currentMove;
				int evalValue = state.grabEvalValue();

				state.makeMove(column);
				if (canPrint){
					System.out.println("Position Eval # :" + evaluateBoard(state));
				}

				if (state.gameIsOver()){
					// Oyun grid tamamen doldugu icin mi bitti?
					if (state.isFull()){
						currentMove = new Connect4Move(0, column); // skor olarak 0 yani beraberlik skorunu koyariz.
					}

					// Move yaptiktan sonra game over olduguna gore kazanma senaryomuz gerceklesti
					currentMove = new Connect4Move(WEIGTH_OF_WINNING, column);
				}
				// daha acilmamis depth var ise acmaya devam edelim
				else if (depth >= 1){
					// Minimax algoritmasinda simdi sira karsi kullaniciya gectigi icin perspektifi degistiririz.
					// ve ayrica depth'i bir azaltiriz
					currentMove = pickMove(state, depth - 1, -high, -low, view);
					currentMove.value = (currentMove.value * -1);
					currentMove.move = column;
				} else {
					currentMove = new Connect4Move(state.grabEvalValue(), column);
				}
				// Mevcut hamle eger best hamlemizden iyi ise best hamlemizi guncelleriz.
				if (currentMove.value > bestMove.value){
					bestMove = currentMove;
					low = Math.max(bestMove.value, low); // Ve ayrica elde edilebilecek lower boundu guncelleriz.
				}
				// Bir sonraki hamleyi yapmadan once mevcut hamleyi undo yapariz.
				state.undoMove(column, evalValue);
			}
		}
		return bestMove;
	}


	/**
	 *
	 * @param state mevcut oyun durumu
	 * @return skoru en cokdan aza dogru siralanmis hamle arrayi
	 */
	private static Connect4Move[] checkMoves(Connect4Game state){
		int stateEval;
		Connect4Move[] movesArray = new Connect4Move[Connect4Game.COLS];

		stateEval = state.grabEvalValue();

		for (int i = 0; i < Connect4Game.COLS; i++){
			int theMove = movesByCol[i];

			movesArray[i] = new Connect4Move(-Integer.MAX_VALUE, theMove);
			if (state.isValidMove(theMove)){
				state.makeMove(theMove);
				movesArray[i].value = state.grabEvalValue();
				state.undoMove(theMove, stateEval);
			}
		}

		// hamle listesi sort edilir.
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

		return movesArray;
	}

	/**
	 * Suana kadar yapilmis hamle sayisini olcer.
	 *
	 * @param state grid state'i
	 * @return suana kadar yapilmis hamle sayisi
	 */
	private static int movesDone(Connect4State state){

		int counter = 0;
		for (int row = 0; row < Connect4Game.ROWS; row++){
			for (int column = 0; column < Connect4Game.COLS; column++){
				if (state.getBoard()[row][column] != Connect4Game.EMPTY) counter++;
			}
		}

		return counter;
	}


		/**
		 * Mevcut grid skorumuzu heuristigimize gore degerlendirir.
		 *
		 * @return yeni grid skoru.
		 */
		public static int evaluateBoard(Connect4State state){
			char opponent = Connect4State.CHECKERS[(1 - state.getPlayerNum())];
			char mainPlayer = Connect4State.CHECKERS[state.getPlayerNum()];

			int horizValue=0;
			int vertValue=0;
			int diagPValue=0;
			int diagNValue=0;
			int winLoseValue=0;

			for(int i=0;i<Connect4State.ROWS-3;i++){
				for(int j=0;j<Connect4State.COLS;j++){
					vertValue += checkPos(state, mainPlayer, opponent, i, i+3, j, j, false);
					if(hChoice){
						winLoseValue += checkThreat(state, mainPlayer, opponent, i, i+3, j, j, false);
					}
				}
			}

			for(int i=0;i<Connect4State.ROWS;i++){
				for(int j=0;j<Connect4State.COLS-3;j++){
					horizValue += checkPos(state, mainPlayer, opponent, i, i, j, j+3, false);
					if(hChoice){
						winLoseValue += checkThreat(state, mainPlayer, opponent, i, i, j, j+3, false);
					}
				}
			}
			for(int i=0;i<Connect4State.ROWS-3;i++){
				for(int j=0;j<Connect4State.COLS-3;j++){
					diagPValue += checkPos(state, mainPlayer, opponent, i, i+3, j, j+3, false);
					diagNValue += checkPos(state, mainPlayer, opponent, i+3, i, j, j+3, true);
					if(hChoice){
						winLoseValue += checkThreat(state, mainPlayer, opponent, i, i+3, j, j+3, false);
						winLoseValue += checkThreat(state, mainPlayer, opponent, i+3, i, j, j+3, true);
					}

				}
			}
			// now return the total value of horizontal, vertical and diagonals
			int sum = horizValue + vertValue + diagPValue + diagNValue+winLoseValue;
			return sum;
		}

		/**
		 * Olasi yatay, dikey veya capraz connect4 kontrol edebilen method
		 *
		 * @param state grid durum objesi
		 * @param mainPlayer mevcut oyuncu
		 * @param opponent rakip oyuncu
		 * @param i1 satir left bound
		 * @param i2 satir right bound
		 * @param j1 sutun left bound
		 * @param j2 sutun right bound
		 * @param horizMode horizontal mode switch degeri
		 * @return hesaplanilmis agirlik degeri
		 */
		private static int checkPos(Connect4State state, char mainPlayer, char opponent, int i1, int i2, int j1, int j2, boolean horizMode){
			int opponentCount = 0;
			int playerCount = 0;
			char[][] board= state.getBoard();
			if(horizMode==true){
				int j=j1;
				for(int i=i1;i>=i2 && j<=j2; i--){

					if (board[i][j] == opponent){
						opponentCount++;
					} else if (board[i][j] == mainPlayer){
						playerCount++;
					}
					j++;

				}
			}
			else{
				if(i1==i2){
					int i=i1;
					for(int j=j1;j<=j2;j++){
						if (board[i][j] == opponent){
							opponentCount++;
						} else if (board[i][j] == mainPlayer){
							playerCount++;
						}
					}
				}
				else if(j1==j2){
					int j=j1;
					for(int i=i1;i<=i2;i++){
						if (board[i][j] == opponent){
							opponentCount++;
						} else if (board[i][j] == mainPlayer){
							playerCount++;
						}
					}
				}
				else{
					int j=j1;
					for(int i=i1;i<=i2;i++){
						if (board[i][j] == opponent){
							opponentCount++;
						} else if (board[i][j] == mainPlayer){
							playerCount++;
						}
						j++;
					}
				}

			}
			if (opponentCount == 0){
				return 1;
			}
			else return 0;
		}

		/**
		 * Olasi yatay, dikey veya capraz connect4larin tehtit durumlarini kontrol eden metod
		 *
		 * @param state grid durum objesi
		 * @param mainPlayer mevcut oyuncu
		 * @param opponent rakip oyuncu
		 * @param i1 satir left bound
		 * @param i2 satir right bound
		 * @param j1 sutun left bound
		 * @param j2 sutun right bound
		 * @param horizMode horizontal mode switch degeri
		 * @return hesaplanilmis agirlik degeri
		 */
		private static int checkThreat(Connect4State state, char mainPlayer, char opponent, int i1, int i2, int j1, int j2, boolean horizMode){
			int opponentCount = 0;
			int playerCount = 0;
			char[][] board= state.getBoard();
			if(horizMode==true){
				int j=j1;
				for(int i=i1;i>=i2 && j<=j2; i--){

					if (board[i][j] == opponent){
						opponentCount++;
					} else if (board[i][j] == mainPlayer){
						playerCount++;
					}
					j++;

				}
			}
			else{
				if(i1==i2){
					int i=i1;
					for(int j=j1;j<=j2;j++){
						if (board[i][j] == opponent){
							opponentCount++;
						} else if (board[i][j] == mainPlayer){
							playerCount++;
						}
					}
				}
				else if(j1==j2){
					int j=j1;
					for(int i=i1;i<=i2;i++){
						if (board[i][j] == opponent){
							opponentCount++;
						} else if (board[i][j] == mainPlayer){
							playerCount++;
						}
					}
				}
				else{
					int j=j1;
					for(int i=i1;i<=i2;i++){
						if (board[i][j] == opponent){
							opponentCount++;
						} else if (board[i][j] == mainPlayer){
							playerCount++;
						}
						j++;
					}
				}

			}
			return applyThreatWeights(playerCount, opponentCount);
		}



			/**
			 * Tehtit etme durumlarinda skor agirliklarini gunceller
			 *
			 * @param playerCount suanki playera ait bu dortludeki parca sayisi
			 * @param opponentCount rakip playerdaki bu dortludeki parca sayisi
			 * @return hesaplanilmis agirlik degeri
			 */
			public static int applyThreatWeights(int playerCount, int opponentCount){
				int sum=0;

				//Eger bu dortlu uzerinde bize ait hic parca yok ve rakip oyuncuya ait
				//3 adet parca varsa bu durum kaybetme durumudur ve agirlikli degeri -WEIGTH_OF_THREAT yapilir.
				if (playerCount == 0 && opponentCount == 3){
					sum = -WEIGTH_OF_THREAT;
				}
				//Eger bize ait 3 parca var ve diger parca bos ise bu da kazanma durumudur
				//ve agirlikli degeri WEIGTH_OF_WINNING olur.
				else if (opponentCount == 0 && playerCount ==3) {
					sum = WEIGTH_OF_WINNING;
				}

				return sum;
			}


}
