module ru.nstu.labs {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    exports ru.nstu.labs.Add;
    exports ru.nstu.labs.AI;
    exports ru.nstu.labs.Entities;

    opens ru.nstu.labs.Add to com.fasterxml.jackson.databind;

    requires ru.nstu.shared;

    exports ru.nstu.labs.modules.module;
    opens ru.nstu.labs.modules.module to javafx.fxml;
    exports ru.nstu.labs.modules;
    opens ru.nstu.labs.modules to javafx.fxml;

    opens ru.nstu.labs to javafx.fxml;
    exports ru.nstu.labs;
    exports ru.nstu.labs.modules.moduleList;
    opens ru.nstu.labs.modules.moduleList to javafx.fxml;
}