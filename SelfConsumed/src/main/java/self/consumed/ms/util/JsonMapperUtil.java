package self.consumed.ms.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class JsonMapperUtil {

    private JsonMapperUtil() {
    }

    public static ObjectMapper getMapper() {
        return new ObjectMapper()
                //.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false)
                //.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                //.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .registerModule(new JavaTimeModule());
    }

    public static String writeValueAsString(@NotNull Object object) {
        try {
            return JsonMapperUtil.getMapper().writeValueAsString(object);
        } catch (JsonProcessingException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    public static <T> T writeStringAsObject(@NotNull String string, @NotNull Class<T> clazz) {
        try {
            return (T) JsonMapperUtil.getMapper().readValue(string, clazz);
        } catch (JsonProcessingException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    public static String pathsJsonExcluder(String jsonInput, List<PathModifier> listExcluded) throws IOException {
        JsonNode jsonNodeInput = getMapper().readTree(jsonInput);
        StringWriter stringWriter = new StringWriter();
        ModifierDTO modifierDTO = ModifierDTO
                .builder()
                .generator(getMapper().getFactory().createGenerator(stringWriter))
                .stringWriter(stringWriter)
                .pathDeque(new ArrayDeque<>())
                .isRootObject(jsonNodeInput.isObject())
                .isRootArray(jsonNodeInput.isArray())
                .modifiedProperties(listExcluded)
                .build();

        traverse(modifierDTO, jsonNodeInput, "", modifierDTO.isRootArray);
        modifierDTO.generator.flush();

        System.out.println("stringWriter.toString(): " + stringWriter.toString());

        JsonNode jsonNodeOutput = getMapper().readTree(stringWriter.toString());
        return getMapper().writeValueAsString(jsonNodeOutput);
    }

    // https://jenkov.com/tutorials/java-json/jackson-jsonnode.html#convert-jsonnode-field
    private static void traverse(ModifierDTO modifierDTO, JsonNode currentJsonNode, String currentFieldName,
                                 boolean previousIsArray) throws IOException {
        if (currentFieldName.isEmpty()) {
            if (modifierDTO.isRootObject) {
                modifierDTO.generator.writeStartObject();
            }
            if (modifierDTO.isRootArray) {
                modifierDTO.generator.writeStartArray();
            }
        }
        final String PATH_SEPARATOR = "/";
        List<String> paths = new ArrayList<>(modifierDTO.pathDeque);
        Collections.reverse(paths);
        String currentPath = PATH_SEPARATOR + String.join(PATH_SEPARATOR, paths);
        System.out.println("currentFieldName: " + currentFieldName + ", currentPath: " + currentPath);
        Optional<PathModifier> optionalFoundPathReplacer = modifierDTO.modifiedProperties
                .stream()
                .filter(pr -> pr.oldPath.equals(currentPath))
                .findAny();

        if (optionalFoundPathReplacer.isPresent()) {
            PathModifier pathModifier = optionalFoundPathReplacer.get();
            if (pathModifier.operation == Operation.REPLACE) {
                if (pathModifier.object != null) {
                    JsonNode jsonNode = getMapper().valueToTree(pathModifier.object);
                    if (jsonNode.isBoolean() || jsonNode.isTextual() || jsonNode.isNumber() || jsonNode.isArray() ||
                            jsonNode.isObject() && !previousIsArray) {
                        modifierDTO.generator.writeFieldName(pathModifier.newPath);
                        modifierDTO.generator.writeObject(jsonNode);
                    }
                    if (jsonNode.isObject() && previousIsArray) {
                        modifierDTO.generator.writeObject(jsonNode);
                    }
                } else {
                    if (previousIsArray) {
                        modifierDTO.generator.writeEmbeddedObject(null);
                    } else {
                        modifierDTO.generator.writeNullField(pathModifier.newPath);
                    }
                }
                return;
            }
            if (pathModifier.operation == Operation.DELETE) {
                return;
            }
        }
        if (currentJsonNode.isObject()) {
            if (previousIsArray) {
                modifierDTO.generator.writeStartObject();
            } else {
                if (!currentFieldName.isEmpty()) {
                    modifierDTO.generator.writeFieldName(currentFieldName);
                    modifierDTO.generator.writeStartObject();
                }
            }

            Iterator<String> fieldNames = currentJsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                modifierDTO.pathDeque.push(fieldName);
                JsonNode fieldValue = currentJsonNode.get(fieldName);
                traverse(modifierDTO, fieldValue, fieldName, false);
                modifierDTO.pathDeque.pop();
            }
            modifierDTO.generator.writeEndObject();

        } else if (currentJsonNode.isArray()) {
            if (modifierDTO.isRootObject || modifierDTO.isRootArray && !currentPath.equals(PATH_SEPARATOR)) {
                modifierDTO.generator.writeFieldName(currentFieldName);
            }
            modifierDTO.generator.writeStartArray();
            ArrayNode arrayNode = (ArrayNode) currentJsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                modifierDTO.pathDeque.push(String.valueOf(i));
                JsonNode arrayElement = arrayNode.get(i);
                traverse(modifierDTO, arrayElement, String.valueOf(i), true);
                modifierDTO.pathDeque.pop();
            }
            modifierDTO.generator.writeEndArray();
        } else {
            modifierDTO.generator.writeObjectField(currentFieldName, currentJsonNode);
        }
    }

    @Data
    @Builder
    private static class ModifierDTO {
        private JsonGenerator generator;
        private StringWriter stringWriter;
        private Deque<String> pathDeque;
        private boolean isRootObject;
        private boolean isRootArray;
        private List<PathModifier> modifiedProperties;
    }

    @Data
    @Builder
    public static class PathModifier {
        private Operation operation;
        private String oldPath;
        private String newPath;
        private Object object;
    }

    public enum Operation {
        DELETE,
        REPLACE,
        INSERT;
    }
}
