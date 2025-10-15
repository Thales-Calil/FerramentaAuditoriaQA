module com.ferramenta.ferramentaAuditoria {
    requires javafx.controls;
    requires java.desktop;

    opens com.ferramenta.ferramentaAuditoria to javafx.graphics;
    opens com.ferramenta.ferramentaAuditoria.model to javafx.base;

    exports com.ferramenta.ferramentaAuditoria;
    exports com.ferramenta.ferramentaAuditoria.controller;
    exports com.ferramenta.ferramentaAuditoria.model;
    exports com.ferramenta.ferramentaAuditoria.view;
}