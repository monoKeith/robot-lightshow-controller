package com.keith.bot_control.model;

import com.google.gson.*;
import javafx.geometry.Point2D;

import java.lang.reflect.Type;

public class PointAdapter implements JsonSerializer<Point2D>, JsonDeserializer<Point2D> {

    private static final String X = "X";
    private static final String Y = "Y";

    @Override
    public JsonElement serialize(Point2D point2D, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(X, new JsonPrimitive(point2D.getX()));
        jsonObject.add(Y, new JsonPrimitive(point2D.getY()));
        return jsonObject;
    }

    @Override
    public Point2D deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double x = jsonObject.get(X).getAsDouble();
        double y = jsonObject.get(Y).getAsDouble();
        return new Point2D(x, y);
    }

}
