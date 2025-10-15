package com.ferramenta.ferramentaAuditoria.facade;

import com.ferramenta.ferramentaAuditoria.model.*;
import com.ferramenta.ferramentaAuditoria.util.AlertUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaFacade {

    private AuditoriaData model;

    public AuditoriaFacade(AuditoriaData model) {
        this.model = model;
    }

    public double registrarAuditoria(String auditor, String responsavel, Checklist checklist, List<String> respostas) {
        List<String> perguntas = checklist.getPerguntas();
        List<String> naoConformidades = new ArrayList<>();

        int totalValidos = 0;
        int conformidades = 0;

        for (int i = 0; i < respostas.size(); i++) {
            String resposta = respostas.get(i);

            if (!resposta.equals("Não se Aplica")) {
                totalValidos++;
                if (resposta.equals("Não")) {
                    String perguntaNC = perguntas.get(i);
                    naoConformidades.add(perguntaNC);

                    NaoConformidade novaNC = new NaoConformidade(auditor, responsavel, perguntaNC, "Pendente");
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

        double porcentagemAderencia = (totalValidos > 0)
                ? (double) conformidades / totalValidos * 100
                : 0;

        try {
            String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            model.salvarEmCSV(dataHora, auditor, responsavel, "Respostas aqui...", naoConformidades.size());
        } catch (IOException ex) {
            AlertUtils.getInstance().mostrarErro("Erro", "Falha ao salvar auditoria: " + ex.getMessage());
        }

        return porcentagemAderencia;
    }
}
