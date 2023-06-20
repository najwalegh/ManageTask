package com.example.gestion_de_taches;

import javafx.concurrent.Task;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class TaskManagerServer implements TaskManager {
    private List<task> tasks;

    public TaskManagerServer() {
        tasks = new ArrayList<>();
    }

    @Override
    public void addTask(task task) throws RemoteException {
        tasks.add(task);
        System.out.println("Nouvelle tâche ajoutée : " + task.getTitle() +" effectué par :"+task.getUser());
    }

    @Override
    public void updateTask(String title, String newDescription) throws RemoteException {
        for (task task : tasks) {
            if (task.getTitle().equals(title)) {
                task.setDescription(newDescription);
                System.out.println("Task updated: " + task.getTitle()+" "+task.getDescription());
                break;
            }
        }
    }

    @Override
    public void deleteTask(String title) throws RemoteException {
        for (task task : tasks) {
            if (task.getTitle().equals(title)) {
                tasks.remove(task);
                System.out.println("Tache supprimée: "+title);
                break;
            }
        }
    }

    @Override
    public List<task> getTasks() throws RemoteException {
        return tasks;
    }

    public static void main(String[] args) {
        try {
            TaskManagerServer server = new TaskManagerServer();
            TaskManager stub = (TaskManager) UnicastRemoteObject.exportObject(server, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("TaskManager", stub);

            System.out.println("Serveur RMI prêt !");
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur : " + e.toString());
        }
    }
}
