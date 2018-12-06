import java.util.ArrayList;

import java.lang.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Connect4.java
 *
 * Controller that plays the connect 4 game.
 * @author Delos Chang
 *
 */

public class Connect4 {



	public static long averageTime(List<Result> arr){
		long sum = 0;
		for (int i = 0 ; i < arr.size(); i++){
			sum += arr.get(i).Time;
		}
		return sum/arr.size();
	}
	public static List<List<Result>> playGame(String fileName, int depth, Connect4View view){
		List list = new ArrayList<List>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
			ArrayList<Result> result_H1 = new ArrayList<Result>();
			ArrayList<Result> result_H2 = new ArrayList<Result>();

			stream.forEach(string->{
				String s[] = string.split(" ");
				String steps=s[0];
				int expected=Integer.parseInt(s[1]);
				Player [] tmpPlayers = new Player[2];
				tmpPlayers[0] = new Connect4StatPlayer("", steps);
				tmpPlayers[1]=tmpPlayers[0];
				Connect4Game tmpState = new Connect4Game(0, tmpPlayers);
				int tmpMove = tmpState.getPlayerToMove().getMove(tmpState, view);
				while(tmpMove!=-1){
					tmpState.makeMove(tmpMove);
					tmpMove = tmpState.getPlayerToMove().getMove(tmpState, view);
				}

				for (int i = 0 ; i < 2 ; i++){

					ComputerConnect4Player.hChoice=(i==1?true:false);
					long start = System.currentTimeMillis();

					Player [] players = new Player[2];
					String playerName = "Computer1";
					players[0] = new ComputerConnect4Player(playerName, depth);

					playerName = "Computer2";
					players[1] = new ComputerConnect4Player(playerName, depth);
					Connect4Game state = new Connect4Game(0, players, tmpState.getBoard(), tmpState.getMovesPlayed(), tmpState.grabEvalValue());

					// Hold current game state
					while (!state.gameIsOver()){
						int move = state.getPlayerToMove().getMove(state, view);
						state.makeMove(move);

					}

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
					tmpResult.Expected=expected;
					if (i == 0){
							result_H1.add(tmpResult);
					}else {
							result_H2.add(tmpResult);
					}
				}
			});

			list.add(result_H1);
			list.add(result_H2);


		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}


	public static void main(String [] args){
		Scanner input = new Scanner(System.in);
		String answer = "";
		Connect4View view;

		// Ask for either text or view
		while (!(answer.toLowerCase().contains("demo") || answer.toLowerCase().contains("istatistik"))){
			System.out.println("Demo / Istatistik Seciniz: ");
			answer = input.nextLine();
		}

		if (answer.toLowerCase().contains("demo")){
			view = new Connect4Graph();
			Player [] players = new Player[2];

			// Initialize the game
			// Computer - for computer
			ComputerConnect4Player.hChoice=true;

			String playerName = view.getAnswer("Ilk oyuncunun adini giriniz:" +
					"\n(Bilgisayar ise computer yazabilirsiniz)");

			if (playerName.toLowerCase().contains("computer")){
				int askDepth = view.getIntAnswer("Bilgisayar sezgi derinligini giriniz");
				players[0] = new ComputerConnect4Player(playerName, askDepth);
				players[0].canPrint = false;
			} else {
				players[0] = new Connect4HumanPlayer(playerName);
				players[0].canPrint = true;
			}

			playerName = view.getAnswer("Ikinci oyuncunun adini giriniz." +
					"\nBilgisayar ise computer yazabilirsiniz: ");

			if (playerName.toLowerCase().contains("computer")){
				int askDepth = view.getIntAnswer("Bilgisayar sezgi derinligini giriniz");
				players[1] = new ComputerConnect4Player(playerName, askDepth);
				players[1].canPrint = false;
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

		}
		else if (answer.toLowerCase().contains("istatistik")){
			view = new Connect4Text();
			Scanner keyboard = new Scanner(System.in);

			System.out.println("Derinlik giriniz : ");
			int statisticDepth = keyboard.nextInt();
			List<List<Result>> results= playGame("Test_L3_R1", statisticDepth, view);
			List<Result> result_H1 = results.get(0);
			List<Result> result_H2 = results.get(1);



			//System.out.println("H1 : " + result_H1.toString() );
			long average1 = averageTime(result_H1);

			int numberOfWins1 = 0;
			int numberOfLoses1 = 0;
			int numberOfTies1 = 0;
			int numberOfSuccess1 = 0;
			int numberOfFails1 = 0;

			for (int i = 0 ; i < result_H1.size(); i++){
				if (result_H1.get(i).Winner.equals("Computer1")){
					numberOfWins1++;
				}else if (result_H1.get(i).Winner.equals("Tie")){
					numberOfTies1++;
				}else{
					numberOfLoses1++;
				}
				if(result_H1.get(i).Expected>0 && result_H1.get(i).Winner.equals("Computer1")){
					numberOfSuccess1++;
				}else if (result_H1.get(i).Expected==0 && result_H1.get(i).Winner.equals("Tie")){
					numberOfSuccess1++;
				}else if(result_H1.get(i).Expected<0 && result_H1.get(i).Winner.equals("Computer2")){
					numberOfSuccess1++;
				}
				else{
					numberOfFails1++;
				}
			}


			System.out.println(result_H1.size() + " oyun oynandi. \nBu oyunlar icerisinde H1 kullanilarak YZ1: \n " + numberOfWins1 + "  adet galibiyet \n "
			 + numberOfTies1 + " adet beraberlik \n "
			  + numberOfLoses1 +" adet malubiyet alinmistir.\n"
			 + "Oyunlar ortalama " + average1 + "ms surmustur.\n"
			 + "Kullanilan data set uzerinde dogru tahmin orani = % "+((1.0*numberOfSuccess1/(numberOfSuccess1+numberOfFails1))*100) );

			 System.out.println("-----------------------------------------------------------------------");




			//System.out.println("H2 : " + result_H2.toString() );
			long average2 = averageTime(result_H2);
			int numberOfWins2 = 0;
			int numberOfLoses2 = 0;
			int numberOfTies2 = 0;
			int numberOfSuccess2 = 0;
			int numberOfFails2 = 0;

			for (int i = 0 ; i < result_H2.size(); i++){
				if (result_H2.get(i).Winner.equals("Computer1")){
					numberOfWins2++;
				}else if (result_H2.get(i).Winner.equals("Tie")){
					numberOfTies2++;
				}else{
					numberOfLoses2++;
				}
				if(result_H2.get(i).Expected>0 && result_H2.get(i).Winner.equals("Computer1")){
					numberOfSuccess2++;
				}else if (result_H2.get(i).Expected==0 && result_H2.get(i).Winner.equals("Tie")){
					numberOfSuccess2++;
				}else if(result_H2.get(i).Expected<0 && result_H2.get(i).Winner.equals("Computer2")){
					numberOfSuccess2++;
				}else{
					numberOfFails2++;
				}
			}

			System.out.println(result_H2.size() + " oyun oynandi.\n Bu oyunlar icerisinde H2 kullanilarak YZ2:  \n" + numberOfWins2 + "  adet galibiyet \n "
			 + numberOfTies2 + " adet beraberlik \n "
			  + numberOfLoses2 +" adet malubiyet alinmistir.\n"
			 + "Oyunlar ortalama " + average2 + "ms surmustur.\n"
			 + "Kullanilan data set uzerinde dogru tahmin orani = % "+((1.0*numberOfSuccess2/(numberOfSuccess2+numberOfFails2))*100) );




		}



	}
}
