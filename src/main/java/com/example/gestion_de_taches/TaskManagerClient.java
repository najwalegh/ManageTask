package com.example.gestion_de_taches;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskManagerClient extends Application {
    private final String username;
    private ListView<String> taskListView;
    private Map<String, String> taskDescriptions = new HashMap<String, String>();

    public TaskManagerClient(String username) {
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Bonjour "+username);

        // Création des composants de l'interface utilisateur
        Button addButton = new Button("Ajouter");
        Button deleteButton = new Button("Supprimer");
        Button updateButton = new Button("Modifier");

        taskListView = new ListView<>();
        taskListView.setPrefWidth(300);
        taskListView.setPrefHeight(200);

        // Création de la zone de description
        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setEditable(false);

        // Mise en place d'un écouteur pour mettre à jour la zone de description lorsque la tâche sélectionnée change
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String desc = taskDescriptions.get(newValue);
                descriptionTextArea.setText(desc);
            } else {
                descriptionTextArea.setText("");
            }
        });

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(taskListView,descriptionTextArea, addButton, deleteButton,updateButton);

        Registry registry = LocateRegistry.getRegistry("localhost");
        TaskManager taskManager = (TaskManager) registry.lookup("TaskManager");

        // Appel de la méthode getTasks pour récupérer la liste des tâches
        List<task> tasks = taskManager.getTasks();

        // Utilisation de la liste des tâches
        for (task task : tasks) {
            taskListView.getItems().add(task.getTitle());
        }

        // Configuration des actions des boutons
        addButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Ajouter une tâche");
            dialog.setHeaderText(null);
            dialog.setContentText("Entrez le nom de la tâche :");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String taskName = result.get();
                // Ajouter la tâche à la liste et sa description
                taskListView.getItems().add(taskName+" ("+username+")");
                task task = new task(taskName,"-pas de description-",username);
                descriptionTextArea.setText(task.getDescription());
                try {
                    taskManager.addTask(task);
                    taskDescriptions.put(task.getTitle(), task.getDescription());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        deleteButton.setOnAction(e -> {
            String selected = taskListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                taskListView.getItems().remove(selected);
                try {
                    taskManager.deleteTask(selected);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        updateButton.setOnAction(e->{
            String selected = taskListView.getSelectionModel().getSelectedItem();
            String description = descriptionTextArea.getText();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog(description);
                dialog.setTitle("Modifier la tâche");
                dialog.setHeaderText(null);
                dialog.setContentText("Entrez la nouvelle description :");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String newDescription = result.get();

                    try {
                        taskManager.updateTask(selected, newDescription);
                        taskDescriptions.put(selected, newDescription);
                        descriptionTextArea.setText(newDescription);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Configuration de la fenêtre principale
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}