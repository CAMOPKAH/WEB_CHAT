package ChatForm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainForm extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        Controller controller = loader.getController();

        Parent root = loader.load();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));



        primaryStage.setTitle("WebChat");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
