package com.ferramenta.ferramentaAuditoria.controller;

import com.ferramenta.ferramentaAuditoria.model.AuditoriaData;
import com.ferramenta.ferramentaAuditoria.model.NaoConformidade;
import com.ferramenta.ferramentaAuditoria.model.Checklist;
import com.ferramenta.ferramentaAuditoria.view.AuditoriaView;
import com.ferramenta.ferramentaAuditoria.view.AcompanhamentoView;
import com.ferramenta.ferramentaAuditoria.util.AlertUtils;
import com.ferramenta.ferramentaAuditoria.observer.AcompanhamentoObserver;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaController {

    private final AuditoriaData model;
    private final AuditoriaView auditoriaView;
    private final AcompanhamentoView acompanhamentoView;

    private final AcompanhamentoObserver acompanhamentoObserver;

    public AuditoriaController(AuditoriaData model, AuditoriaView auditoriaView, AcompanhamentoView acompanhamentoView) {
        this.model = model;
        this.auditoriaView = auditoriaView;
        this.acompanhamentoView = acompanhamentoView;

        // Criar o observer para atualização da tabela
        this.acompanhamentoObserver = new AcompanhamentoObserver(model.getListaNaoConformidades());

        // Registrar observer nas NCs já existentes
        for (NaoConformidade nc : model.getListaNaoConformidades()) {
            nc.adicionarObserver(acompanhamentoObserver);
        }

        // Conecta o botão de registrar auditoria ao processamento
        auditoriaView.registrarButton.setOnAction(e -> processarAuditoria());
    }

    // ===============================
    // PROCESSAMENTO DA AUDITORIA
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
        List<String> perguntas = checklistSelecionado.getPerguntas();

        int totalValidos = 0;
        int conformidades = 0;
        List<String> naoConformidades = new ArrayList<>();

        for (int i = 0; i < grupos.size(); i++) {
            String resposta = grupos.get(i).getSelectedToggle().getUserData().toString();
            if (!resposta.equals("Não se Aplica")) {
                totalValidos++;
                if (resposta.equals("Não")) {
                    String perguntaNC = perguntas.get(i);
                    naoConformidades.add(perguntaNC);

                    // Criar nova NC e registrar observer
                    NaoConformidade novaNC = new NaoConformidade(auditor, responsavel, perguntaNC, "Pendente");
                    novaNC.adicionarObserver(acompanhamentoObserver);

                    try {
                        model.salvarNCemCSV(novaNC);
                    } catch (IOException ex) {
                        AlertUtils.getInstance().mostrarErro("Erro", "Falha ao salvar NC: " + ex.getMessage());
                    }
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
        } catch (IOException ex) {
            AlertUtils.getInstance().mostrarErro("Erro", "Falha ao salvar dados: " + ex.getMessage());
        }

        AlertUtils.getInstance().mostrarInfo("Sucesso!", "Auditoria registrada com sucesso.");
    }

    // ===============================
    // AÇÕES SOBRE NCs
    // ===============================
    public void escalonarNC(NaoConformidade nc) {
        nc.setSituacao("Escalonada");
        try { model.salvarTodasNCs(); } catch (IOException e) { AlertUtils.getInstance().mostrarInfo("Erro", "Falha ao atualizar NCs."); }
    }

    public void resolverNC(NaoConformidade nc) {
        nc.setSituacao("Resolvida");
        try { model.salvarTodasNCs(); } catch (IOException e) { AlertUtils.getInstance().mostrarInfo("Erro", "Falha ao atualizar NCs."); }
    }

    public void notificarNC(NaoConformidade nc) { exibirPopUpEmail(nc); }

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
