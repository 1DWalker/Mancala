import javafx.application.Application;
import javafx.scene.Scene;
//import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class GUI extends Application implements EventHandler<ActionEvent> {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start (Stage primaryStage) {
		String css = this.getClass().getResource("css.css").toExternalForm();
		//Board
		Image board = new Image("board.jpg");
		ImageView boardView = new ImageView();
		boardView.setImage(board);

		Image store = new Image("store.png");

		//North store
		ImageView storeNorthView = new ImageView();
		storeNorthView.setImage(store);
		storeNorthView.relocate(43, (board.getHeight() - store.getHeight()) / 2);

		Text storeNorthText = new Text("0");
		storeNorthText.setId("storeText");

		StackPane storeNorthTextPane = new StackPane();
		storeNorthTextPane.setPrefSize(30, 30);
		storeNorthTextPane.getChildren().add(storeNorthText);
		storeNorthTextPane.relocate(13, (board.getHeight() - 20) / 2);

		Pane storeNorthPane = new Pane(); //To contain the north store and stone images
		storeNorthPane.getChildren().addAll(storeNorthView, storeNorthTextPane);

		//South store
		ImageView storeSouthView = new ImageView();
		storeSouthView.setImage(store);
		storeSouthView.relocate(board.getWidth() - 43 - store.getWidth(), (board.getHeight() - store.getHeight()) / 2);

		Text storeSouthText = new Text("0");
		storeSouthText.setId("storeText");

		StackPane storeSouthTextPane = new StackPane();
		storeSouthTextPane.setPrefSize(30, 30);
		storeSouthTextPane.getChildren().add(storeSouthText);
		storeSouthTextPane.relocate(board.getWidth() - 43, (board.getHeight() - 20) / 2);

		Pane storeSouthPane = new Pane(); //To contain the south store and stone images
		storeSouthPane.getChildren().addAll(storeSouthView, storeSouthTextPane);

		//Pits
		Image pitImage = new Image("pit.png");

		ImageView[] pitImageView = new ImageView[2 * GameBase.pitNum];

		for (int i = 0; i < pitImageView.length; i++) {
			pitImageView[i] = new ImageView();
			pitImageView[i].setImage(pitImage);
		}

		double gap = (board.getWidth() - 2 * (43 + store.getWidth()) - pitImage.getWidth() * pitImageView.length / 2) / (pitImageView.length / 2 + 1);
		for (int i = 0; i < pitImageView.length / 2; i++) pitImageView[i].relocate(43 + gap + store.getWidth() + (gap + pitImage.getWidth()) * i, 226);
		for (int i = pitImageView.length - 1; i >= pitImageView.length / 2; i--) pitImageView[i].relocate(43 + gap + store.getWidth() + (gap + pitImage.getWidth()) * (pitImageView.length - 1 - i), 43);

		Pane[] pitPane = new Pane[2 * GameBase.pitNum]; //To contain the pit and stone images
		for (int i = 0; i < pitImageView.length; i++) {
			pitPane[i] = new Pane();
			pitPane[i].getChildren().add(pitImageView[i]);
		}

		Pane rootPane = new Pane();
		rootPane.getChildren().add(boardView);
		rootPane.getChildren().add(storeNorthPane);
		rootPane.getChildren().add(storeSouthPane);
		for (int i = 0; i < pitPane.length; i++) rootPane.getChildren().add(pitPane[i]);

		Scene scene = new Scene(rootPane, 886, 361); //dimensions of board.jpg
		scene.getStylesheets().add(css);

		primaryStage.setTitle("Mancala");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void handle (ActionEvent event) {
		System.out.println("clicked");
	}
}