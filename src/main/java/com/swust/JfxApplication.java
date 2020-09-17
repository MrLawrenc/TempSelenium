package com.swust;

import com.swust.controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * @author : LiuMingyao
 * 2019/12/8 19:53
 */
public class JfxApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/Main.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("投保自动化");

        stage.setOnCloseRequest(e -> {
            controller.destroy();
            Platform.exit();
        });
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}