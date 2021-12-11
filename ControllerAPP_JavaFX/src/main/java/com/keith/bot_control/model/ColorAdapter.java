package com.keith.bot_control.model;

import com.google.gson.*;
import javafx.scene.paint.Color;

import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(color.toString());
    }

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        String colorCode = jsonElement.toString();
        return Color.web(colorCode);
    }
}
