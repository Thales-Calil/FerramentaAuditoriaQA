// src/main/java/com/example/demo/model/AuditoriaData.java
package com.example.demo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditoriaData {

    private final String ARQUIVO_DADOS = "auditoria_registros.csv";
    private final String ARQUIVO_NCS = "nao_conformidades.csv";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ObservableList<NaoConformidade> listaNaoConformidades = FXCollections.observableArrayList();

    public AuditoriaData() {
        carregarNCs();
    }

    public ObservableList<NaoConformidade> getListaNaoConformidades() {
        return listaNaoConformidades;
    }

    public void salvarEmCSV(String dataHora, String auditor, String responsavel, String respostas, int totalNCs) throws IOException {
        try (FileWriter fw = new FileWriter(ARQUIVO_DADOS, true)) {
            fw.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d\n", dataHora, auditor, responsavel, respostas, totalNCs));
        }
    }

    public void salvarNCemCSV(NaoConformidade nc) throws IOException {
        try (FileWriter fw = new FileWriter(ARQUIVO_NCS, true)) {
            fw.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    LocalDateTime.now().format(formatter), nc.getAuditor(), nc.getResponsavel(), nc.getNc(), nc.getSituacao()));
        }
        listaNaoConformidades.add(nc);
    }

    public void salvarTodasNCs() throws IOException {
        try (FileWriter fw = new FileWriter(ARQUIVO_NCS, false)) {
            fw.append("\"Data/Hora\",\"Auditor\",\"Responsável\",\"NC\",\"Situação da NC\"\n");
            for (NaoConformidade nc : listaNaoConformidades) {
                fw.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        LocalDateTime.now().format(formatter), nc.getAuditor(), nc.getResponsavel(), nc.getNc(), nc.getSituacao()));
            }
        }
    }

    private void carregarNCs() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_NCS))) {
            String linha;
            br.readLine();
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (dados.length >= 5) {
                    NaoConformidade nc = new NaoConformidade(
                            dados[1].replace("\"", ""),
                            dados[2].replace("\"", ""),
                            dados[3].replace("\"", ""),
                            dados[4].replace("\"", "")
                    );
                    listaNaoConformidades.add(nc);
                }
            }
        } catch (IOException e) {
            System.err.println("Arquivo de NCs não encontrado. Um novo será criado.");
        }
    }
}