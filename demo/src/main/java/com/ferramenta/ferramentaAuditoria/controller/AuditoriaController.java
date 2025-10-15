package com.ferramenta.ferramentaAuditoria.controller;

import com.ferramenta.ferramentaAuditoria.model.AuditoriaData;
import com.ferramenta.ferramentaAuditoria.model.NaoConformidade;
import com.ferramenta.ferramentaAuditoria.model.Checklist;
import com.ferramenta.ferramentaAuditoria.view.AuditoriaView;
import com.ferramenta.ferramentaAuditoria.util.AlertUtils;
import com.ferramenta.ferramentaAuditoria.facade.AuditoriaFacade;

import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaController {

    private final AuditoriaData model;
    private final AuditoriaView auditoriaView;

    public AuditoriaController(AuditoriaData model, AuditoriaView auditoriaView) {
        this.model = model;
        this.auditoriaView = auditoriaView;

        // Conecta o botão de registrar auditoria ao processamento
        auditoriaView.registrarButton.setOnAction(e -> processarAuditoria());
    }

    // ===============================
    // Processamento da auditoria usando facade
    // ===============================
    public void processarAuditoria() {
        String auditor = auditoriaView.auditorField.getText().trim();
        String responsavel = auditoriaView.responsavelField.getText().trim();
        List<ToggleGroup> grupos = auditoriaView.gruposRespostas;

        if (auditor.isEmpty() || responsavel.isEmpty()) {
            AlertUtils.getInstance().mostrarErro("Campos obrigatórios", "Por favor, preencha o nome do Auditor e do Responsável.");
            return;
        }

        Checklist checklistSelecionado = auditoriaView.checklistComboBox.getSelectionModel().getSelectedItem();
        if (checklistSelecionado == null) {
            AlertUtils.getInstance().mostrarErro("Erro", "Selecione um checklist válido.");
            return;
        }

        // Coleta as respostas marcadas
        List<String> respostas = new ArrayList<>();
        for (ToggleGroup grupo : grupos) {
            if (grupo.getSelectedToggle() != null) {
                respostas.add(grupo.getSelectedToggle().getUserData().toString());
            } else {
                respostas.add("Não se Aplica"); // Evita erro se o usuário não marcar algo
            }
        }

        // Usa o padrão Facade para processar toda a lógica
        AuditoriaFacade facade = new AuditoriaFacade(model);
        double aderencia = facade.registrarAuditoria(auditor, responsavel, checklistSelecionado, respostas);

        // Atualiza a tela
        auditoriaView.aderenciaLabel.setText(String.format("Aderência: %.2f%%", aderencia));
        AlertUtils.getInstance().mostrarInfo("Sucesso!", "Auditoria registrada com sucesso.");
    }

    // ===============================
    // AÇÕES SOBRE NCs
    // ===============================
    public void escalonarNC(NaoConformidade nc) {
        nc.setSituacao("Escalonada");
        try {
            model.salvarTodasNCs();
        } catch (IOException e) {
            AlertUtils.getInstance().mostrarInfo("Erro", "Falha ao atualizar NCs.");
        }
    }

    public void resolverNC(NaoConformidade nc) {
        nc.setSituacao("Resolvida");
        try {
            model.salvarTodasNCs();
        } catch (IOException e) {
            AlertUtils.getInstance().mostrarInfo("Erro", "Falha ao atualizar NCs.");
        }
    }

    public void notificarNC(NaoConformidade nc) {
        exibirPopUpEmail(nc);
    }

    // ===============================
    // POP-UP DE EMAIL
    // ===============================
    private void exibirPopUpEmail(NaoConformidade nc) {
        Stage popUpStage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(20));

        Label titulo = new Label("Detalhes do E-mail");
        titulo.setStyle("-fx-font-weight: bold;");
        Label emailLabel = new Label("E-mail:");
        TextField emailField = new TextField();
        Label corpoLabel = new Label("Corpo da Mensagem:");
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
                AlertUtils.getInstance().mostrarInfo("Atenção", "Por favor, insira o e-mail do responsável.");
                return;
            }
            abrirClienteEmail(emailField.getText(), assunto, corpo);
            popUpStage.close();
        });

        root.getChildren().addAll(titulo, emailLabel, emailField, corpoLabel, corpoEmail, enviarButton);

        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.setTitle("Notificar Responsável por E-mail");
        popUpStage.setScene(new Scene(root, 400, 450));
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
                AlertUtils.getInstance().mostrarErro("Atenção", "Nenhum cliente de e-mail padrão foi encontrado.");
            }
        } catch (Exception e) {
            AlertUtils.getInstance().mostrarErro("Erro", "Não foi possível abrir o cliente de e-mail.");
        }
    }
}
