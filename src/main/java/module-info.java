module ru.nstu.lab02v2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    exports ru.nstu.lab02v2.module;
    opens ru.nstu.lab02v2.module to javafx.fxml;

    opens ru.nstu.lab02v2 to javafx.fxml;
    exports ru.nstu.lab02v2;
}