package com.swust;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLInputElement;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author : LiuMingyao
 * @date : 2019/12/8 19:53
 */
public class MyApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();

        engine.load("http://www.baidu.com");
        AtomicBoolean clicked = new AtomicBoolean(false);
        engine.documentProperty().addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> ov, Document oldDoc, Document doc) {
                if (doc != null) {
                    NodeList input = doc.getElementsByTagName("input");
                    for (int i = 0; i < input.getLength(); i++) {
                        Node item = input.item(i);
                        NamedNodeMap attributes = item.getAttributes();
                        Node node = attributes.getNamedItem("id");
                        Node submitNode = attributes.getNamedItem("value");
                        if (Objects.nonNull(node)) {
                            if ("kw".equals(node.getNodeValue())) {
                                HTMLInputElement search = (HTMLInputElement) item;
                                search.setValue("我是自动填充的百度搜索");
                            }
                        }
                        if (Objects.nonNull(submitNode)) {
                            if ("百度一下".equals(submitNode.getNodeValue()) && !clicked.get()) {
                                clicked.set(true);
                                HTMLInputElement submit = (HTMLInputElement) item;
                                submit.click();
                            }
                        }
                    }
                    System.out.println(doc);
                }
            }
        });


        engine.getLoadWorker().stateProperty().addListener(
                (obs, oldValue, newValue) -> {
                    System.out.println(newValue);
                    if (newValue == Worker.State.SUCCEEDED) {
                        System.out.println("########finished loading");
                        String html = (String) engine
                                .executeScript("document.documentElement.outerHTML");
                        System.out.println(html);

                    }
                });

        engine.getLoadWorker().stateProperty()
                .addListener((obs, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        System.out.println("===========finished loading html");
                        org.w3c.dom.Document xmlDom = engine.getDocument();

                        try {
                            TransformerFactory transformerFactory = TransformerFactory
                                    .newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            StringWriter stringWriter = new StringWriter();
                            transformer.transform(new DOMSource(engine.getDocument()),
                                    new StreamResult(stringWriter));
                            String xml = stringWriter.getBuffer().toString();
                            System.out.println(xml);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }); // addListener()


        AnchorPane root = new AnchorPane();
        root.getChildren().add(webView);

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setHeight(600);
        stage.setWidth(1000);

        //自适应，绑定浏览器和面板
        webView.prefWidthProperty().bind(root.widthProperty());
        webView.prefHeightProperty().bind(root.heightProperty());

        stage.show();


        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                engine.load(null);
                Platform.exit();
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}