module ru.nstu.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.compiler;


    opens ru.nstu.server to javafx.fxml;
    exports ru.nstu.server;
}