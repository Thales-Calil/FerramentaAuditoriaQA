package com.example.demo.controller;

import com.example.demo.model.AuditoriaData;
import com.example.demo.model.NaoConformidade;
import com.example.demo.view.AuditoriaView;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class AuditoriaController {

    private final AuditoriaData model;
    private final AuditoriaView auditoriaView;

    public AuditoriaController(AuditoriaData model, AuditoriaView auditoriaView) {
        this.model = model;
        this.auditoriaView = auditoriaView;

        // Conectar o botão de registrar da View ao Controller
        auditoriaView.registrarButton.setOnAction(e -> {
            processarAuditoria();
        });
    }

    public void processarAuditoria() {
        String auditor = auditoriaView.auditorField.getText();
        String responsavel = auditoriaView.responsavelField.getText();
        List<String> perguntas = Arrays.asList(
                "1. O bibliotecário verificou se o estudante está inelegível?",
                "2. O bibliotecário solicitou a carteirinha?",
                "3. O bibliotecário informou sobre o prazo de devolução?"
        );
        List<ToggleGroup> grupos = auditoriaView.gruposRespostas;

        if (auditor.trim().isEmpty() || responsavel.trim().isEmpty()) {
            showAlert("Campos obrigatórios", "Por favor, preencha o nome do Auditor e do Responsável.");
            return;
        }

        int totalValidos = 0;
        int conformidades = 0;
        List<String> naoConformidades = new ArrayList<>();

        for (int i = 0; i < grupos.size(); i++) {
            String resposta = grupos.get(i).getSelectedToggle().getUserData().toString();
            if (!resposta.equals("Não se Aplica")) {
                totalValidos++;
                if (resposta.equals("Não")) {
                    naoConformidades.add(perguntas.get(i));
                } else {
                    conformidades++;
                }
            }
        }

        double porcentagemAderencia = (totalValidos > 0) ? (double) conformidades / totalValidos * 100 : 0;
        auditoriaView.aderenciaLabel.setText(String.format("Aderência: %.2f%%", porcentagemAderencia));

        try {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            model.salvarEmCSV(dataHora, auditor, responsavel, "Respostas aqui...", naoConformidades.size());

            for (String nc : naoConformidades) {
                NaoConformidade novaNC = new NaoConformidade(auditor, responsavel, nc, "Pendente");
                model.salvarNCemCSV(novaNC);
            }
            showAlert("Sucesso!", "Auditoria registrada com sucesso.");
        } catch (IOException ex) {
            showAlert("Erro", "Falha ao salvar dados: " + ex.getMessage());
        }
    }

    public void escalonarNC(NaoConformidade nc) {
        nc.setSituacao("Escalonada");
        try {
            model.salvarTodasNCs();
        } catch (IOException e) {
            showAlert("Erro", "Falha ao atualizar o arquivo de NCs.");
        }
    }

    public void resolverNC(NaoConformidade nc) {
        nc.setSituacao("Resolvida");
        try {
            model.salvarTodasNCs();
        } catch (IOException e) {
            showAlert("Erro", "Falha ao atualizar o arquivo de NCs.");
        }
    }

    public void notificarNC(NaoConformidade nc) {
        exibirPopUpEmail(nc);
    }

    private void exibirPopUpEmail(NaoConformidade nc) {
        Stage popUpStage = new Stage();
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.setTitle("Notificar Responsável por E-mail");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label titulo = new Label("Detalhes do E-mail");
        titulo.setStyle("-fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("E-mail do Responsável");

        TextArea corpoEmail = new TextArea();
        corpoEmail.setWrapText(true);
        corpoEmail.setEditable(false);
        corpoEmail.setPrefRowCount(10);

        String assunto = "Não-Conformidade Registrada: " + nc.getNc();
        String corpo = "Prezado(a) " + nc.getResponsavel() + ",\n\n"
                + "Uma não-conformidade foi registrada durante uma auditoria realizada por " + nc.getAuditor() + ".\n\n"
                + "Detalhe da NC: " + nc.getNc() + "\n"
                + "Situação: " + nc.getSituacao() + "\n\n"
                + "Por favor, tome as devidas ações para a resolução.\n\n"
                + "Atenciosamente,\nEquipe de Auditoria";

        corpoEmail.setText(corpo);

        Button enviarButton = new Button("Enviar E-mail");
        enviarButton.setOnAction(e -> {
            if (emailField.getText().trim().isEmpty()) {
                showAlert("Atenção", "Por favor, insira o e-mail do responsável.");
                return;
            }
            abrirClienteEmail(emailField.getText(), assunto, corpo);
            popUpStage.close();
        });

        root.getChildren().addAll(titulo, new Label("E-mail:"), emailField, new Label("Corpo da Mensagem:"), corpoEmail, enviarButton);

        Scene scene = new Scene(root, 400, 450);
        popUpStage.setScene(scene);
        popUpStage.showAndWait();
    }

    private void abrirClienteEmail(String para, String assunto, String corpo) {
        try {
            String mailtoLink = String.format("mailto:%s?subject=%s&body=%s",
                    URLEncoder.encode(para, StandardCharsets.UTF_8),
                    URLEncoder.encode(assunto, StandardCharsets.UTF_8),
                    URLEncoder.encode(corpo, StandardCharsets.UTF_8));

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                Desktop.getDesktop().mail(new URI(mailtoLink));
            } else {
                showAlert("Atenção", "Nenhum cliente de e-mail padrão foi encontrado.");
            }
        } catch (Exception e) {
            showAlert("Erro", "Não foi possível abrir o cliente de e-mail.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}