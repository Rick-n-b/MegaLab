module ru.nstu.lab02v2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.nstu.lab02v2 to javafx.fxml;
    exports ru.nstu.lab02v2;
}