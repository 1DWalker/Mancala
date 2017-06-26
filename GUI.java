import javafx.application.Application;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.util.Duration;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class GUI extends Application implements EventHandler<ActionEvent> {
	static Random randomNum = new Random();

	static int pitNum = 6; //Number of boardImages per side
	static int stoneNum = 6; //Number of starting stones per boardImage
	static int[] board = new int[pitNum * 2 + 2];
	/* boardImage representation
	 * 13 | 12 11 10 9 8 7
	 *       0  1  2 3 4 5 | 6
	 */
	static boolean player = true;
	static boolean repeatMove;

	//GUI
	static MenuItem newGame = new MenuItem("New Game");
	static Menu menu = new Menu("File");
	static MenuBar menuBar = new MenuBar();

	static Image boardImage = new Image("board.jpg");
	static Image storeImage = new Image("store.png");
	static Image pitImage = new Image("pit.png");
	static Image ballImage = new Image("ball.png");

	static ImageView boardImageView = new ImageView();

	static ImageView storeImageNorthView = new ImageView();
	static Text storeImageNorthText = new Text();
	static StackPane storeImageNorthTextPane = new StackPane();
	static Pane storeImageNorthPane = new Pane(); //To contain the north storeImage and stone images

	static ImageView storeImageSouthView = new ImageView();
	static Text storeImageSouthText = new Text();
	static StackPane storeImageSouthTextPane = new StackPane();
	static Pane storeImageSouthPane = new Pane(); //To contain the south storeImage and stone images

	static ImageView[] pitImageView = new ImageView[2 * pitNum];
	static Pane[] pitImagePane = new Pane[2 * pitNum];
	static Text[] pitText = new Text[2 * pitNum];
	static StackPane[] pitTextPane = new StackPane[pitText.length];
	static Button[] pitButton = new Button[2 * pitNum];
	static Pane[] pitPane = new Pane[2 * pitNum]; //To contain the pit and stone images

	static ImageView[] ballImageView = new ImageView[2 * pitNum * stoneNum];
	static List<Integer>[] ballIndexes = new ArrayList[2 * pitNum + 2];

	static Text gameText = new Text();
	static StackPane gameTextPane = new StackPane();

	static ParallelTransition sowTransition = new ParallelTransition();
	static ParallelTransition captureTransition = new ParallelTransition();

	public static void main(String[] args) {
		setBoard();
		launch(args);
	}

	@Override
	public void start (Stage primaryStage) throws Exception {
		String css = this.getClass().getResource("css.css").toExternalForm();
		double gap = (boardImage.getWidth() - 2 * (30 + storeImage.getWidth()) - pitImage.getWidth() * pitNum) / (pitNum + 1); //The space between pits

		//Menu
		newGame.setOnAction(this);
		menu.getItems().add(newGame);
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		menuBar.getMenus().add(menu);

		//boardImage
		boardImageView.setImage(boardImage);

		//North storeImage
		storeImageNorthView.setImage(storeImage);
		storeImageNorthView.setLayoutX(30);
		storeImageNorthView.setLayoutY((boardImage.getHeight() - storeImage.getHeight()) / 2);

		storeImageNorthText.setText(Integer.toString(board[board.length - 1]));
		storeImageNorthText.setId("Text");

		storeImageNorthTextPane.setPrefSize(30, 20);
		storeImageNorthTextPane.getChildren().add(storeImageNorthText);
		storeImageNorthTextPane.setLayoutY((boardImage.getHeight() - 20) / 2);

		storeImageNorthPane.setPrefSize(118 + gap / 2, 328);
		storeImageNorthPane.getChildren().addAll(storeImageNorthView, storeImageNorthTextPane);

		//South storeImage
		storeImageSouthView.setImage(storeImage);
		storeImageSouthView.setLayoutX(gap / 2);
		storeImageSouthView.setLayoutY((boardImage.getHeight() - storeImage.getHeight()) / 2);

		storeImageSouthText.setText(Integer.toString(board[pitNum]));
		storeImageSouthText.setId("Text");

		storeImageSouthTextPane.setPrefSize(30, 20);
		storeImageSouthTextPane.getChildren().add(storeImageSouthText);
		storeImageSouthTextPane.setLayoutX(88 + gap / 2);
		storeImageSouthTextPane.setLayoutY((boardImage.getHeight() - 20) / 2);

		storeImageSouthPane.setPrefSize(118 + gap / 2, 328);
		storeImageSouthPane.relocate(boardImage.getWidth() - 118 - gap / 2, 0);
		storeImageSouthPane.getChildren().addAll(storeImageSouthView, storeImageSouthTextPane);

		//Pits
		for (int i = 0; i < pitImageView.length; i++) { //Images
			pitImageView[i] = new ImageView();
			pitImageView[i].setImage(pitImage);
		}

		for (int i = 0; i < pitImagePane.length; i++) {
			pitImagePane[i] = new Pane();
			pitImagePane[i].setPrefSize(pitImage.getWidth(), pitImage.getHeight());
			pitImagePane[i].getChildren().add(pitImageView[i]);
		}

		for (int i = 0; i < pitText.length; i++) { //Text
			pitText[i] = new Text(Integer.toString(i < pitText.length / 2 ? board[i] : board[i + 1]));
			pitText[i].setId("Text");
		}

		for (int i = 0; i < pitTextPane.length; i++) {
			pitTextPane[i] = new StackPane();
			pitTextPane[i].setPrefSize(pitImage.getWidth() + gap, 20);
			pitTextPane[i].getChildren().add(pitText[i]);
		}

		for (int i = 0; i < pitButton.length; i++) {
			pitButton[i] = new Button(Integer.toString(i < pitNum ? i : i + 1));
			pitButton[i].setShape(new Circle(88));
			pitButton[i].setPrefSize(88, 88);
			pitButton[i].setOnAction(this);
			pitButton[i].setId("Button");
		}

		for (int i = 0; i < ballIndexes.length; i++) ballIndexes[i] = new ArrayList<Integer>(); //Initialize ballIndexes

		//Put balls in each pit
		for (int i = 0; i < ballImageView.length; i++) {
			ballImageView[i] = new ImageView();
			ballImageView[i].setImage(ballImage);
		}

		for (int i = 0; i < pitImageView.length; i++) {
			pitPane[i] = new Pane();
			pitPane[i].setPrefSize(pitImage.getWidth() + gap, 328 / 2);

			pitImagePane[i].setLayoutX(gap / 2);
			if (i < pitImagePane.length / 2) pitImagePane[i].setLayoutY(30 + 20);
			else pitImagePane[i].setLayoutY(30);
			pitPane[i].getChildren().add(pitImagePane[i]);

			if (i < pitImageView.length / 2) pitTextPane[i].setLayoutY(30 + 20 + pitImage.getHeight());
			pitPane[i].getChildren().add(pitTextPane[i]);

			pitButton[i].setLayoutX(gap / 2);
			if (i < pitImageView.length / 2) pitButton[i].setLayoutY(50);
			else pitButton[i].setLayoutY(30);
			pitPane[i].getChildren().add(pitButton[i]);
		}

		for (int i = 0; i < pitImageView.length / 2; i++) pitPane[i].relocate(118 + gap / 2 + (gap + pitImage.getWidth()) * i, 328 / 2);
		for (int i = pitImageView.length - 1; i >= pitImageView.length / 2; i--) pitPane[i].relocate(118 + gap / 2 + (gap + pitImage.getWidth()) * (pitImageView.length - 1 - i), 0);

		//Add balls
		for (int i = 0; i < ballImageView.length; i++) {
			//Add balls
			ballImageView[i].setLayoutX(pitPane[i % (2 * pitNum)].getLayoutX() + pitImagePane[i % (2 * pitNum)].getLayoutX() + (pitImage.getWidth() - ballImage.getWidth()) / 2 + 88 * 0.55 * (randomNum.nextDouble() - 0.55));
			ballImageView[i].setLayoutY(pitPane[i % (2 * pitNum)].getLayoutY() + pitImagePane[i % (2 * pitNum)].getLayoutY() + (pitImage.getHeight() - ballImage.getHeight()) / 2 + 88 * 0.55 * (randomNum.nextDouble() - 0.55));

			ballIndexes[i % (2 * pitNum) < pitNum ? i % (2 * pitNum) : i % (2 * pitNum) + 1].add(i);
		}

		gameText.setText("");
		gameText.setId("Text");
		gameTextPane.getChildren().add(gameText);
		gameTextPane.setPrefSize(862, 328);

		Pane gamePane = new Pane();
		gamePane.getChildren().add(boardImageView);
		gamePane.getChildren().add(storeImageNorthPane);
		gamePane.getChildren().add(storeImageSouthPane);
		gamePane.getChildren().add(gameTextPane);
		for (int i = 0; i < pitPane.length; i++) gamePane.getChildren().add(pitPane[i]);
		for (int i = 0; i < ballImageView.length; i++) gamePane.getChildren().add(ballImageView[i]);

		gamePane.setLayoutY(25);

		Pane mainPane = new Pane();
		mainPane.getChildren().add(menuBar);
		mainPane.getChildren().add(gamePane);

		Scene scene = new Scene(mainPane, 862, 353); //dimensions of boardImage.jpg
		scene.getStylesheets().add(css);

		primaryStage.setTitle("Mancala");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void handle (ActionEvent event) {
		Object object = event.getSource();
		if (object instanceof Button) {
			Button button = (Button) object;
			System.out.println(button.getText());

			if (!(player && Integer.parseInt(button.getText()) < pitNum) && !(!player && Integer.parseInt(button.getText()) > pitNum)) {
				System.out.println("Wrong player's move.");
				return;
			}
			if (board[Integer.parseInt(button.getText())] == 0) {
				System.out.println("blank button");
				return;
			}

			for (int i = 0; i < pitButton.length; i++) { //To avoid multiple button presses
				pitButton[i].setDisable(true);
			}

			updateBoard(Integer.parseInt(button.getText()));
			printBoard();
			updateGUI();

			captureTransition.setOnFinished((e) -> { //This animation is run in the updateGUI() function
				if (!repeatMove) {
					player = player ? false : true;
				}

				repeatMove = false;

				if (terminal()) {
					captureRemainingPieces();
					printBoard();
					updateGUI();

					if (board[pitNum] > board[2 * pitNum + 1]) gameText.setText("Player South wins by " + (board[pitNum] - board[2 * pitNum + 1]) + " Points!");
					else if (board[pitNum] == board[2 * pitNum + 1]) gameText.setText("Draw!");
					else gameText.setText("Player North wins by " + (board[2 * pitNum + 1] - board[pitNum]) + " Points!");
				}

				for (int i = 0; i < pitButton.length; i++) {
					pitButton[i].setDisable(false);
				}
			});
		} else if (object instanceof MenuItem) {
			MenuItem menuItem = (MenuItem) object;
			if (menuItem.getText().equals("New Game")) {
				System.out.println("restart");
				player = true;
				setBoard();
				printBoard();
				updateGUI();

				sowTransition = new ParallelTransition();

				for (int i = 0; i < ballIndexes.length; i++) {
					int size = ballIndexes[i].size();
					for (int k = 0; k < size; k++) {
						int end = ballIndexes[i].get(k) % (2 * pitNum) < pitNum ? ballIndexes[i].get(k) % (2 * pitNum) : ballIndexes[i].get(k) % (2 * pitNum) + 1;
						if (ballIndexes[i].size() == 0) break;
						addAnimation(k, i, end, sowTransition);
						k--;
						size--;
					}
				}
				sowTransition.play();
				gameText.setText("");
			}
		}
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

	public static void captureRemainingPieces() { //At the end of the game, all the pieces belonging to one's side are captured.
		captureTransition = new ParallelTransition();

		for (int i = 0; i < board.length; i++) {
			if (i == pitNum || i == 2 * pitNum + 1) continue;
			if (i < pitNum) {
				board[pitNum] += board[i];
				board[i] = 0;
				while (ballIndexes[i].size() != 0) addAnimation(0, i, pitNum, captureTransition);
			} else {
				board[2 * pitNum + 1] += board[i];
				board[i] = 0;
				while (ballIndexes[i].size() != 0) addAnimation(0, i, 2 * pitNum + 1, captureTransition);
			}
		}

		captureTransition.play();
	}

	public static void updateGUI() {
		for (int i = 0; i < pitNum; i++) {
			pitText[i].setText(Integer.toString(board[i]));
			pitText[i + 6].setText(Integer.toString(board[i + 7]));
		}

		storeImageNorthText.setText(Integer.toString(board[board.length - 1]));
		storeImageSouthText.setText(Integer.toString(board[pitNum]));
	}

	public static void setBoard() {
		for (int i = 0; i < pitNum; i++) {
			board[i] = stoneNum;
			board[2 * pitNum - i] = stoneNum;
		}

		board[pitNum] = 0;
		board[2 * pitNum + 1] = 0;
	}

	public static void updateBoard(int move) {
		int sowLocation = move;

		int numberOfStones = board[sowLocation];
		board[sowLocation] = 0;
		sowLocation++;

		sowTransition = new ParallelTransition();
		captureTransition = new ParallelTransition();
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

					//Animation for last stone sown
					addAnimation(0, move, sowLocation, sowTransition);
					addAnimation(0, sowLocation, move < pitNum ? pitNum : 2 * pitNum + 1, captureTransition);

					//Animation for the captured opposing stones
					while (ballIndexes[Math.abs(2 * pitNum - sowLocation)].size() != 0) {
						addAnimation(0, Math.abs(2 * pitNum - sowLocation), move < pitNum ? pitNum : 2 * pitNum + 1, captureTransition);
					}
					break;
				}
			}

			//Animation
			addAnimation(0, move, sowLocation, sowTransition);

			board[sowLocation]++;
			sowLocation++;
		}

		sowTransition.play();
		sowTransition.setOnFinished((e) -> {
			captureTransition.play();
		});
	}

	public static void addAnimation(int index, int start, int end, ParallelTransition parallelTransition) { //Start and end are positions of the board
		if (ballIndexes[start].size() == 0) {
			System.out.println("No balls");
			return;
		}

		int nodeNumber = ballIndexes[start].get(index);
		ballIndexes[start].remove(index);
		ballIndexes[end].add(nodeNumber);
		int pitIndex = -1;
		boolean storeSouth = false;
		boolean storeNorth = false;
		if (end < pitNum) pitIndex = end;
		else if (end == pitNum) storeSouth = true;
		else if (end == 2 * pitNum + 1) storeNorth = true;
		else pitIndex = end - 1;

		TranslateTransition transition = new TranslateTransition();
		transition.setDuration(Duration.millis(800));
		transition.setNode(ballImageView[nodeNumber]);
		if (!storeSouth & !storeNorth) {
			transition.setToX(pitPane[pitIndex].getLayoutX() + pitImagePane[pitIndex].getLayoutX() + (pitImage.getWidth() - ballImage.getWidth()) / 2 + 88 * 0.55 * (randomNum.nextDouble() - 0.55) - ballImageView[nodeNumber].getLayoutX());
			transition.setToY(pitPane[pitIndex].getLayoutY() + pitImagePane[pitIndex].getLayoutY() + (pitImage.getHeight() - ballImage.getHeight()) / 2 + 88 * 0.55 * (randomNum.nextDouble() - 0.55) - ballImageView[nodeNumber].getLayoutY());
		} else {
			if (storeSouth) {
				transition.setToX(storeImageSouthPane.getLayoutX() + storeImageSouthView.getLayoutX() + (storeImage.getWidth() - ballImage.getWidth()) / 2 + 88 * 0.75 * (randomNum.nextDouble() - 0.55) - ballImageView[nodeNumber].getLayoutX());
				transition.setToY(storeImageSouthPane.getLayoutY() + storeImageSouthView.getLayoutY() + (storeImage.getHeight() - ballImage.getHeight()) / 2 + 179 * 0.75 * (randomNum.nextDouble() - 0.55) - ballImageView[nodeNumber].getLayoutY());
			} else {
				transition.setToX(storeImageNorthPane.getLayoutX() + storeImageNorthView.getLayoutX() + (storeImage.getWidth() - ballImage.getWidth()) / 2 + 88 * 0.75 * (randomNum.nextDouble() - 0.55) - ballImageView[nodeNumber].getLayoutX());
				transition.setToY(storeImageNorthPane.getLayoutY() + storeImageNorthView.getLayoutY() + (storeImage.getHeight() - ballImage.getHeight()) / 2 + 179 * 0.75 * (randomNum.nextDouble() - 0.55) - ballImageView[nodeNumber].getLayoutY());
			}
		}
		parallelTransition.getChildren().add(transition);
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
}