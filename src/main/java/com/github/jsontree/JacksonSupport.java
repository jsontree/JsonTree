package com.github.jsontree;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.github.jsontree.exception.ServiceException;
import com.github.jsontree.node.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JacksonSupport {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final ObjectMapper defaultMapper = new ObjectMapper();

    static {

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        defaultMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        defaultMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        defaultMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        defaultMapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        defaultMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        defaultMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        defaultMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    }

    public static ValueNode parse(String json) {
        try {
            return mapper.readValue(json, ValueNode.class);
        } catch (Exception e) {
            String message = "json parse error :"
                    + (json == null ? "null" : json.substring(0, Math.min(100, json.length())));
            throw new RuntimeException(message, e);
        }
    }

    public static ValueNode parse(File file) {
        try {
            return mapper.readValue(file, ValueNode.class);
        } catch (Exception e) {
            String message = "file json parse error ";
            throw new RuntimeException(message, e);
        }
    }

    public static <T> T parseJson(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException("Deserialize from JSON failed.", e);
        }
    }

    public static <T> T parseJsonToList(String json, TypeReference<T> typeReference) {
        if (json == null || "".equals(json.trim())) {
            return null;
        }
        try {
            return mapper.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Deserialize from JSON failed.", e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json format error: " + obj, e);
        }
    }

    public static String toTrimjson(Object obj) {
        try {
            return defaultMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json format error: " + obj, e);
        }
    }

    public static void write(ValueNode node, JsonGenerator gen) throws IOException {
        switch (node.getType()) {
            case bool:
                gen.writeBoolean(node.toBooleanValue());
                break;
            case NULL:
                gen.writeNull();
                break;
            case number:
                writeNumber(node, gen);
                break;
            case string:
                gen.writeString(node.toString());
                break;
            case list:
                gen.writeStartArray();
                for (ValueNode child : node) {
                    write(child, gen);
                }
                gen.writeEndArray();
                break;
            case map:
                gen.writeStartObject();
                for (Map.Entry<String, ValueNode> entry : node.entrySet()) {
                    gen.writeFieldName(entry.getKey());
                    write(entry.getValue(), gen);
                }
                gen.writeEndObject();
                break;
            default:
                break;
        }
    }

    private static void writeNumber(ValueNode node, JsonGenerator gen) throws IOException {
        Number num = node.toNumber();
        if (num instanceof Integer) {
            gen.writeNumber(num.intValue());
        } else if (num instanceof Long) {
            gen.writeNumber(num.longValue());
        } else if (num instanceof Double) {
            gen.writeNumber(num.doubleValue());
        } else {
            gen.writeNumber(num.floatValue());
        }
    }

    private static ValueNode parseToken(JsonToken token, JsonParser jp) throws Exception {
        switch (token) {
            case START_OBJECT:
                return parseMap(jp);
            case START_ARRAY:
                return parseArray(jp);
            case VALUE_STRING:
                return new StringNode(jp.getValueAsString());
            case VALUE_NUMBER_FLOAT:
            case VALUE_NUMBER_INT:
                return new NumericNode(jp.getNumberValue());
            case VALUE_NULL:
                return NullNode.getRoot();
            case VALUE_FALSE:
            case VALUE_TRUE:
                return new BooleanValue(jp.getBooleanValue());
            default:
                throw new ServiceException("意外的的token: " + token);
        }
    }

    private static ValueNode parseArray(JsonParser jp) throws IOException {
        ListNode node = new ListNode();
        JsonToken token = jp.nextToken();
        while (token != JsonToken.END_ARRAY) {
            ValueNode vn = null;
            try {
                vn = parseToken(token, jp);
            } catch (Exception ignored) {
                return null;
            }
            node.add(vn);
            token = jp.nextToken();
        }
        return node;
    }

    private static MapNode parseMap(JsonParser jp) throws IOException {
        MapNode node = new MapNode();
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            ValueNode vn = null;
            try {
                vn = parseToken(jp.nextToken(), jp);
            } catch (Exception ignored) {
                return null;
            }
            node.set(fieldName, vn);
        }
        return node;
    }

    public static class ValueNodeSerializer extends JsonSerializer<ValueNode> {
        @Override
        public void serialize(ValueNode value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            write(value, jgen);
        }
    }

    public static class ValueNodeDeserializer extends JsonDeserializer<ValueNode> {
        @Override
        public ValueNode deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            try {
                return parseToken(jp.getCurrentToken(), jp);
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}
