package com.example.gestion_de_taches;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TaskManager extends Remote {
    void addTask(task task) throws RemoteException;
    void updateTask(String task, String newDescription) throws RemoteException;
    void deleteTask(String title) throws RemoteException;
    List<task> getTasks() throws RemoteException;
}