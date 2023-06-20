module com.example.gestion_de_taches {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens com.example.gestion_de_taches to javafx.fxml;
    exports com.example.gestion_de_taches;
}