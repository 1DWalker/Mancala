import javafx.application.Application;
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

public class GUI extends Application implements EventHandler<ActionEvent> {

	static int pitNum = 6; //Number of boardImages per side
	static int stoneNum = 1; //Number of starting stones per boardImage
	static int[] board = new int[pitNum * 2 + 2];
	/* boardImage representation
	 * 13 | 12 11 10 9 8 7
	 *       0  1  2 3 4 5 | 6
	 */
	static boolean player = true;
	static boolean repeatMove;

	//GUI
	static Image boardImage = new Image("board.jpg");
	static Image storeImage = new Image("store.png");
	static Image pitImage = new Image("pit.png");
	static ImageView boardImageView = new ImageView();

	static ImageView storeImageNorthView = new ImageView();
	static Text storeImageNorthText = new Text();
	static StackPane storeImageNorthTextPane = new StackPane();
	static Pane storeImageNorthPane = new Pane(); //To contain the north storeImage and stone images

	static ImageView storeImageSouthView = new ImageView();
	static Text storeImageSouthText = new Text();
	static StackPane storeImageSouthTextPane = new StackPane();
	static Pane storeImageSouthPane = new Pane(); //To contain the south storeImage and stone images

	static ImageView[] pitImageView = new ImageView[2 * GameBase.pitNum];
	static Text[] pitText = new Text[2 * GameBase.pitNum];
	static StackPane[] pitTextPane = new StackPane[pitText.length];
	static Button[] pitButton = new Button[2 * GameBase.pitNum];
	static Pane[] pitPane = new Pane[2 * GameBase.pitNum]; //To contain the pit and stone images

	public static void main(String[] args) {
		setBoard();
		launch(args);
	}

	@Override
	public void start (Stage primaryStage) throws Exception {
		String css = this.getClass().getResource("css.css").toExternalForm();
		double gap = (boardImage.getWidth() - 2 * (30 + storeImage.getWidth()) - pitImage.getWidth() * GameBase.pitNum) / (GameBase.pitNum + 1); //The space between pits

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
			if (player & i >= pitButton.length / 2) pitButton[i].setDisable(true);
			pitButton[i].setId("Button");

		}

		for (int i = 0; i < pitImageView.length; i++) {
			pitPane[i] = new Pane();
			pitPane[i].setPrefSize(pitImage.getWidth() + gap, 328 / 2);

			pitImageView[i].setLayoutX(gap / 2);
			if (i < pitImageView.length / 2) pitImageView[i].setLayoutY(30 + 20);
			else pitImageView[i].setLayoutY(30);
			pitPane[i].getChildren().add(pitImageView[i]);

			if (i < pitImageView.length / 2) pitTextPane[i].setLayoutY(30 + 20 + pitImage.getHeight());
			pitPane[i].getChildren().add(pitTextPane[i]);

			pitButton[i].setLayoutX(gap / 2);
			if (i < pitImageView.length / 2) pitButton[i].setLayoutY(50);
			else pitButton[i].setLayoutY(30);
			pitPane[i].getChildren().add(pitButton[i]);
		}

		for (int i = 0; i < pitImageView.length / 2; i++) pitPane[i].relocate(118 + gap / 2 + (gap + pitImage.getWidth()) * i, 328 / 2);
		for (int i = pitImageView.length - 1; i >= pitImageView.length / 2; i--) pitPane[i].relocate(118 + gap / 2 + (gap + pitImage.getWidth()) * (pitImageView.length - 1 - i), 0);

		Pane rootPane = new Pane();
		rootPane.getChildren().add(boardImageView);
		rootPane.getChildren().add(storeImageNorthPane);
		rootPane.getChildren().add(storeImageSouthPane);
		for (int i = 0; i < pitPane.length; i++) rootPane.getChildren().add(pitPane[i]);

		Scene scene = new Scene(rootPane, 862, 328); //dimensions of boardImage.jpg
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
			updateBoard(Integer.parseInt(button.getText()));
			printBoard();
			updateGUI();
			if (!repeatMove) {
				player = player ? false : true;
				for (int i = 0; i < pitButton.length; i++) {
					if (player) {
						if (i < pitButton.length / 2) pitButton[i].setDisable(false);
						else pitButton[i].setDisable(true);
					}

					if (!player) {
						if (i >= pitButton.length / 2) pitButton[i].setDisable(false);
						else pitButton[i].setDisable(true);
					}
				}
			}

			repeatMove = false;

			if (terminal()) {
				System.out.println("terminal");
				captureRemainingPieces();
				printBoard();
				updateGUI();
				for (int i = 0; i < pitButton.length; i++) pitButton[i].setDisable(true);
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

	public static void updateGUI() {
		for (int i = 0; i < GameBase.pitNum; i++) {
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