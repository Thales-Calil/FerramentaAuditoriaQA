package com.ferramenta.ferramentaAuditoria.util;

import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.List;

public class AlertUtils {

    // ===== Singleton =====
    private static AlertUtils instance;

    private AlertUtils() {
        // inicializa o log
        alertLog = new ArrayList<>();
    }

    public static AlertUtils getInstance() {
        if (instance == null) {
            instance = new AlertUtils();
        }
        return instance;
    }

    // ===== Log de alertas =====
    private final List<String> alertLog;

    public List<String> getAlertLog() {
        return new ArrayList<>(alertLog); // retorna cópia para não modificar direto
    }

    public void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();

        String logEntry = "[ERRO] " + titulo + " - " + mensagem;
        alertLog.add(logEntry);

        // Mostrar no terminal
        System.out.println(logEntry);
    }

    public void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informação");
        alert.setHeaderText(titulo);
        alert.setContentText(mensagem);
        alert.showAndWait();

        String logEntry = "[INFO] " + titulo + " - " + mensagem;
        alertLog.add(logEntry);

        // Mostrar no terminal
        System.out.println(logEntry);
    }

}
