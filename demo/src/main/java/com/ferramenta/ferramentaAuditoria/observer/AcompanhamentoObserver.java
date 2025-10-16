package com.ferramenta.ferramentaAuditoria.observer;

import com.ferramenta.ferramentaAuditoria.model.NaoConformidade;
import javafx.collections.ObservableList;

public class AcompanhamentoObserver implements Observer {

    private ObservableList<NaoConformidade> listaNCs;

    public AcompanhamentoObserver(ObservableList<NaoConformidade> listaNCs) {
        this.listaNCs = listaNCs;
    }

    @Override
    public void atualizar(String evento, Object dados) {
        if ("NC_ATUALIZADA".equals(evento) && dados instanceof NaoConformidade nc) {
            // Força atualização da tabela
            int index = listaNCs.indexOf(nc);
            if (index >= 0) {
                listaNCs.set(index, nc);
            }
        }
    }
}
