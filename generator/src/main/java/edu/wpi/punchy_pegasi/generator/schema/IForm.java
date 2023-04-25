package edu.wpi.punchy_pegasi.generator.schema;

import javafx.scene.Node;

import java.util.List;

public interface IForm<T> {
    void populateForm(T entry);

    T commit();

    List<Node> getForm();
}

