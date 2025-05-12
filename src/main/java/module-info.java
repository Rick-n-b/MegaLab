module ru.nstu.lab02v2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports ru.nstu.lab02v2.Add;
    exports ru.nstu.lab02v2.AI;
    exports ru.nstu.lab02v2.Entities;

    opens ru.nstu.lab02v2.Add to com.fasterxml.jackson.databind;


    exports ru.nstu.lab02v2.module;
    opens ru.nstu.lab02v2.module to javafx.fxml;

    opens ru.nstu.lab02v2 to javafx.fxml;
    exports ru.nstu.lab02v2;
    exports ru.nstu.lab02v2.moduleList;
    opens ru.nstu.lab02v2.moduleList to javafx.fxml;
}