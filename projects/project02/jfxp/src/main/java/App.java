import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
        primaryStage.setTitle("Napster      0__0");
        primaryStage.setScene(new Scene(root, 650, 650));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}