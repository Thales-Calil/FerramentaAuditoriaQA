package com.ferramenta.ferramentaAuditoria.observer;

public interface Subject {
    void adicionarObserver(Observer observer);
    void removerObserver(Observer observer);
    void notificarObservers(String evento, Object dados);
}
