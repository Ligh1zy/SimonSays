module com.game.simonsays {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.game.simonsays to javafx.fxml;
    exports com.game.simonsays;
}