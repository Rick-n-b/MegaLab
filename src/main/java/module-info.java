module ru.nstu.lab02v2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    exports ru.nstu.lab02v2.module;
    opens ru.nstu.lab02v2.module to javafx.fxml;

    opens ru.nstu.lab02v2 to javafx.fxml;
    exports ru.nstu.lab02v2;
    exports ru.nstu.lab02v2.moduleList;
    opens ru.nstu.lab02v2.moduleList to javafx.fxml;
}