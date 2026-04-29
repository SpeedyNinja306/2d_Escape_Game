import javafx.application.Application;
import javafx.stage.Stage;


public class main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        GamePanel gamePanel = new GamePanel();
        gamePanel.setupScene(primaryStage);
        gamePanel.startGameLoop();

    }

    static void main(String[] args) {
        launch(args);
    }
}
