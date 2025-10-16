package com.ferramenta.ferramentaAuditoria.view;

import com.ferramenta.ferramentaAuditoria.model.ChecklistData;
import com.ferramenta.ferramentaAuditoria.model.Checklist;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class AuditoriaView {

    public final TextField auditorField = new TextField();
    public final TextField responsavelField = new TextField();
    public final VBox checklistBox = new VBox(5);
    public final Label aderenciaLabel = new Label("Aderência: 0%");
    public final Button registrarButton = new Button("Registrar Auditoria");
    public final List<ToggleGroup> gruposRespostas = new ArrayList<>();

    // ComboBox para selecionar o checklist
    public final ComboBox<Checklist> checklistComboBox = new ComboBox<>();

    public VBox criarTabAuditoria() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        auditorField.setPromptText("Nome do Auditor");
        responsavelField.setPromptText("Nome do Responsável");

        Label checklistLabel = new Label("Checklist de Qualidade");
        checklistLabel.setStyle("-fx-font-weight: bold;");

        // Carregar todos os checklists salvos
        ChecklistData.carregar();
        checklistComboBox.getItems().clear();
        checklistComboBox.getItems().addAll(ChecklistData.getChecklists());

        // Seleciona o primeiro checklist como padrão, se houver
        if (!checklistComboBox.getItems().isEmpty()) {
            checklistComboBox.getSelectionModel().selectFirst();
            carregarChecklistSelecionado();
        }

        // Quando o usuário escolhe outro checklist
        checklistComboBox.setOnAction(event -> carregarChecklistSelecionado());

        // Layout principal
        root.getChildren().addAll(
                new HBox(10, auditorField, responsavelField),
                checklistLabel,
                checklistComboBox,
                checklistBox,
                registrarButton,
                aderenciaLabel
        );

        return root;
    }

    private void carregarChecklistSelecionado() {
        Checklist selecionado = checklistComboBox.getSelectionModel().getSelectedItem();
        checklistBox.getChildren().clear();
        gruposRespostas.clear();

        if (selecionado != null) {
            for (String pergunta : selecionado.getPerguntas()) {
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
        } else {
            checklistBox.getChildren().add(new Label("Nenhum checklist disponível."));
        }
    }
}
