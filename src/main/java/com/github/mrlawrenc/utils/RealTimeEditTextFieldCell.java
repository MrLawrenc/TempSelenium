package com.github.mrlawrenc.utils;

import com.github.mrlawrenc.entity.conf.CmdEnum;
import com.github.mrlawrenc.entity.conf.StepCommand;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author : MrLawrenc
 * date  2020/9/22 21:56
 * <p>
 * 可以实时监听编辑时的字符变化，便于提示
 * @see TextFieldTableCell#forTableColumn()
 * @see javafx.scene.control.cell.CheckBoxTableCell#forTableColumn(TableColumn)
 * @see javafx.scene.control.cell.ComboBoxTableCell#forTableColumn(StringConverter, Object[])
 */
@Slf4j
public class RealTimeEditTextFieldCell extends TableCell<StepCommand, String> {

    /**
     * 是否有输入提示
     */
    private final boolean msgInputTip;
    /**
     * 提示列表
     */
    private final ObservableList<String> locationList = FXCollections.observableArrayList();

    /**
     * 可编辑的单元格控件
     */
    private final TextField textField;

    private final TableColumn<StepCommand, String> column;
    private final VBox vBox;
    private ListView<String> listView;


    public RealTimeEditTextFieldCell(TableColumn<StepCommand, String> column, boolean msgInputTip) {
        this.column = column;
        this.column.setUserData(this);
        this.vBox = new VBox();

        this.msgInputTip = msgInputTip;
        this.textField = new TextField();
        if (msgInputTip) {
            listView = new ListView<>();
            listView.setEditable(false);
            listView.prefWidthProperty().bind(textField.widthProperty());

            listView.setItems(locationList);
            vBox.getChildren().addAll(textField, listView);
        } else {
            vBox.getChildren().addAll(textField);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();

        textField.setText(getText());

        this.setGraphic(vBox);

        if (Objects.nonNull(listView)) {
            textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                locationList.clear();
                for (CmdEnum cmdEnum : CmdEnum.values()) {
                    if (cmdEnum.getCmd().contains(newValue)) {
                        locationList.add(cmdEnum.getCmd());
                    }
                }
            });
            listView.getSelectionModel().selectedItemProperty().addListener(
                    (ov, oldVal, newVal) -> textField.setText(newVal));

        }
        //绑定快捷键
        textField.setOnKeyPressed(keyEvent -> {

            if (keyEvent.getCode() == KeyCode.DOWN && Objects.nonNull(listView)) {
                int size = listView.getItems().size();
                if (size - 1 == listView.getSelectionModel().getSelectedIndex()) {
                    listView.getSelectionModel().select(0);
                } else {
                    listView.getSelectionModel().select(listView.getSelectionModel().getSelectedIndex() + 1);
                }
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                if (Objects.nonNull(listView)) {
                    int selectedIndex = listView.getSelectionModel().getSelectedIndex();
                    if (selectedIndex == -1) {
                        commitEdit(textField.getText());
                    } else {
                        commitEdit(listView.getSelectionModel().getSelectedItem());
                    }
                } else {
                    commitEdit(textField.getText());
                }
            } else if (keyEvent.getCode() == KeyCode.TAB) {
                cancelEdit();
                Platform.runLater(() -> {
                    ObservableList<TableColumn<StepCommand, ?>> columns = column.getTableView().getColumns();
                    //
                    int i = columns.indexOf(column);
                    if (i + 1 == columns.size()) {
                        i = 0;
                    }


                    //触发下一个单元格编辑事件
                    RealTimeEditTextFieldCell cell = (RealTimeEditTextFieldCell) columns.get(i + 1).getUserData();
                });
            }
        });

    }

    @Override
    public void commitEdit(String s) {
        super.commitEdit(s);
        setText(s);
        textField.cancelEdit();
        this.setGraphic(null);
    }

    @Override
    public void cancelEdit() {
        log.info("cancelEdit");
        super.cancelEdit();
        textField.cancelEdit();
        this.setGraphic(null);
    }


    @Override
    public void updateItem(String s, boolean empty) {
        super.updateItem(s, empty);
        setText(s);
    }
}