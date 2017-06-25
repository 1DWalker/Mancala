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

	//GUI
	static Image board = new Image("board.jpg");
	static Image store = new Image("store.png");
	static Image pitImage = new Image("pit.png");
	static ImageView boardView = new ImageView();

	static ImageView storeNorthView = new ImageView();
	static Text storeNorthText = new Text("0");
	static StackPane storeNorthTextPane = new StackPane();
	static Pane storeNorthPane = new Pane(); //To contain the north store and stone images

	static ImageView storeSouthView = new ImageView();
	static Text storeSouthText = new Text("0");
	static StackPane storeSouthTextPane = new StackPane();
	static Pane storeSouthPane = new Pane(); //To contain the south store and stone images

	static ImageView[] pitImageView = new ImageView[2 * GameBase.pitNum];
	static Text[] pitText = new Text[2 * GameBase.pitNum];
	static StackPane[] pitTextPane = new StackPane[pitText.length];
	static Button[] pitButton = new Button[2 * GameBase.pitNum];
	static Pane[] pitPane = new Pane[2 * GameBase.pitNum]; //To contain the pit and stone images

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start (Stage primaryStage) throws Exception {
		String css = this.getClass().getResource("css.css").toExternalForm();
		double gap = (board.getWidth() - 2 * (30 + store.getWidth()) - pitImage.getWidth() * GameBase.pitNum) / (GameBase.pitNum + 1); //The space between pits

		//Board
		boardView.setImage(board);

		//North store
		storeNorthView.setImage(store);
		storeNorthView.setLayoutX(30);
		storeNorthView.setLayoutY((board.getHeight() - store.getHeight()) / 2);

		storeNorthText.setId("Text");

		storeNorthTextPane.setPrefSize(30, 20);
		storeNorthTextPane.getChildren().add(storeNorthText);
		storeNorthTextPane.setLayoutY((board.getHeight() - 20) / 2);

		storeNorthPane.setPrefSize(118 + gap / 2, 328);
		storeNorthPane.getChildren().addAll(storeNorthView, storeNorthTextPane);

		//South store
		storeSouthView.setImage(store);
		storeSouthView.setLayoutX(gap / 2);
		storeSouthView.setLayoutY((board.getHeight() - store.getHeight()) / 2);

		storeSouthText.setId("Text");

		storeSouthTextPane.setPrefSize(30, 20);
		storeSouthTextPane.getChildren().add(storeSouthText);
		storeSouthTextPane.setLayoutX(88 + gap / 2);
		storeSouthTextPane.setLayoutY((board.getHeight() - 20) / 2);

		storeSouthPane.setPrefSize(118 + gap / 2, 328);
		storeSouthPane.relocate(board.getWidth() - 118 - gap / 2, 0);
		storeSouthPane.getChildren().addAll(storeSouthView, storeSouthTextPane);

		//Pits
		for (int i = 0; i < pitImageView.length; i++) { //Images
			pitImageView[i] = new ImageView();
			pitImageView[i].setImage(pitImage);
		}

		for (int i = 0; i < pitText.length; i++) { //Text
			pitText[i] = new Text("0");
			pitText[i].setId("Text");
		}

		for (int i = 0; i < pitTextPane.length; i++) {
			pitTextPane[i] = new StackPane();
			pitTextPane[i].setPrefSize(pitImage.getWidth() + gap, 20);
			pitTextPane[i].getChildren().add(pitText[i]);
		}

		for (int i = 0; i < pitButton.length; i++) {
			pitButton[i] = new Button(Integer.toString(i));
			pitButton[i].setShape(new Circle(88));
			pitButton[i].setPrefSize(88, 88);
			pitButton[i].setOnAction(this);
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
		rootPane.getChildren().add(boardView);
		rootPane.getChildren().add(storeNorthPane);
		rootPane.getChildren().add(storeSouthPane);
		for (int i = 0; i < pitPane.length; i++) rootPane.getChildren().add(pitPane[i]);

		Scene scene = new Scene(rootPane, 862, 328); //dimensions of board.jpg
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
		}
	}

	public static void updateBoard(int[] board) {
		for (int i = 0; i < GameBase.pitNum; i++) {
			pitText[i].setText(Integer.toString(board[i]));
			pitText[i + 6].setText(Integer.toString(i + 6));
		}
	}
}