package com.example.guiscenebuilder;

import javafx.stage.Stage;

public interface CRUD {
    void create(Stage stage,Object obj, Runnable goBack);
    void read(Stage stage,Object obj, Runnable goBack);
    void update(Stage stage,Object obj, Runnable goBack);
    void delete(Stage stage,Object obj, Runnable goBack);
}
