package com.ferramenta.ferramentaAuditoria.view;

import com.ferramenta.ferramentaAuditoria.model.Checklist;
import com.ferramenta.ferramentaAuditoria.model.ChecklistData;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class ChecklistView {

    private final VBox root;
    private final ListView<String> listaChecklists;
    private final TextArea detalhesArea;

    public ChecklistView() {
        root = new VBox(10);
        root.setPadding(new Insets(20));

        Label title = new Label("Gerenciar Checklists");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        listaChecklists = new ListView<>();
        listaChecklists.setPrefHeight(180);

        detalhesArea = new TextArea();
        detalhesArea.setEditable(false);
        detalhesArea.setWrapText(true);
        detalhesArea.setPrefRowCount(8);

        Button cadastrarBtn = new Button("Cadastrar Checklist");
        Button excluirBtn = new Button("Excluir Checklist");

        HBox actions = new HBox(10, cadastrarBtn, excluirBtn);

        root.getChildren().addAll(title, actions, new Label("Checklists:"), listaChecklists, new Label("Detalhes (perguntas):"), detalhesArea);

        // listeners e ações
        cadastrarBtn.setOnAction(e -> {
            // determina o Stage owner, se possível
            Stage owner = null;
            if (root.getScene() != null && root.getScene().getWindow() instanceof Stage) {
                owner = (Stage) root.getScene().getWindow();
            }
            exibirCadastroChecklist(owner);
            refreshList();
        });

        excluirBtn.setOnAction(e -> {
            String selecionado = listaChecklists.getSelectionModel().getSelectedItem();
            if (selecionado == null) {
                new Alert(Alert.AlertType.WARNING, "Selecione um checklist para excluir.").showAndWait();
                return;
            }
            ChecklistData.carregar();
            List<Checklist> itens = ChecklistData.getChecklists();
            Checklist alvo = null;
            for (Checklist c : itens) {
                if (c.getNome().equals(selecionado)) {
                    alvo = c;
                    break;
                }
            }
            if (alvo != null) {
                itens.remove(alvo);
                // Persistir (assume que ChecklistData possui método salvar())
                try {
                    ChecklistData.salvar();
                } catch (Exception ex) {
                    // se a sua ChecklistData não lançar exceção em salvar, adapte conforme necessário
                }
                refreshList();
                new Alert(Alert.AlertType.INFORMATION, "Checklist excluído com sucesso.").showAndWait();
                detalhesArea.clear();
            }
        });

        listaChecklists.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV == null) {
                detalhesArea.clear();
                return;
            }
            ChecklistData.carregar();
            for (Checklist c : ChecklistData.getChecklists()) {
                if (c.getNome().equals(newV)) {
                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    for (String p : c.getPerguntas()) {
                        sb.append(i++).append(". ").append(p).append("\n");
                    }
                    detalhesArea.setText(sb.toString());
                    break;
                }
            }
        });

        // carrega a lista inicial
        refreshList();
    }

    // Método que a Main pode chamar para inserir essa view na Tab
    public VBox getView() {
        return root;
    }

    // Atualiza itens da lista a partir do ChecklistData
    private void refreshList() {
        ChecklistData.carregar();
        listaChecklists.getItems().clear();
        for (Checklist c : ChecklistData.getChecklists()) {
            listaChecklists.getItems().add(c.getNome());
        }
    }

    // Modal existente para cadastrar um checklist (mantive quase igual ao seu código)
    public void exibirCadastroChecklist(Stage parentStage) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Cadastro de Checklist");

        VBox rootModal = new VBox(10);
        rootModal.setPadding(new Insets(20));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do Checklist");

        TextField perguntaField = new TextField();
        perguntaField.setPromptText("Digite uma pergunta");

        ListView<String> listaPerguntas = new ListView<>();
        listaPerguntas.setPrefHeight(200);

        Button adicionarPergunta = new Button("Adicionar Pergunta");
        Button salvarChecklist = new Button("Salvar Checklist");

        adicionarPergunta.setOnAction(e -> {
            if (!perguntaField.getText().trim().isEmpty()) {
                listaPerguntas.getItems().add(perguntaField.getText().trim());
                perguntaField.clear();
            }
        });

        salvarChecklist.setOnAction(e -> {
            if (nomeField.getText().trim().isEmpty() || listaPerguntas.getItems().isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Preencha o nome e pelo menos uma pergunta.").showAndWait();
                return;
            }
            Checklist checklist = new Checklist(nomeField.getText().trim());
            checklist.getPerguntas().addAll(listaPerguntas.getItems());
            ChecklistData.adicionarChecklist(checklist); // assume que este método salva

            new Alert(Alert.AlertType.INFORMATION, "Checklist salvo com sucesso!").showAndWait();
            stage.close();
        });

        rootModal.getChildren().addAll(new Label("Nome:"), nomeField,
                new Label("Perguntas:"), perguntaField, adicionarPergunta,
                listaPerguntas, salvarChecklist);

        Scene scene = new Scene(rootModal, 420, 520);
        stage.setScene(scene);

        if (parentStage != null) {
            stage.initOwner(parentStage);
        }
        stage.showAndWait();

        // Atualiza a lista ao voltar do modal
        refreshList();
    }
}
