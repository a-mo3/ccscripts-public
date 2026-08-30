package org.dreambot.comms;

import org.dreambot.api.utilities.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * We need to save server logs to an easy to find file because users cant find the client hosting the server
 */
public class AsyncBufferedLogger {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final int batchSize;
    private final String filePath;
    private final Thread workerThread;
    private volatile boolean running = true;

    public AsyncBufferedLogger(String filePath) {
        this(filePath, 5); // default batch size = 10
    }

    public AsyncBufferedLogger(String filePath, int batchSize) {
        this.filePath = filePath;
        this.batchSize = batchSize;

        this.workerThread = new Thread(this::processLoop, "AsyncBufferedLogger-Worker");
        this.workerThread.setDaemon(true); // optional, depends on your app
        this.workerThread.start();
    }

    public void log(String message) {
        // You might want to handle offer() failure, but for simplicity we use add()
        queue.add(message);
    }

    private void processLoop() {
        List<String> buffer = new ArrayList<>(batchSize);

        try {
            while (running || !queue.isEmpty()) {
                // Wait up to 500ms for a message (to allow periodic flush)
                String msg = queue.poll(500, TimeUnit.MILLISECONDS);

                if (msg != null) {
                    buffer.add(msg);
                }

                // If we reached the batch size, flush
                if (buffer.size() >= batchSize) {
                    flushToFile(buffer);
                    buffer.clear();
                }

                // If shutting down and queue is empty, flush remaining and exit
                if (!running && queue.isEmpty() && !buffer.isEmpty()) {
                    flushToFile(buffer);
                    buffer.clear();
                }
            }
        } catch (InterruptedException e) {
            // On interrupt, flush anything remaining then exit
            Thread.currentThread().interrupt();
            if (!buffer.isEmpty()) {
                flushToFile(buffer);
            }
        }
    }

    private void flushToFile(List<String> lines) {
        if (lines.isEmpty()) return;

        try {
            // Ensure parent directories exist
            File file = new File(filePath);
            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                if (!created && !parent.exists()) {
                    throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
            }
        } catch (IOException e) {
            // Real code: use fallback logging / monitoring here
            e.printStackTrace();
        }
    }

    public void shutdown() {
        running = false;
        workerThread.interrupt();
        try {
            workerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
