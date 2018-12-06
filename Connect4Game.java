import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class Connect4Game implements Connect4State{
	private char[][] board;
	private Player [] players;
	private int playerToMoveNum; // Mevcut hamle sirasinin hangi oyuncuda oldugunu tutar.

	private int latestRow = -1; // en son yapilan hamlenin satirinin numarasi
	private int latestCol = -1; // en son yapilan hamlenin sutununun numarasi

	private int movesDone; // Suana kadar yapilmis hamle sayisi

	private int evalValue; // Mevcut gridin skor degeri(Pozitif ise avantaj ilk kullanicida, negatif ise ikinci kullanicida, 0 ise avantaj yok)


	/**
	 * Game'i initialize eder
	 *
	 * @param playerNum ilk hamleyi yapacak kullanici
	 * @param thePlayers player obje arrayleri
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
	 * @param playerNum ilk hamleyi yapacak kullanici
	 * @param thePlayers player obje arrayleri
	 * @param initialBoard baslanilacak olan grid degerleri
	 * @param movesMade suana kadar yapilmis hamle sayisi
	 * @param unblockedTotal suanki grid skor degeri
	 */
	public Connect4Game(int playerNum, Player[] thePlayers, char[][] initialBoard, int movesMade, int unblockedTotal){
		// ROWS ve COLS kullanilarak grid initialize edilir.
		board = new char[ROWS][COLS];

		// Yeni grid kopyalanir.
		for (int row = 0; row < ROWS; row++){
			for (int column = 0; column < COLS; column++){
				board[row][column] = initialBoard[row][column];
			}
		}

		playerToMoveNum = playerNum;
		players = thePlayers;

		movesDone = movesMade;
		evalValue = unblockedTotal;
	}


	@Override
	/**
	 * Connect4 tahtasinin 2 boyutlu ifade edilmis olunan grid degeri dondurulur.
	 * @return grid arrayi dondurulur
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
	 * Suana kadar oynanmis hamle sayisi dondurulur.
	 * @return yapilmis hamle sayisi dondurulur.
	 */
	public int getMovesPlayed(){
		return movesDone;
	}

	/**
	 * Gride ait skor degeri dondurulur
	 * @return grid skor degeri dondurulur.
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
		// Eger bu sutun full degil ise bu hamle validdir.
		if (col >= 0 && col < 7){
			return !isColumnFull(col);
		}else{
			return false;
		}

	}

	/**
	 * Grid uzerinde bir hamle yapar
	 * @param col the sutun no
	 */
	@Override
	public void makeMove(int col) {
		// hamlenin valid olup olmadigi kontrol edilir.
		if (isValidMove(col)){
			int openRow = findOpenRow(col);


			playerToMoveNum = 1 - playerToMoveNum;

			evalValue = -1 * evalValue;

			board[openRow][col] = CHECKERS[getPlayerNum()];
			evalValue = ComputerConnect4Player.evaluateBoard(this);

			movesDone++;

			latestRow = openRow;
			latestCol = col;
		} else {
			throw new IllegalStateException("Column is full!");
		}
	}

	/**
	 * Verilen sutun uzerindeki ilk bos satir elde edilir
	 * Eger sutun dolu ise -1 dondurulur
	 *
	 * @param col sutun no
	 */
	private int findOpenRow(int col){
		for (int i = 0; i < ROWS; i++){
			if (board[i][col] == EMPTY){
				return i;
			}
		}

		return -1;
	}

	/**
	 * Sutun uzerindeki ilk dolu slotu elde eder
	 *
	 * @param col kontrol edilecek sutun
	 */
	private int findTop(int col){
		int row = ROWS - 1;
		while (board[row][col] == EMPTY && row > 0){
			row--;
		}
		return row;

	}

	/**
	 * Verilen hamleyi geri alir ve state puanimizi eski haline getirir.
	 *
	 * @param column undo yapilacak olan sutun, en ustteki tas en son konuldugu icin sutun numarasi yeterlidir.
	 * @param stateEval hamleden onceki grid degerlendirme puani
	 */
	public void undoMove(int column, int stateEval){
		int row = this.findTop(column);

		// grid uzerindeki bu hucreyi bosa dondurulur
		board[row][column] = EMPTY;

		// Oynama sirasini bir onceki oyuncuya getirir.
		playerToMoveNum = 1 - playerToMoveNum;

		evalValue = stateEval;
		movesDone--;
	}

	/**
	 * Sutunun dolu olup olmadigini kontrol eder
	 *
	 * @param col kontrol edilecek sutun
	 * @return eger sutun dolu ise true dondurulur, diger durumlarda false dondurulur.
	 */
	private boolean isColumnFull(int col) {
		return !(board[ROWS - 1][col] == EMPTY);
	}

	/**
	 * Gridin dolu olup olmadigini kontrol eder
	 * @return Eger grid dolu ise true doner.
	 */
	@Override
	public boolean isFull() {
		return (movesDone == ROWS * COLS);
	}


	/**
	 * Herhangi bir 4'lu ile kazanilip kazanilmadigini karar verir
	 *
	 * @param row son yapilan hamlenin satir numarasi
	 * @param column son yapilan hamlenin sutun numarasi
	 * @param rowOffset row offset
	 * @param colOffset col offset
	 * @return Eger bir dortlu bulunmus ise true dondurulur.
	 */
	private boolean checkForFour(int row, int column,
			int rowOffset, int colOffset){

		int winCounter = 0; // counts to 4 for win

		// Olasi 4lu icin diger uc taraf elde edilir.
		int oppRow = 3 * rowOffset + row;
		int oppColumn = 3 * colOffset + column;

		// imkansiz durumlari denememek amaciyla bazi durum kontrolleri yapilmistir.
		// toplam hamlenin 7 hamleden az olmasi
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

			// Offsetleri guncelleyerek bir sonraki hucreye bakmamiz ayarlanir.
			row += rowOffset;
			column += colOffset;
		}

		// Eger 4lu elde edilmisse true dondurulur
		return (winCounter == 4);
	}

	/**
	 * Oyunun bitip bitmedigine karar verir.
	 * @return Oyun bitmis ise true dondurulur.
	 */
	@Override
	public boolean gameIsOver() {
		// Gridin dolu olup olmadigi kontrol edilir.
		if ( isFull() ){
			return true;
		}

		// Dikey dortluler kontrol edilir.
		if ( checkForFour(latestRow, latestCol, -1, 0)) return true;

		for (int offset = 0; offset < 4; offset++){
			// Yatay dortluler kontrol edilir.
			if ( checkForFour(latestRow, latestCol - offset, 0, 1)) return true;

			// Pozitif capraz kontrol edilir.
			if ( checkForFour(latestRow - offset, latestCol + offset, 1, -1)) return true;

			// Negatif capraz kontrol edilir.
			if ( checkForFour(latestRow - offset, latestCol - offset, 1, 1)) return true;
		}

		return false;
	}


}
