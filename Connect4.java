import java.util.Scanner;
import java.util.ArrayList;
import java.lang.*;

/**
 * Connect4.java
 *
 * Controller that plays the connect 4 game.
 * @author Delos Chang
 *
 */

public class Connect4 {
	public static void main(String [] args){
		Scanner input = new Scanner(System.in);
		String answer = "";
		Connect4View view;

		// Ask for either text or view
		while (!(answer.contains("Demo") || answer.contains("Istatistik"))){
			System.out.println("Demo / Istatistik Seciniz");
			answer = input.nextLine();
		}

		if (answer.contains("Demo")){
			view = new Connect4Text();
			Player [] players = new Player[2];

			// Initialize the game
			// Computer - for computer

			String playerName = view.getAnswer("Ilk oyuncunun adini giriniz" +
					"\n Bilgisayar ise computer yazabilirsiniz.");

			if (playerName.contains("Computer")){
				int askDepth = view.getIntAnswer("Bilgisayar sezgi derinligini giriniz");
				players[0] = new ComputerConnect4Player(playerName, askDepth);
				players[0].canPrint = true;
			} else {
				players[0] = new Connect4HumanPlayer(playerName);
				players[0].canPrint = true;
			}

			playerName = view.getAnswer("Ikinci oyuncunun adini giriniz." +
					"\n Bilgisayar ise computer yazabilirsiniz. ");

			if (playerName.contains("Computer")){
				int askDepth = view.getIntAnswer("Bilgisayar sezgi derinligini giriniz");
				players[1] = new ComputerConnect4Player(playerName, askDepth);
				players[1].canPrint = true;
			} else {
				players[1] = new Connect4HumanPlayer(playerName);
				players[1].canPrint = true;
			}

			Connect4Game state = new Connect4Game(0, players);
			view.display(state);

			// Hold current game state
			while (!state.gameIsOver()){
				int move = state.getPlayerToMove().getMove(state, view);

				state.makeMove(move);

				view.display(state);
			}

			// The game is over
			// declare the winner!
			view.reportToUser(state.getPlayers()[1 - state.getPlayerNum()].getName() + " won!");
		}else if (answer.contains("Istatistik")){
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Derinlik giriniz : ");
			int statisticDepth = keyboard.nextInt();
			System.out.println("Oyun sayisini giriniz : ");
			int numberOfGame = keyboard.nextInt();
			ArrayList<Result> result_H1 = new ArrayList<Result>();
			ArrayList<Result> result_H2 = new ArrayList<Result>();

			for (int j = 1 ; j <= numberOfGame ; j++){
				int Heuristic = 0;
				for (int i = 0 ; i < 2 ; i++){
					long start = System.currentTimeMillis();
				  Heuristic = i;

					view = new Connect4Text();
					Player [] players = new Player[2];

					// Initialize the game
					// Computer - for computer

					String playerName = "Computer1";

					if (playerName.contains("Computer")){
						int askDepth = statisticDepth;
						players[0] = new ComputerConnect4Player(playerName, askDepth);
					} else {
						players[0] = new Connect4HumanPlayer(playerName);
					}

					playerName = "Computer2";

					if (playerName.contains("Computer")){
						int askDepth = statisticDepth;
						players[1] = new ComputerConnect4Player(playerName, askDepth);
					} else {
						players[1] = new Connect4HumanPlayer(playerName);
					}

					Connect4Game state = new Connect4Game(0, players);

				//	view.display(state);

					// Hold current game state
					while (!state.gameIsOver()){
						int move = state.getPlayerToMove().getMove(state, view);

						state.makeMove(move);

						//view.display(state);
					}

					// The game is over
					// declare the winner!
					//view.reportToUser(state.getPlayers()[1 - state.getPlayerNum()].getName() + " won!");
					long finish = System.currentTimeMillis();
					long timeElapsed = finish - start;
					Result tmpResult=new Result();
					if (state.isFull()){
							tmpResult.Winner="Tie";
					}
					else{
						tmpResult.Winner=state.getPlayers()[1 - state.getPlayerNum()].getName();
					}
					tmpResult.Time=timeElapsed;
					if (i == 0){
							result_H1.add(tmpResult);
					}else if (i == 1){
							result_H2.add(tmpResult);
					}
				}
			}

			System.out.println("H1 : " + result_H1.toString() );

			System.out.println("H2 : " + result_H2.toString() );



		}



	}
}
