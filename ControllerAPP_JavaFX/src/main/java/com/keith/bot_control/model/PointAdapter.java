package com.keith.bot_control.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.geometry.Point2D;

import java.io.IOException;

public class PointAdapter extends TypeAdapter<Point2D> {

    private static final String X = "X";
    private static final String Y = "Y";

    @Override
    public void write(JsonWriter jsonWriter, Point2D point2D) throws IOException {
        jsonWriter.beginObject();
        jsonWriter.name(X);
        jsonWriter.value(point2D.getX());
        jsonWriter.name(Y);
        jsonWriter.value(point2D.getY());
        jsonWriter.endObject();
    }

    @Override
    public Point2D read(JsonReader jsonReader) throws IOException {

        return null;
    }
}
