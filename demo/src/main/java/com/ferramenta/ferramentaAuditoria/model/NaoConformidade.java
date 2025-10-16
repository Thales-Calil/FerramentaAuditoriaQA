package com.ferramenta.ferramentaAuditoria.model;

import com.ferramenta.ferramentaAuditoria.observer.Observer;
import com.ferramenta.ferramentaAuditoria.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class NaoConformidade implements Subject {

    private String auditor;
    private String responsavel;
    private String nc;
    private String situacao;

    private List<Observer> observers = new ArrayList<>();

    public NaoConformidade(String auditor, String responsavel, String nc, String situacao) {
        this.auditor = auditor;
        this.responsavel = responsavel;
        this.nc = nc;
        this.situacao = situacao;
    }

    public String getAuditor() {
        return auditor;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public String getNc() {
        return nc;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setNc(String nc) {
        this.nc = nc;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
        // Notifica todos os observers quando a situação muda
        notificarObservers("NC_ATUALIZADA", this);
    }

    // =====================
    // Implementação do Subject (Observer Pattern)
    // =====================
    @Override
    public void adicionarObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removerObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notificarObservers(String evento, Object dados) {
        for (Observer observer : observers) {
            observer.atualizar(evento, dados);
        }
    }
}
