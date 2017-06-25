import java.util.Scanner;

public class GameBase {

	static int pitNum = 6; //Number of boards per side
	static int stoneNum = 10; //Number of starting stones per board
	static int[] board = new int[pitNum * 2 + 2];
	/* Board representation
	 * 13 | 12 11 10 9 8 7
	 *       0  1  2 3 4 5 | 6
	 */
	static boolean player;
	static boolean repeatMove;

	public static void main(String[] args) {
		setBoard();
		player = true; //true for the south player, false for the north player

		printBoard();
		while (!terminal()) {
			repeatMove = false;
			int move = playerMove();
			updateBoard(move);
			printBoard();
			if (repeatMove) continue;
			player = player ? false : true;
		}

		captureRemainingPieces();
		printBoard();
		System.out.print("Game over. ");
		if (board[pitNum] > board[2 * pitNum + 1]) System.out.println("Player South wins by " + (board[pitNum] - board[2 * pitNum + 1]) + "!");
		else if (board[pitNum] == board[2 * pitNum + 1]) System.out.println("Draw!");
		else System.out.println("Player North wins by " + (board[2 * pitNum + 1] - board[pitNum]) + "!");
	}

	public static boolean terminal() {
		//Inefficient as it checks both sides
		for (int i = 0; i < pitNum; i++) {
			if (board[i] != 0) break;
			if (i == pitNum - 1) return true;
		}

		for (int i = pitNum + 1; i < 2 * pitNum + 1; i++) {
			if (board[i] != 0) break;
			if (i == 2 * pitNum) return true;
		}
		return false;
	}

	public static void setBoard() {
		for (int i = 0; i < pitNum; i++) {
			board[i] = stoneNum;
			board[2 * pitNum - i] = stoneNum;
		}

		board[pitNum] = 0;
		board[2 * pitNum + 1] = 0;
	}

	public static void printBoard() {
		//Find the largest value.
		int max = -1;
		for (int i = 0; i < board.length; i++) {
			if (i == pitNum || i == 2 * pitNum + 1) continue;
			if (board[i] > max) {
				max = board[i];
			}
		}

		for (int i = 0; i < pitNum; i++) {
			if (i + 1 > max) {
				max = i + 1;
			}
		}

		String boardString = " ";
		for (int i = 2 * pitNum; i >= pitNum + 1; i--) {
			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, board[i])) / Math.log(10)); k++) boardString += " ";
			boardString += "  " + board[i];
		}
		boardString += " ";

		//Print "<-- North"
		if ((boardString.length() / 2 - 8) > 0) {
			for (int i = 0; i < (boardString.length() - 9) / 2; i++) System.out.print(" ");
			System.out.print("<-- North");
			for (int i = 0; i < (boardString.length() - 9) / 2; i++) System.out.print(" ");
		}
		System.out.println();

		for (int i = 0; i < boardString.length() + 2; i++) System.out.print("-");
		System.out.println();
		System.out.println(boardString);
		System.out.println();

		String storeString = "   ";
		for (int i = 0; i < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, board[2 * pitNum + 1])) / Math.log(10)); i++) storeString += " ";
		storeString += board[2 * pitNum + 1];
		int storeStringLength = storeString.length();
		for (int i = 0; i < boardString.length() - storeStringLength - Math.floor(Math.log(Math.max(1, board[pitNum])) / Math.log(10)) - 2; i++) storeString += " ";
		storeString += board[pitNum];
		System.out.println(storeString);
		System.out.println();

		boardString = " ";
		for (int i = 0; i < pitNum; i++) {
			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, board[i])) / Math.log(10)); k++) boardString += " ";
			boardString += "  " + board[i];
		}
		boardString += " ";
		System.out.println(boardString);

		for (int i = 0; i < boardString.length() + 2; i++) System.out.print("-");
		System.out.println();

		String columnString = " ";
		for (int i = 0; i < pitNum; i++) {
			for (int k = 0; k < Math.floor(Math.log(Math.max(1, max)) / Math.log(10)) - Math.floor(Math.log(Math.max(1, i + 1)) / Math.log(10)); k++) columnString += " ";
			columnString += "  " + (i + 1);
		}
		columnString += " ";
		System.out.println(columnString);

		//Print "South -->"
		if ((boardString.length() / 2 - 8) > 0) {
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
				if (board[move - 1] == 0) {
					System.out.print("Invalid move! Player " + (player ? "South's" : "North's") + " move: ");
					continue;
				}
			} else {
				if (board[2 * pitNum - move + 1] == 0) {
					System.out.print("Invalid move! Player "  + (player ? "South's" : "North's") + " move: ");
					continue;
				}
			}

			System.out.println();
			keyboard.close();
			return move;
		}
	}

	public static void updateBoard(int move) {
		int sowLocation;
		if (player) sowLocation = move - 1;
		else sowLocation = 2 * pitNum - move + 1;

		int numberOfStones = board[sowLocation];
		board[sowLocation] = 0;
		sowLocation++;

		for (int i = 0; i < numberOfStones; i++) {
			if ((player && sowLocation == 2 * pitNum + 1) || (!player && sowLocation == pitNum)) { //Cannot sow the opponent's store
				i--;
				sowLocation++;
				continue;
			}
			if (sowLocation == board.length) sowLocation = 0;
			//Capture
			if (i == numberOfStones - 1) {
				if (sowLocation == pitNum || sowLocation == 2 * pitNum + 1) repeatMove = true;
				else if (board[sowLocation] == 0 && (player && (sowLocation < pitNum) || !player && (sowLocation > pitNum) )) {
					board[sowLocation < pitNum ? pitNum : 2 * pitNum + 1] += 1 + board[Math.abs(2 * pitNum - sowLocation)];
					board[Math.abs(2 * pitNum - sowLocation)] = 0;
					break;
				}
			}

			board[sowLocation]++;
			sowLocation++;
		}
	}

	public static void captureRemainingPieces() { //At the end of the game, all the pieces belonging to one's side are captured.
		for (int i = 0; i < board.length; i++) {
			if (i == pitNum || i == 2 * pitNum + 1) continue;
			if (i < pitNum) {
				board[pitNum] += board[i];
				board[i] = 0;
			} else {
				board[2 * pitNum + 1] += board[i];
				board[i] = 0;
			}
		}
	}
}