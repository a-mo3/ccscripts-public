package com.piler;

import com.ccscripts.actions.AbstractAction;
import com.ccscripts.actions.ActionType;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ActionDeserializer implements JsonDeserializer<AbstractAction> {
    @Override
    public AbstractAction deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        JsonElement typeElement = obj.get("type");
        if (typeElement == null) {
            throw new JsonParseException("Missing required field 'type'");
        }

        ActionType actionType;
        try {
            actionType = ActionType.valueOf(
                    typeElement.getAsString());
        } catch (IllegalArgumentException e) {
            throw new JsonParseException("Unknown action type: " + typeElement.getAsString(), e);
        }

        return jsonDeserializationContext.deserialize(
                obj,
                actionType.getClazz());
    }
}
