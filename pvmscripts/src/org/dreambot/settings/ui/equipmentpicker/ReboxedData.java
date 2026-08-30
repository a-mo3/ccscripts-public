package org.dreambot.settings.ui.equipmentpicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class ReboxedData {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static Map<Integer, EquipmentDataModel> loadData(String filename, String url) throws IOException {
        File file = new File(filename);

        Type listType = new TypeToken<Map<Integer, EquipmentDataModel>>() {
        }.getType();
        // If the file exists, load it and parse it with Gson
        if (file.exists()) {
            System.out.println("File found, loading data from file...");
            try (Reader reader = new FileReader(file)) {
                return gson.fromJson(reader, listType);
            }
        } else {
            // If the file doesn't exist, request the URL and save the response to the file
            System.out.println("File not found, requesting data from URL...");
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                // Save the response to the file
                String jsonResponse = response.body().string();
                Files.write(Paths.get(filename), jsonResponse.getBytes());

                // Parse the response as a list of objects
                return gson.fromJson(jsonResponse, listType);
            }
        }
    }
}
