module com.game.simonsays {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.game.simonsays to javafx.fxml;
    exports com.game.simonsays;
}