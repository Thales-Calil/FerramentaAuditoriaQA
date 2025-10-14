// src/main/java/com/example/demo/view/AuditoriaView.java
package com.example.demo.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuditoriaView {

    // Deixamos os componentes públicos para o Controller poder acessá-los
    public final TextField auditorField = new TextField();
    public final TextField responsavelField = new TextField();
    public final VBox checklistBox = new VBox(5);
    public final Label aderenciaLabel = new Label("Aderência: 0%");
    public final Button registrarButton = new Button("Registrar Auditoria");
    public final List<ToggleGroup> gruposRespostas = new ArrayList<>();

    public VBox criarTabAuditoria() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        auditorField.setPromptText("Nome do Auditor");
        responsavelField.setPromptText("Nome do Responsável");

        Label checklistLabel = new Label("Checklist de Qualidade");
        checklistLabel.setStyle("-fx-font-weight: bold;");

        List<String> perguntas = Arrays.asList(
                "1. O bibliotecário verificou se o estudante está inelegível?",
                "2. O bibliotecário solicitou a carteirinha?",
                "3. O bibliotecário informou sobre o prazo de devolução?"
        );

        for (String pergunta : perguntas) {
            HBox perguntaBox = new HBox(10);
            Label label = new Label(pergunta);
            label.setPrefWidth(350);

            ToggleGroup grupo = new ToggleGroup();
            gruposRespostas.add(grupo);

            RadioButton radioSim = new RadioButton("Sim");
            radioSim.setUserData("Sim");
            radioSim.setToggleGroup(grupo);
            radioSim.setSelected(true);

            RadioButton radioNao = new RadioButton("Não");
            radioNao.setUserData("Não");
            radioNao.setToggleGroup(grupo);

            RadioButton radioNA = new RadioButton("Não se Aplica");
            radioNA.setUserData("Não se Aplica");
            radioNA.setToggleGroup(grupo);

            perguntaBox.getChildren().addAll(label, radioSim, radioNao, radioNA);
            checklistBox.getChildren().add(perguntaBox);
        }

        root.getChildren().addAll(new HBox(10, auditorField, responsavelField), checklistLabel, checklistBox, registrarButton, aderenciaLabel);
        return root;
    }
}