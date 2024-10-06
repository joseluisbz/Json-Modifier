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

    public static String pathsJsonExcluder(String jsonInput, List<PathReplacer> listExcluded) throws IOException {
        JsonNode jsonNodeInput = getMapper().readTree(jsonInput);
        StringWriter stringWriter = new StringWriter();
        ReplacerDTO replacerDTO = ReplacerDTO
                .builder()
                .generator(getMapper().getFactory().createGenerator(stringWriter))
                .stringWriter(stringWriter)
                .pathDeque(new ArrayDeque<>())
                .isRootObject(jsonNodeInput.isObject())
                .isRootArray(jsonNodeInput.isArray())
                .replacedProperties(listExcluded)
                .build();

        traverse(replacerDTO, jsonNodeInput, "", replacerDTO.isRootArray);
        replacerDTO.generator.flush();

        JsonNode jsonNodeOutput = getMapper().readTree(stringWriter.toString());
        return getMapper().writeValueAsString(jsonNodeOutput);
    }

    // https://jenkov.com/tutorials/java-json/jackson-jsonnode.html#convert-jsonnode-field
    private static void traverse(ReplacerDTO replacerDTO, JsonNode currentJsonNode, String currentFieldName,
                                 boolean previousIsArray) throws IOException {
        if (currentFieldName.isEmpty()) {
            if (replacerDTO.isRootObject) {
                replacerDTO.generator.writeStartObject();
            }
            if (replacerDTO.isRootArray) {
                replacerDTO.generator.writeStartArray();
            }
        }
        final String PATH_SEPARATOR = "/";
        List<String> paths = new ArrayList<>(replacerDTO.pathDeque);
        Collections.reverse(paths);
        String currentPath = PATH_SEPARATOR + String.join(PATH_SEPARATOR, paths);
        Optional<PathReplacer> optionalFoundPathReplacer = replacerDTO.replacedProperties
                .stream()
                .filter(pr -> pr.oldPath.equals(currentPath))
                .findAny();

        if (optionalFoundPathReplacer.isPresent()) {
            PathReplacer pathReplacer = optionalFoundPathReplacer.get();
            if (!pathReplacer.isDelete()) {
                if (pathReplacer.object != null) {
                    JsonNode jsonNode = getMapper().valueToTree(pathReplacer.object);
                    if (jsonNode.isBoolean() || jsonNode.isTextual() || jsonNode.isNumber() || jsonNode.isArray() ||
                            jsonNode.isObject() && !previousIsArray) {
                        replacerDTO.generator.writeFieldName(pathReplacer.newPath);
                        replacerDTO.generator.writeObject(jsonNode);
                    }
                    if (jsonNode.isObject() && previousIsArray) {
                        replacerDTO.generator.writeObject(jsonNode);
                    }
                } else {
                    if (previousIsArray) {
                        replacerDTO.generator.writeEmbeddedObject(null);
                    } else {
                        replacerDTO.generator.writeNullField(pathReplacer.newPath);
                    }
                }
            }
            return;
        }
        if (currentJsonNode.isObject()) {
            if (previousIsArray) {
                replacerDTO.generator.writeStartObject();
            } else {
                if (!currentFieldName.isEmpty()) {
                    replacerDTO.generator.writeFieldName(currentFieldName);
                    replacerDTO.generator.writeStartObject();
                }
            }

            Iterator<String> fieldNames = currentJsonNode.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                replacerDTO.pathDeque.push(fieldName);
                JsonNode fieldValue = currentJsonNode.get(fieldName);
                traverse(replacerDTO, fieldValue, fieldName, false);
                replacerDTO.pathDeque.pop();
            }
            replacerDTO.generator.writeEndObject();

        } else if (currentJsonNode.isArray()) {
            if (replacerDTO.isRootObject || replacerDTO.isRootArray && !currentPath.equals(PATH_SEPARATOR)) {
                replacerDTO.generator.writeFieldName(currentFieldName);
            }
            replacerDTO.generator.writeStartArray();
            ArrayNode arrayNode = (ArrayNode) currentJsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                replacerDTO.pathDeque.push(String.valueOf(i));
                JsonNode arrayElement = arrayNode.get(i);
                traverse(replacerDTO, arrayElement, String.valueOf(i), true);
                replacerDTO.pathDeque.pop();
            }
            replacerDTO.generator.writeEndArray();
        } else {
            replacerDTO.generator.writeObjectField(currentFieldName, currentJsonNode);
        }
    }

    @Data
    @Builder
    private static class ReplacerDTO {
        private JsonGenerator generator;
        private StringWriter stringWriter;
        private Deque<String> pathDeque;
        private boolean isRootObject;
        private boolean isRootArray;
        private List<PathReplacer> replacedProperties;
    }

    @Data
    @Builder
    public static class PathReplacer {
        private boolean delete;
        private String oldPath;
        private String newPath;
        private Object object;
    }
}
