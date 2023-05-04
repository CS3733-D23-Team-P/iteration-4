package edu.wpi.punchy_pegasi.backend;

import edu.wpi.punchy_pegasi.frontend.Screen;
import lombok.Data;
import lombok.RequiredArgsConstructor;


public class AppSearch {

    @RequiredArgsConstructor
    @Data
    public static class SearchableItem {
        private final String description;
        private final Screen screen;
        private final Runnable navigate;
        private int currentWeight;
    }
    public static void initialize() throws Exception {
    }
}
