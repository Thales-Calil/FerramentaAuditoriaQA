package com.ferramenta.ferramentaAuditoria.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Checklist implements Serializable {
    private String nome;
    private final List<String> perguntas;

    public Checklist(String nome) {
        this.nome = nome;
        this.perguntas = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getPerguntas() {
        return perguntas;
    }

    public void adicionarPergunta(String pergunta) {
        perguntas.add(pergunta);
    }

    @Override
    public String toString() {
        return this.getNome();
    }
}
