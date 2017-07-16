import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI {

	static Random randomNum = new Random();

//	static int maxMemory = 536870912; //In bytes. 512 mb
	static int maxMemory = 50000000;

	static int pitNum = 6; //Number of boards per side
	static int stoneNum = 6; //Number of starting stones per board
	static boolean easyCapture = true;

	static int rootPlayer;
	static int currentPlayer;
	static int savedPlayer; //for default policy

	static int[] rootBoard = new int[pitNum * 2 + 2];
	static int[] currentBoard = new int[pitNum * 2 + 2];

	//Time
	static int simulations; //Count the number of simulations
	static long timeBegin; //To determine time elapsed
	static long timeEnd;
	static long tolerance = 10; //Minimum time remaining after move is played MILLISECONDS
	static long timeTarget;
	static int gameLength = 0;
	static int gameDepth;

	static boolean memoryUsed = false;

	static int rootMemoryIndex; //Need to implement this for turn based play. Used in backup
	static int currentMemoryIndex; //Change to be the memory of the root node
	static int memoryCursor; //Keeps track of the next index of the index that was last used

	static int size = maxMemory / 30;
	static ArrayList<Integer>[] childNodes = new ArrayList[size]; //input: index, any possible child in list. output: index of child node
	static int[] parentNodes = new int[size]; //input: index. output: index of parent node
	static int[] moveOfNode = new int[size]; //To keep track of what move corresponds to a child in childNodes
	static int[] winCount = new int[size];
	static int[] lossCount = new int[size];
	static int[] drawCount = new int[size];
	static int[] totalVisits = new int[size];
	static boolean[] fullyExpanded = new boolean[size];
	static boolean[] lastSowStore = new boolean[size]; //Keep track of nodes in which the player to move doesn't change


	//Others
	static double winScore = 1;
	static double drawScore = 0;
	static double lossScore = -1;
	static int currentMove; //To store move between fullyExpanded() and expand()
	static boolean repeatMove;

	public static void main(String[] args) {}

	public static void initialize() { //This function is to be called outside of this class to save time when allocating space in the ram
		for (int i = 0; i < childNodes.length; i++) childNodes[i] = new ArrayList<Integer>();
		clearAllMemory();
	}

	public static int findMove(int[] board, List<Integer> lastMoves, long[] timeControl, long playerTime, boolean newGame, boolean south) {
    	timeBegin = System.currentTimeMillis();
    	timeTarget = Math.max(playerTime * 4 / 5 - timeControl[1], tolerance);
		gameLength += 2;
    	rootMemoryIndex = 0;

		filterMemory();

    	memoryUsed = false;
		memoryCursor = 1;

		setRootBoard(board);

		if (south) rootPlayer = 1;
		else rootPlayer = 2;

		return search(timeControl, playerTime, south);
	}

	public static void clearAllMemory() {
		for (int i = 0; i < childNodes.length; i++) {
//			if (!childNodes[i].isEmpty()) childNodes[i].clear();
			childNodes[i].clear();
		}

		for (int i = 0; i < size; i++) {
			parentNodes[i] = -1;
			moveOfNode[i] = -1;
			winCount[i] = 0;
			lossCount[i] = 0;
			drawCount[i] = 0;
			totalVisits[i] = 0;
			fullyExpanded[i] = false;
			lastSowStore[i] = false;
		}
	}

	public static void filterMemory() {
		for (int i = 0; i < parentNodes.length; i++) {
			if (parentNodes[i] != -1) {
				childNodes[i].clear();
				parentNodes[i] = -1;
				moveOfNode[i] = -1;
				winCount[i] = 0;
				lossCount[i] = 0;
				drawCount[i] = 0;
				totalVisits[i] = 0;
				fullyExpanded[i] = false;
				lastSowStore[i] = false;
			}
		}

		childNodes[0].clear();
		parentNodes[0] = -1;
		moveOfNode[0] = -1;
		winCount[0] = 0;
		lossCount[0] = 0;
		drawCount[0] = 0;
		totalVisits[0] = 0;
		fullyExpanded[0] = false;
		lastSowStore[0] = false;
	}

	public static int search(long[] timeControl, long playerTime, boolean south) {
    	simulations = 0; //testing purposes

    	while (computationalBudget(timeControl, playerTime)) {
    		int newMemoryIndex = treePolicy();
    		if (newMemoryIndex == -1)  return bestNode();
    		double result = defaultPolicy(newMemoryIndex);
    		backup(newMemoryIndex, result);
    	}

    	return bestNode();
	}

	public static boolean computationalBudget(long[] timeControl, long playerTime) {
    	timeEnd = System.currentTimeMillis();
    	simulations++;
//		System.out.println(simulations);

//    	if (playerTime - (timeEnd - timeBegin) <= timeTarget) return false;
////    	if (timeEnd - timeBegin >= 1000) return false; //Time per move.
//
//    	return true;

    	if (simulations <= 500) return true;
    	return false;
	}

	public static int treePolicy() {
    	gameDepth = gameLength;
    	setCurrentBoard();
    	currentMemoryIndex = rootMemoryIndex;
    	currentPlayer = rootPlayer == 1 ? 2 : 1; //Start as the opposing player
    	repeatMove = false;

//    	System.out.println("new game");
//		printBoard(currentBoard);

    	int bestChild = -1;
    	while (!terminal(currentBoard)) {
//    		printBoard(currentBoard);

    		if (!repeatMove) currentPlayer = currentPlayer == 1 ? 2 : 1;
    		repeatMove = false;

    		if (!fullyExpanded()) {
    			return expand();
    		} else {
    			bestChild = bestChild();
    		}

    		gameDepth++;
    	}

//    	if (bestChild == -1) {
////    		printBoard(rootBoard);
////    		System.exit(0);
//    		return -1;
//    	}

    	return bestChild;
	}

	public static double defaultPolicy(int newMemoryIndex) { //Simulate a game
 		savedPlayer = currentPlayer;

 		//Remember not to change if repeat move
//		currentPlayer = currentPlayer == 1 ? 2 : 1;

 		PlayMove:
		while (!terminal(currentBoard)) {
	    	boolean[] possibleMoves = new boolean[pitNum];
	    	for (int i = 0; i < possibleMoves.length; i++) possibleMoves[i] = true;

	    	int numberOfPossibleMoves = pitNum;

	    	//Illegal moves are ignored
	    	int playerAdjustment = currentPlayer == 1 ? 0 : pitNum + 1; //To adjust for which player it is when looking at the board

			//Play moves that repeat the turn
			for (int i = pitNum - 1; i >= 0; i--) {
				if (currentBoard[i + playerAdjustment] % (2 * pitNum + 1) == pitNum - i) {
			    	updateBoard(i, currentPlayer == 1 ? true : false, currentBoard);
					repeatMove = false;
		    		continue PlayMove;
				}
			}

			//Capture if possible searching from right to left
//			for (int i = 0 + playerAdjustment; i <= pitNum - 2 + playerAdjustment; i++) {
//				if (currentBoard[i] == 0) continue;
//				int lastSowIndex = (i + currentBoard[i]) % (2 * pitNum + 1);
//				if (lastSowIndex == pitNum || lastSowIndex == currentBoard.length - 1) continue;
//				if ((currentPlayer == 1 && lastSowIndex > pitNum) || (currentPlayer == 2 && lastSowIndex < pitNum)) continue;
//				if (currentBoard[lastSowIndex] == 0 && currentBoard[2 * pitNum - lastSowIndex] >= 3) {
////						System.out.println(i);
////						System.out.println(lastSowIndex);
////						printBoard(currentBoard);
////						updateBoard(i - playerAdjustment, currentPlayer == 1 ? true : false, currentBoard);
////						printBoard(currentBoard);
////						System.exit(0);
//
//					updateBoard(i - playerAdjustment, currentPlayer == 1 ? true : false, currentBoard);
//					if (repeatMove) {
//						repeatMove = false;
//						continue PlayMove;
//					}
//					currentPlayer = currentPlayer == 1 ? 2 : 1;
//					continue PlayMove;
//				}
//			}

//			for (int i = pitNum - 3; i >= 0; i--) {
//				if (currentBoard[i + playerAdjustment + currentBoard[i + playerAdjustment]] == 0 && true) {
//
//				}
//			}

	    	for (int i = 0; i < pitNum; i++) {
	    		if (currentBoard[i + playerAdjustment] == 0) {
	    			possibleMoves[i] = false;
	    			numberOfPossibleMoves--;
	    		}
	    	}

	    	if (numberOfPossibleMoves == 0) {
	    		System.out.println("Error");
	    		System.exit(0);
	    	}

	    	int i;
	    	int randomExpand = randomNum.nextInt(numberOfPossibleMoves) + 1;
	     	for (i = 0; i < randomExpand; i++) {
	    		if (!possibleMoves[i]) randomExpand++;
	    	}

	    	i -= 1;

	    	updateBoard(i, currentPlayer == 1 ? true : false, currentBoard);
	    	if (repeatMove) {
				repeatMove = false;
	    		continue;
	    	}

			currentPlayer = currentPlayer == 1 ? 2 : 1;
		}

		captureRemainingPieces(currentBoard);

		if (currentBoard[pitNum] > currentBoard[currentBoard.length - 1]) {
			if (savedPlayer == 1) return winScore;
			else return lossScore;
		} else if (currentBoard[pitNum] == currentBoard[currentBoard.length - 1]) {
			return drawScore;
		} else {
			if (savedPlayer == 1) return lossScore;
			else return winScore;
		}
//		return randomNum.nextInt(3) - 1;
	}

	public static void backup(int newMemoryIndex, double result) { //Update the tree information. REMEMBER TO BACKPROPAGATE PROPERLY FOR REPEATED MOVES
    	int perspective;

    	if (result == winScore) perspective = 1;
    	else if (result == lossScore) perspective = -1;
    	else perspective = 0;

    	do {
    		if (perspective == 1) winCount[newMemoryIndex]++;
    		else if (perspective == -1) lossCount[newMemoryIndex]++;
    		else drawCount[newMemoryIndex]++;

    		totalVisits[newMemoryIndex]++;

    		if (!lastSowStore[parentNodes[newMemoryIndex]]) {
            	result *= -1;
            	perspective *= -1;
    		}

    		newMemoryIndex = parentNodes[newMemoryIndex];
    	} while (newMemoryIndex != rootMemoryIndex);

    	//One last time for root node
    	totalVisits[newMemoryIndex]++;
	}

	public static int bestNode() { //Calculate the best node based on search
    	double highScore = -2;
    	int bestNode = -1;

    	for (int i = 0; i < childNodes[rootMemoryIndex].size(); i++) {
			double score = winScore * winCount[childNodes[rootMemoryIndex].get(i)] + lossScore * lossCount[childNodes[rootMemoryIndex].get(i)] + drawScore * drawCount[childNodes[rootMemoryIndex].get(i)];
			int visits = totalVisits[childNodes[rootMemoryIndex].get(i)];
			double percentScore = score / visits;

//			System.out.println("Move : " + (rootPlayer == 1 ? moveOfNode[childNodes[rootMemoryIndex].get(i)] + 1 : (6 - moveOfNode[childNodes[rootMemoryIndex].get(i)])) + " Scoretotal: " + (percentScore + 1) / 2 + " visits: " + visits);
			if (percentScore > highScore) {
    			highScore = percentScore;
    			bestNode = i;
    		}
    	}

    	if (bestNode == -1) {
    	   	//Illegal moves are ignored
        	int playerAdjustment = rootPlayer == 1 ? 0 : pitNum + 1; //To adjust for which player it is when looking at the board
        	for (int i = 0; i < pitNum; i++) {
        		if (currentBoard[i + playerAdjustment] != 0) {
        			return rootPlayer == 1 ? i + 1 : (6 - i);
        		}
        	}
    	}

//    	System.out.println(rootPlayer == 1 ? "[South]" : "[North]" + " Best Move: " + (rootPlayer == 1 ? moveOfNode[childNodes[rootMemoryIndex].get(bestNode)] + 1 : (6 - moveOfNode[childNodes[rootMemoryIndex].get(bestNode)])));
//    	System.out.println("Score: " + (highScore + 1) / 2);
//    	System.out.println((double) simulations / (timeEnd - timeBegin) + " kN/s");
//		System.exit(0);
		return rootPlayer == 1 ? moveOfNode[childNodes[rootMemoryIndex].get(bestNode)] + 1 : (6 - moveOfNode[childNodes[rootMemoryIndex].get(bestNode)]); //Adjust for the actual board domain
	}

	public static void setRootBoard(int[] board) {
		for (int i = 0; i < board.length; i++) {
			rootBoard[i] = board[i];
		}
	}

	public static void setCurrentBoard() {
		for (int i = 0; i < rootBoard.length; i++) {
			currentBoard[i] = rootBoard[i];
		}
	}

	public static boolean terminal(int[] board) {
//		for (int i = 0; i < pitNum; i++) {
//			if (board[i] != 0) break;
//			if (i == pitNum - 1) return true;
//		}
//
//		for (int i = pitNum + 1; i < 2 * pitNum + 1; i++) {
//			if (board[i] != 0) break;
//			if (i == 2 * pitNum) return true;
//		}
//
		int stoneSum = 0;
		int sum = 0;

		for (int i = 0; i < pitNum; i++) {
			sum += board[i];
		}

		if (sum == 0) return true;
		stoneSum += sum;
		sum = 0;

		for (int i = pitNum + 1; i < 2 * pitNum + 1; i++) {
			sum += board[i];
		}
		if (sum == 0) return true;
		stoneSum += sum;

		if (board[pitNum] > board[board.length - 1] + stoneSum || board[board.length - 1] > board[pitNum] + stoneSum) return true;
		return false;
	}

	public static boolean fullyExpanded() { //Is there still a move that isn't in the tree?
    	if (fullyExpanded[currentMemoryIndex]) return true;

    	boolean[] possibleMoves = new boolean[pitNum];
    	for (int i = 0; i < possibleMoves.length; i++) possibleMoves[i] = true;

    	int numberOfPossibleMoves = pitNum;

    	//Nodes that already exist are ignored
    	for (int i = 0; i < childNodes[currentMemoryIndex].size(); i++) {
    		int child = moveOfNode[childNodes[currentMemoryIndex].get(i)];
    		possibleMoves[child] = false;
    		numberOfPossibleMoves--;
    	}

    	//Illegal moves are ignored
    	int playerAdjustment = currentPlayer == 1 ? 0 : pitNum + 1; //To adjust for which player it is when looking at the board
    	for (int i = 0; i < pitNum; i++) {
    		if (possibleMoves[i] == false) continue;
    		if (currentBoard[i + playerAdjustment] == 0) {
    			possibleMoves[i] = false;
    			numberOfPossibleMoves--;
    		}
    	}

    	if (numberOfPossibleMoves == 0) {
    		fullyExpanded[currentMemoryIndex] = true;
    		return true;
    	}

    	if (numberOfPossibleMoves == 1) fullyExpanded[currentMemoryIndex] = true;

    	//Select move to expand on random
    	int i;
    	int randomExpand = randomNum.nextInt(numberOfPossibleMoves) + 1;

     	for (i = 0; i < randomExpand; i++) {
    		if (!possibleMoves[i]) randomExpand++;
    	}

    	i -= 1;
    	currentMove = i;
    	return false;
	}

	public static int expand() {
    	if (memoryUsed) { //Do not expand and keep the node as is.
    		System.out.println("MemoryUsed");
    		currentPlayer = currentPlayer == 1 ? 2 : 1; //Since no child is created, player stays the same
    		return currentMemoryIndex;
    	}

    	int newMemoryIndex = 0; //Position for new memory

    	//Is the for loop necessary? Use memoryCursor + 1 instead?
    	for (int i = memoryCursor; i < size; i++) {
    		memoryCursor++;

    		if (i == size - 1) memoryUsed = true; //No need to return currentMemoryPosition since size - 1 must be available

    		if (parentNodes[i] == -1) {
    			newMemoryIndex = i;
    			break;
    		}
    	}

		updateBoard(currentMove, currentPlayer == 1 ? true : false, currentBoard);
		if (repeatMove) lastSowStore[newMemoryIndex] = true;

		childNodes[currentMemoryIndex].add(newMemoryIndex);
		parentNodes[newMemoryIndex] = currentMemoryIndex;
		moveOfNode[newMemoryIndex] = currentMove;

//		printBoard(currentBoard);

//		System.out.println("New Memory Index " + newMemoryIndex);
		return newMemoryIndex;
	}

	public static int bestChild() {
		double bestChildScore = -2;
		int bestChild = -1;

		for (int i = 0; i < childNodes[currentMemoryIndex].size(); i++) {
			double score = winScore * winCount[childNodes[currentMemoryIndex].get(i)] + lossScore * lossCount[childNodes[currentMemoryIndex].get(i)] + drawScore * drawCount[childNodes[currentMemoryIndex].get(i)];
			int visits = totalVisits[childNodes[currentMemoryIndex].get(i)];
			double percentScore = score / visits;
			int parentVisits = totalVisits[currentMemoryIndex];
			double exploration = Math.log(parentVisits) / visits;
			double childScore = percentScore + Math.sqrt(exploration);

			if (childScore > bestChildScore) {
				bestChildScore = childScore;
				bestChild = i;
			}
		}

		updateBoard(moveOfNode[childNodes[currentMemoryIndex].get(bestChild)], currentPlayer == 1 ? true : false, currentBoard);
		if (lastSowStore[childNodes[currentMemoryIndex].get(bestChild)]) repeatMove = true; //If the last move is a sow, the player moves again
    	currentMemoryIndex = childNodes[currentMemoryIndex].get(bestChild);
		return currentMemoryIndex;
	}

	public static void updateBoard(int move, boolean player, int[] board) {
		int sowLocation = move;

		if (!player) sowLocation += pitNum + 1;

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
					if (easyCapture || (!easyCapture && board[Math.abs(2 * pitNum - sowLocation)] != 0)) {
						board[sowLocation < pitNum ? pitNum : 2 * pitNum + 1] += 1 + board[Math.abs(2 * pitNum - sowLocation)];
						board[Math.abs(2 * pitNum - sowLocation)] = 0;
						break;
					}
				}
			}

			board[sowLocation]++;
			sowLocation++;
		}
	}

	public static void captureRemainingPieces(int[] board) { //At the end of the game, all the pieces belonging to one's side are captured.
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

	public static void printBoard(int[] board) {
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
}
