package org.development.blogApi.modules.quiz.question.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public class StrictBooleanDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_TRUE) {
            return true;
        } else if (p.getCurrentToken() == JsonToken.VALUE_FALSE) {
            return false;
        }
        throw new JsonMappingException(p, "Only boolean values true or false are allowed");
    }
}
