import java.util.InputMismatchException;
import java.util.Scanner;


public class Connect4Text implements Connect4View{
	private Scanner input;

	public Connect4Text(){
		input = new Scanner(System.in);
	}

	/**
	 * Mevcut gridi ekrana bastirir.
	 * @param state oyun durumu
	 */
	public void display(Connect4State state){
		char [][] board = state.getBoard();

		for (int row = Connect4State.ROWS - 1; row > -1; row--){
			System.out.print(row + " |");
			for (int column = 0; column < Connect4State.COLS; column++){
				System.out.print(board[row][column]);
				System.out.print("  ");
			}
			System.out.println("\n");
		}

		System.out.print(" ");
		for (int column = 0; column < Connect4State.COLS; column++){
			System.out.print("  "+column);
		}

		System.out.println("\n");

	}

	/**
	 * Kullanicidan hamle ister
	 * @param state oyun state'i
	 * @return yapilacak olan hamlenin sutun nosu
	 */
	public int getUserMove(Connect4State state){
		Scanner column;
		int choose;

		System.out.println();
		System.out.println("Please pick a column");

		column = new Scanner(System.in);

		for(;;) {
		    if(!column.hasNextInt() ) {
		        System.out.println("Integers from 0 to 6 allowed.");
						System.out.println("Please pick a column");
		        column.next();
		        continue;
		    }
		    choose = column.nextInt();
				if(!state.isValidMove(choose)){
					System.out.println("This move is not valid, please make a valid move.");
					continue;
				}
		    if( (choose < 0) || (choose > Connect4State.COLS - 1) ) {
						System.out.println("Illegal column. Please try again");
						System.out.println("Please pick a column");
		        continue;
		    }
		    break;
		}

		return choose;
	}

	/**
	 * Yapilan hamleyi report eder.
	 * @param chosenMove yapilmis hamle
	 * @param name player ismi
	 */
	public void reportMove (int chosenMove, String name){
		System.out.println("\n" + name + " chooses the column " + chosenMove);
	}

	/**
	 * Kullaniciya question valuesunda bir soru sorar ve bir int cevabi alir.
	 * @param question sorulacak soru
	 * @return int tipinde cevap
	 */
	public int getIntAnswer (String question){
		int answer = 0;
		boolean valid = false;

		System.out.println(question + " ");

		while (!valid){
			try {
				answer = input.nextInt();
				valid = true;
			} catch (NumberFormatException ex) {
				reportToUser("Error: "+ ex + " Please enter an integer");
				valid = false;
			}
		}
		return answer;
	}

	/**
	 * Usera mesaj gonderir.
	 * @param message gonderilecek mesaj
	 */
	public void reportToUser(String message){
		System.out.println(message);
	}

	/**
	 * Kullaniciya question sorusunu sorar ve string cevabi alir
	 * @param question sorulacak soru
	 * @return cevap
	 */
	public String getAnswer(String question){
		System.out.println(question + " ");

		return input.nextLine();

	}

}
