//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.StackPane;
//import javafx.stage.Stage;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import java.util.Random;
import java.util.Scanner;

public class GameBase {

	static int pitNum = 6; //Number of pits per side
	static int stoneNum = 6; //Number of starting stones per pit
	static int[] store = new int[2];
	static int[] pit = new int[pitNum * 2];
	/* Board representation
	 * 11 10 9 8 7 6
	 *  0  1 2 3 4 5
	 */
	static boolean player;

	public static void main(String[] args) {
		setBoard();
		player = true; //true for the south player, false for the north player

		printBoard();
		while (!terminal()) {
			int move = playerMove();
			updateBoard(move);
			printBoard();
			player = player ? false : true;
		}

		captureRemainingPieces();
		printBoard();
		System.out.print("Game over. ");
		if (store[0] > store[1]) System.out.println("Player South wins by " + (store[0] - store[1]) + "!");
		else if (store[0] == store[1]) System.out.println("Draw!");
		else System.out.println("Player North wins by " + (store[1] - store[0]) + "!");
	}

	public static boolean terminal() {
		//Inefficient as it checks both sides
		for (int i = 0; i < pitNum; i++) {
			if (pit[i] != 0) break;
			if (i == pitNum - 1) return true;
		}

		for (int i = pitNum; i < 2 * pitNum; i++) {
			if (pit[i] != 0) break;
			if (i == 2 * pitNum - 1) return true;
		}
		return false;
	}

	public static void setBoard() {
		for (int i = 0; i < pit.length; i++) {
			pit[i] = stoneNum;
		}

		store[0] = 0;
		store[1] = 0;
	}

	public static void printBoard() {
		//Find the largest value.
		int max = -1;
		for (int i = 0; i < pit.length; i++) {
			if (pit[i] > max) {
				max = pit[i];
			}
		}

		for (int i = 0; i < pitNum; i++) {
			if (i + 1 > max) {
				max = i + 1;
			}
		}

		for (int i = 0; i < 2; i++) {
			if (store[i] > max) {
				max = store[i];
			}
		}

//		String pitString = "";
//		String storeString = store[1] + "  |";
//		System.out.print(storeString);
//		for (int i = 2 * pitNum - 1; i >= pitNum; i--) {
//			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, pit[i])) / Math.log(10)); k++) pitString += " ";
//			pitString += "  " + pit[i];
//		}
//
//		System.out.println(pitString);
//
//		for (int i = 0; i < storeString.length(); i++) System.out.print(" ");
//		for (int i = 0; i < pitNum; i++) {
//			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, pit[i])) / Math.log(10)); k++) System.out.print(" ");
//			System.out.print("  " + pit[i]);
//		}
//		System.out.println("  |  " + store[0]);
//
//		for (int i = 0; i < storeString.length(); i++) System.out.print(" ");
//		for (int i = 0; i < pitString.length() + 1; i++) System.out.print("-");
//		System.out.println();
//		for (int i = 0; i < storeString.length(); i++) System.out.print(" ");
//		for (int i = 0; i < pitNum; i++) {
//			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, i + 1)) / Math.log(10)); k++) System.out.print(" ");
//			System.out.print("  " + (i + 1));
//		}

		String pitString = " ";
		for (int i = 2 * pitNum - 1; i >= pitNum; i--) {
			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, pit[i])) / Math.log(10)); k++) pitString += " ";
			pitString += "  " + pit[i];
		}
		pitString += " ";

		//Print "<-- North"
		if ((pitString.length() / 2 - 8) > 0) {
			for (int i = 0; i < (pitString.length() - 9) / 2; i++) System.out.print(" ");
			System.out.print("<-- North");
			for (int i = 0; i < (pitString.length() - 9) / 2; i++) System.out.print(" ");
		}
		System.out.println();

		for (int i = 0; i < pitString.length() + 2; i++) System.out.print("-");
		System.out.println();
		System.out.println(pitString);
		System.out.println();

		String storeString = "   ";
		for (int i = 0; i < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, store[1])) / Math.log(10)); i++) storeString += " ";
		storeString += store[1];
		int storeStringLength = storeString.length();
		for (int i = 0; i < pitString.length() - storeStringLength - Math.floor(Math.log(Math.max(1, store[0])) / Math.log(10)) - 2; i++) storeString += " ";
		storeString += store[0];
		System.out.println(storeString);
		System.out.println();

		pitString = " ";
		for (int i = 0; i < pitNum; i++) {
			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, pit[i])) / Math.log(10)); k++) pitString += " ";
			pitString += "  " + pit[i];
		}
		pitString += " ";
		System.out.println(pitString);

		for (int i = 0; i < pitString.length() + 2; i++) System.out.print("-");
		System.out.println();

		String columnString = " ";
		for (int i = 0; i < pitNum; i++) {
			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, i + 1)) / Math.log(10)); k++) columnString += " ";
			columnString += "  " + (i + 1);
		}
		columnString += " ";
		System.out.println(columnString);

		//Print "South -->"
		if ((pitString.length() / 2 - 8) > 0) {
			for (int i = 0; i < (columnString.length() - 9) / 2; i++) System.out.print(" ");
			System.out.print("South -->");
			for (int i = 0; i < (columnString.length() - 9) / 2; i++) System.out.print(" ");
		}
		System.out.println();

		System.out.println();
		System.out.println();

	}

	public static int playerMove() {
		Scanner keyboard = new Scanner(System.in);
		System.out.print("Player " + (player ? "South's" : "North's") + " move: ");
		while (true) {
			int move = keyboard.nextInt();
			if (move < 1 || move > pitNum) {
				System.out.print("Invalid move! Player " + (player ? "South's" : "North's") + " move: ");
				continue;
			}

			if (player) {
				if (pit[move - 1] == 0) {
					System.out.print("Invalid move! Player " + (player ? "South's" : "North's") + " move: ");
					continue;
				}
			} else {
				if (pit[2 * pitNum - move] == 0) {
					System.out.print("Invalid move! Player "  + (player ? "South's" : "North's") + " move: ");
					continue;
				}
			}

			System.out.println();
			return move;
		}
	}

	public static void updateBoard(int move) {
		int sowLocation;
		if (player) sowLocation = move - 1;
		else sowLocation = pit.length - move;

		int numberOfStones = pit[sowLocation];
		pit[sowLocation] = 0;
		sowLocation++;
		for (int i = 0; i < numberOfStones; i++) {
			if (sowLocation == pit.length) sowLocation = 0;

			//The case when a stone goes to the store
			if (sowLocation == pitNum) {
				store[0]++;
				i++;
				if (i < numberOfStones) pit[sowLocation]++;
				sowLocation++;
				continue;
			} else if (sowLocation == 0) {
				store[1]++;
				i++;
				if (i < numberOfStones) pit[sowLocation]++;
				sowLocation++;
				continue;
			}

			//Capture
			if (i == numberOfStones - 1) {
				if (pit[sowLocation] == 0 && pit[Math.abs(pit.length - 1 - sowLocation)] != 0) {
					store[sowLocation < pitNum ? 0 : 1] += 1 + pit[Math.abs(pit.length - 1 - sowLocation)];
					pit[Math.abs(pit.length - 1 - sowLocation)] = 0;
					break;
				}
			}
			pit[sowLocation]++;
			sowLocation++;
		}
	}

	public static void captureRemainingPieces() { //At the end of the game, all the pieces belonging to one's side are captured.
		for (int i = 0; i < pit.length; i++) {
			if (i < pitNum) {
				store[0] += pit[i];
				pit[i] = 0;
			} else {
				store[1] += pit[i];
				pit[i] = 0;
			}
		}
	}
}
