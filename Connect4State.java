public interface Connect4State {
  public final static int ROWS = 6;            // Board height
  public final static int COLS = 7;            // Board width
  public final static char EMPTY = '.';        // Indicate empty place
  public final static char CHECKER0 = 'X';     // Indicate the first player's checker
  public final static char CHECKER1 = 'O';     // Indicate second player's checker
  public final static char [] CHECKERS = {CHECKER0, CHECKER1};

	/**
	 * Connect4 tahtasinin 2 boyutlu ifade edilmis olunan grid degeri dondurulur.
	 * @return grid arrayi dondurulur
	 */
  public char [][] getBoard();

  /**
   * Player arrayini dondurur
   * @return player arrayi
   */
  public Player [] getPlayers();

  /**
   * Hamleyi yapan playerin no su dondurulur
   * @return player no
   */
  public int getPlayerNum ();

  /**
   * Suandaki hamleyi yapacak player no dondurulur
   * @return player no
   */
  public Player getPlayerToMove();

  /**
   * Hamlenin valid olup olmadigi kontrol edilir
   * @param col sutun no
   * @return eger valid ise true dondurulur
   */
  public boolean isValidMove(int col);

  /**
   * Grid uzerinde bir hamle yapar
   * @param col sutun no
   */
  public void makeMove(int col);


  /**
	 * Gridin dolu olup olmadigini kontrol eder
	 * @return Eger grid dolu ise true doner.
	 */
  public boolean isFull();

  /**
	 * Oyunun bitip bitmedigine karar verir.
	 * @return Oyun bitmis ise true dondurulur.
	 */
  public boolean gameIsOver();
}
