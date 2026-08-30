package org.dreambot;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class PointBufferNio implements AutoCloseable {

    private final int MAX_SIZE;

    private final List<MousePoint> buffer = new ArrayList<>();
    private final Path filePath;

    public PointBufferNio(Path filePath, int size) {
        this.filePath = filePath;
        MAX_SIZE = size;
    }

    public void push(MousePoint point) throws IOException {
        buffer.add(point);

        if (buffer.size() >= MAX_SIZE) {
            flushToFile();
        }
    }

    private void flushToFile() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {

            for (MousePoint p : buffer) {
                writer.write(p.toString());
                writer.newLine();
            }
        }

        buffer.clear();
    }

    @Override
    public void close() throws IOException {
        if (!buffer.isEmpty()) {
            flushToFile();
        }
    }
}