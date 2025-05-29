/*
 * Launcher.java
 *
 * Authors: Zhdanovich Iaroslav (xzhdan00)
 *          Malytskyi Denys     (xmalytd00)
 *
 * Description: Entry point class for the "lightbulb" project application that
 * launches the JavaFX application by delegating to the Main class.
 */


package ija2025;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
