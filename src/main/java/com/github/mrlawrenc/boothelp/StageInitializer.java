package com.github.mrlawrenc.boothelp;

import com.github.mrlawrenc.config.JfxConfiguration;
import com.github.mrlawrenc.controller.MainController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author MrLawrenc
 * date  2020/9/21 21:46
 * <p>
 * 加载jfx逻辑
 */
@Component
@Slf4j
public class StageInitializer implements ApplicationListener<StageInitializer.StageReadyEvent> {


    private final JfxConfiguration jfxConfiguration;

    private final ApplicationContext applicationContext;
    private final MainController controller;

    public StageInitializer(ApplicationContext applicationContext, JfxConfiguration jfxConfiguration
            , MainController controller) {
        this.applicationContext = applicationContext;
        this.jfxConfiguration = jfxConfiguration;
        this.controller = controller;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        Stage stage = stageReadyEvent.getStage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(jfxConfiguration.getMainFxml().getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);

            controller.setStage(stage);

            Parent parent = fxmlLoader.load();

            stage.getIcons().add(new Image(jfxConfiguration.getTitleResource().getInputStream()));
            stage.setScene(new Scene(parent));
            stage.setTitle(jfxConfiguration.getStageTitle());
            stage.show();

            //openOther(stage);
        } catch (Exception e) {
            log.error("start jfx fail,will close!", e);
            stage.close();
        }
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }

    @Deprecated
    public void openOther(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/Main.fxml"));
        AnchorPane root = fxmlLoader.load();
        MainController controller = fxmlLoader.getController();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("投保自动化");

        stage.setMaximized(false);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/title.jpg")));

        stage.setOnCloseRequest(e -> {
//            try {
//                controller.destroy();
//            } catch (Exception ex) {
//                log.error("pre destroy fail", ex);
//            }
            Platform.exit();
        });
        stage.show();

    }
}