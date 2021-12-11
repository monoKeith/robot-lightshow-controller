package com.keith.bot_control.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.scene.paint.Color;

import java.io.IOException;

public class ColorAdapter extends TypeAdapter<Color> {

    private static final String COLOR = "color";

    @Override
    public void write(JsonWriter jsonWriter, Color color) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(COLOR);
        jsonWriter.value(color.toString());
        jsonWriter.endObject();
    }

    @Override
    public Color read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
