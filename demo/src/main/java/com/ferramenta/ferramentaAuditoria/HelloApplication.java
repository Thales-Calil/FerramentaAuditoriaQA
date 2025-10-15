package com.ferramenta.ferramentaAuditoria;

import com.ferramenta.ferramentaAuditoria.model.AuditoriaData;
import com.ferramenta.ferramentaAuditoria.controller.AuditoriaController;
import com.ferramenta.ferramentaAuditoria.model.NaoConformidade;
import com.ferramenta.ferramentaAuditoria.view.AuditoriaView;
import com.ferramenta.ferramentaAuditoria.view.AcompanhamentoView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Sistema de Auditoria de Qualidade");

        // 1. Instanciar o Model e as Views
        AuditoriaData model = new AuditoriaData();
        AuditoriaView auditoriaView = new AuditoriaView();
        AcompanhamentoView acompanhamentoView = new AcompanhamentoView(model.getListaNaoConformidades());

        // 2. Instanciar o Controller e passar as referências do Model e da View
        AuditoriaController controller = new AuditoriaController(model, auditoriaView);

        // 3. Criar a coluna de ações para a tabela de acompanhamento
        TableColumn<NaoConformidade, Void> colunaAcoes = new TableColumn<>("Ações");

        colunaAcoes.setCellFactory(new Callback<TableColumn<NaoConformidade, Void>, TableCell<NaoConformidade, Void>>() {
            @Override
            public TableCell<NaoConformidade, Void> call(TableColumn<NaoConformidade, Void> param) {
                return new TableCell<>() {
                    private final Button btnEscalonar = new Button("Escalonar");
                    private final Button btnResolver = new Button("Resolver");
                    private final Button btnNotificar = new Button("Notificar por E-mail");
                    private final HBox pane = new HBox(5, btnEscalonar, btnResolver, btnNotificar);

                    {
                        pane.setSpacing(10);
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            NaoConformidade nc = getTableView().getItems().get(getIndex());

                            btnEscalonar.setOnAction(event -> {
                                controller.escalonarNC(nc);
                                getTableView().refresh();
                            });

                            btnResolver.setOnAction(event -> {
                                controller.resolverNC(nc);
                                getTableView().refresh();
                            });

                            btnNotificar.setOnAction(event -> controller.notificarNC(nc));

                            setGraphic(pane);
                        }
                    }
                };
            }
        });

        // Adicionar a coluna à tabela
        acompanhamentoView.tabelaNCs.getColumns().add(colunaAcoes);

        // 4. Montar as abas da interface gráfica
        TabPane tabPane = new TabPane();
        Tab tabAuditoria = new Tab("Registrar Auditoria", auditoriaView.criarTabAuditoria());
        Tab tabAcompanhamento = new Tab("Acompanhamento de NCs", acompanhamentoView.getView());
        tabAuditoria.setClosable(false);
        tabAcompanhamento.setClosable(false);

        tabPane.getTabs().addAll(tabAuditoria, tabAcompanhamento);

        Scene scene = new Scene(tabPane, 650, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
