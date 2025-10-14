// src/main/java/com/example/demo/view/AcompanhamentoView.java
package com.example.demo.view;

import com.example.demo.model.NaoConformidade;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AcompanhamentoView {

    public final TableView<NaoConformidade> tabelaNCs;

    public AcompanhamentoView(ObservableList<NaoConformidade> listaNaoConformidades) {
        this.tabelaNCs = new TableView<>();
        this.tabelaNCs.setItems(listaNaoConformidades);

        TableColumn<NaoConformidade, String> auditorCol = new TableColumn<>("Auditor");
        auditorCol.setCellValueFactory(new PropertyValueFactory<>("auditor"));

        TableColumn<NaoConformidade, String> responsavelCol = new TableColumn<>("Responsável");
        responsavelCol.setCellValueFactory(new PropertyValueFactory<>("responsavel"));

        TableColumn<NaoConformidade, String> ncCol = new TableColumn<>("NC");
        ncCol.setCellValueFactory(new PropertyValueFactory<>("nc"));
        ncCol.setPrefWidth(250);

        TableColumn<NaoConformidade, String> situacaoCol = new TableColumn<>("Situação da NC");
        situacaoCol.setCellValueFactory(new PropertyValueFactory<>("situacao"));

        TableColumn<NaoConformidade, Void> acoesCol = new TableColumn<>("Ações");
        acoesCol.setPrefWidth(300);

        this.tabelaNCs.getColumns().addAll(auditorCol, responsavelCol, ncCol, situacaoCol, acoesCol);
    }

    public VBox getView() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().add(this.tabelaNCs);
        return root;
    }
}