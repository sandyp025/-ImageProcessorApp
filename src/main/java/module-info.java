module com.converter.imageprocessorapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires static lombok;

    opens com.my.app to javafx.fxml;
    exports com.my.app;
    exports com.my.app.image;
    exports com.my.app.filters;
    exports com.my.app.filters.impl;
    exports com.my.app.batch;
    exports com.my.app.ui;
    exports com.my.app.io;
    exports com.my.app.processor;
}