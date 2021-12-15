package com.keith.bot_control.model;

import com.google.gson.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

    private static final String R = "R", G = "G", B = "B";

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(R, new JsonPrimitive(color.getRed()));
        jsonObject.add(G, new JsonPrimitive(color.getGreen()));
        jsonObject.add(B, new JsonPrimitive(color.getBlue()));
        return jsonObject;
    }

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double r = jsonObject.get(R).getAsDouble();
        double g = jsonObject.get(G).getAsDouble();
        double b = jsonObject.get(B).getAsDouble();
        return Color.color(r, g, b);
    }
}
