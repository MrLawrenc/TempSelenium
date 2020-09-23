package com.github.mrlawrenc.utils;

import com.github.mrlawrenc.entity.conf.CmdEnum;
import com.github.mrlawrenc.entity.conf.StepCommand;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
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
public class RealTimeEditTextFieldCell extends TextFieldTableCell<StepCommand, String> {

    private TextField textField;
    private TableColumn<StepCommand, String> column;
    private Label label = new Label();
    private HBox box;
    private Tooltip tooltip;

    public RealTimeEditTextFieldCell(TableColumn<StepCommand, String> column) {
        super(new DefaultStringConverter());
        this.column = column;

       /* AnchorPane pane = (AnchorPane) column.getTableView().getParent();
        pane.getChildren().addAll(label);*/
    }

    @Override
    public void startEdit() {
        super.startEdit();
        try {
            if (Objects.isNull(textField)) {
                //反射拿值
                Field field = TextFieldTableCell.class.getDeclaredField("textField");
                field.setAccessible(true);
                this.textField = (TextField) field.get(this);

                //输入参数过滤
/*                textField.setTextFormatter(new TextFormatter<>(change -> {
                    if (change.getText().matches("[a-z]*]")) {
                        return change;
                    }
                    return null;
                }));*/


                textField.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                        if ("location".equals(column.getText())) {
                            CmdEnum[] cmdEnums = CmdEnum.values();
                            StringBuilder cmdList = new StringBuilder();
                            for (CmdEnum cmdEnum : cmdEnums) {
                                if (cmdEnum.getCmd().contains(newValue)) {
                                    cmdList.append(cmdEnum.getCmd()).append("\n");
                                }
                            }

                            label.setLayoutX(textField.getLayoutX() + 10);
                            label.setLayoutY(textField.getLayoutY() - 10);
                            label.setText(cmdList.toString());
                        }
                    }
                });

                textField.setOnKeyPressed(keyEvent -> {
                    if (keyEvent.getCode() == KeyCode.TAB) {
                        System.out.println("##########tab##########");
                    }
                });
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Override
    public void commitEdit(String s) {
        super.commitEdit(s);

        //textField.setTooltip(null);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
    }

    @Override
    public void updateItem(String s, boolean empty) {
        super.updateItem(s, empty);

        if (!empty && StringUtils.isNotEmpty(s)) {
            // this.setTooltip(new Tooltip("提示:" + s));

            //可以放自定义控件
           /* ComboBox<String> box = new ComboBox<>();
            List<String> list = new ArrayList<>();
            list.add("s");
            list.add("d");
            box.setItems(FXCollections.observableList(list));
            Tooltip tooltip = new Tooltip();
            tooltip.setGraphic(box);
            //this.setTooltip(tooltip);*/


        }
    }
}