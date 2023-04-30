package edu.wpi.punchy_pegasi.frontend.components;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.util.Objects;
import java.util.function.Function;

public class PFXListView<T> extends VBox {
    private final Function<T, String> keyMapper;
    private final Function<T, Node> mapper;

    public PFXListView(ObservableList<T> list, Function<T, Node> mapper, Function<T, String> keyMapper) {
        super();
        this.keyMapper = keyMapper;
        this.mapper = mapper;
        list.addListener((ListChangeListener<? super T>) c -> {
            while (c.next()) {
                if (c.wasRemoved())
                    for (T item : c.getRemoved())
                        removeNode(item);
                if (c.wasAdded())
                    for (T item : c.getAddedSubList())
                        createNode(item);
            }
        });
        for (T item : list)
            createNode(item);
    }

    private void createNode(T item) {
        var node = mapper.apply(item);
        node.setId(keyMapper.apply(item));
        Platform.runLater(() -> getChildren().add(node));
    }

    private void removeNode(T item) {
        Platform.runLater(() ->
                getChildren().removeIf(node -> Objects.equals(node.getId(), keyMapper.apply(item)))
        );
    }
}
