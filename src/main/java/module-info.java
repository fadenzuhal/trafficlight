module com.example.demotrafik {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.example.demotrafik to javafx.fxml;
    exports com.example.demotrafik;
    exports com.example.demotrafik.Model;
    exports com.example.demotrafik.view;
    opens com.example.demotrafik.Controller;
}