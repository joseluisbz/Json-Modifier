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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    public static String minify(@NotNull String string) {
        try {
            JsonNode jsonNode = JsonMapperUtil.getMapper().readTree(string);
            return JsonMapperUtil.writeValueAsString(jsonNode);
        } catch (JsonProcessingException x) {
            throw new IllegalArgumentException(x.getMessage(), x);
        }
    }

    public static String pathsJsonModifier(@NotNull String jsonInput, @NotNull List<PathModifier> listPathModifier) {
        try {
            JsonNode jsonNodeInput = getMapper().readTree(jsonInput);
            StringWriter stringWriter = new StringWriter();
            ModifierDTO modifierDTO = ModifierDTO
                    .builder()
                    .generator(getMapper().getFactory().createGenerator(stringWriter))
                    .stringWriter(stringWriter)
                    .pathDeque(new ArrayDeque<>())
                    .isRootObject(jsonNodeInput.isObject())
                    .isRootArray(jsonNodeInput.isArray())
                    .modifiedPaths(listPathModifier)
                    .build();

            traverse(modifierDTO, jsonNodeInput, "", modifierDTO.isRootArray);
            modifierDTO.generator.flush();

            JsonNode jsonNodeOutput = getMapper().readTree(stringWriter.toString());
            return getMapper().writeValueAsString(jsonNodeOutput);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
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

        inserter(modifierDTO, true, currentJsonNode, currentFieldName, currentPath, modifierDTO.isRootArray, PATH_SEPARATOR);

        Optional<PathModifier> optionalFoundPathDeleter = modifierDTO.modifiedPaths
                .stream()
                .filter(pm -> pm.operation == Operation.DELETE)
                .filter(pm -> pm.oldPath.equals(currentPath))
                .findAny();
        if (optionalFoundPathDeleter.isPresent()) {
            return;
        }

        Optional<PathModifier> optionalFoundPathReplacer = modifierDTO.modifiedPaths
                .stream()
                .filter(pm -> pm.operation == Operation.REPLACE)
                .filter(pm -> pm.oldPath.equals(currentPath))
                .findAny();

        if (optionalFoundPathReplacer.isPresent()) {
            PathModifier pathModifier = optionalFoundPathReplacer.get();
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
        if (currentJsonNode.isObject()) {
            if (previousIsArray) {
                modifierDTO.generator.writeStartObject();
            } else {
                if (!currentFieldName.isEmpty()) {
                    modifierDTO.generator.writeFieldName(currentFieldName);
                    modifierDTO.generator.writeStartObject();
                }
            }

            inserter(modifierDTO, false, currentJsonNode, currentFieldName, currentPath, false, PATH_SEPARATOR);

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
            if (!currentFieldName.isEmpty()) {
                modifierDTO.generator.writeStartArray();
            }
            ArrayNode arrayNode = (ArrayNode) currentJsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                String currentArrayPath = currentPath + PATH_SEPARATOR + i;
                boolean incrementIndex = modifierDTO.modifiedPaths
                        .stream().filter(pm -> pm.operation != Operation.DELETE) // Review
                        .anyMatch(mp -> mp.newPath.equals(currentArrayPath));

                int incrementArray = 0;
                modifierDTO.modifiedPaths.stream().forEach(
                        mp ->
                        {
                            if (currentArrayPath.equals(mp.newPath)) {
                                inserter(modifierDTO, false, currentJsonNode, currentFieldName, currentArrayPath, true, PATH_SEPARATOR);
                            }
                        }
                );
                if (incrementIndex) {
                    incrementArray++;
                }
                modifierDTO.pathDeque.push(String.valueOf(i + incrementArray));
                JsonNode arrayElement = arrayNode.get(i);
                traverse(modifierDTO, arrayElement, String.valueOf(i + incrementArray), true);
                modifierDTO.pathDeque.pop();
            }
            //Funciona al Final
            inserter(modifierDTO, false, currentJsonNode, currentFieldName, currentPath, true, PATH_SEPARATOR);

            modifierDTO.generator.writeEndArray();
        } else {
            modifierDTO.generator.writeObjectField(currentFieldName, currentJsonNode);
        }
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void inserter(ModifierDTO modifierDTO, boolean initial, JsonNode currentJsonNode, String currentFieldName,
                                 String currentPath, boolean previousIsArray, String PATH_SEPARATOR) {
        modifierDTO.modifiedPaths
                .stream()
                .filter(pm -> !pm.processed)
                .filter(pm -> pm.operation == Operation.INSERT)
                .forEach(pmI -> {
                    if (pmI.newPath.startsWith(currentPath)) {
                        String currentPathRemoved = pmI.newPath.replaceFirst(currentPath, "");

                        Iterable<String> iterable = currentJsonNode::fieldNames;
                        List<String> listFieldNames = StreamSupport
                                .stream(iterable.spliterator(), false)
                                .collect(Collectors.toList());

                        Optional<String> optionalPathFound =
                                listFieldNames.stream().filter(fn ->
                                        (currentPathRemoved.startsWith(PATH_SEPARATOR + fn) || currentPathRemoved.startsWith(fn))
                                ).findAny();

                        String[] tokensPath = currentPathRemoved.split(PATH_SEPARATOR);
                        int indexFind = tokensPath.length > 1 && isInteger(tokensPath[1]) ? Integer.parseInt(tokensPath[1]) : -1;
                        boolean isIndexContained = currentJsonNode.isArray() && (indexFind >= 0 && currentJsonNode.size() > indexFind);

                        boolean comparison = initial ? pmI.waitFor == null : currentPath.equals(pmI.waitFor);
                        if (!isIndexContained && optionalPathFound.isEmpty() && comparison) {
                            pmI.processed = true;
                            String[] newPaths = pmI.newPath.split(PATH_SEPARATOR);
                            String newPath = newPaths[newPaths.length - 1];
                            try {
                                if (previousIsArray) {
                                    modifierDTO.generator.writeObject(pmI.object);
                                } else {
                                    modifierDTO.generator.writeObjectField(newPath, pmI.object);
                                }

                            } catch (IOException e) {
                                System.out.println("NO SE PUDO AGREGAR: " + e.getMessage());
                            }
                        }

                        if (isIndexContained || optionalPathFound.isPresent()) {
                            if (PATH_SEPARATOR.equals(currentPath)) {
                                pmI.waitFor = currentPath + optionalPathFound.get();
                            } else {
                                pmI.waitFor = optionalPathFound.map(s -> currentPath + PATH_SEPARATOR + s).orElseGet(() -> currentPath + PATH_SEPARATOR + indexFind);
                            }
                        }
                    }
                });
    }

    @Data
    @Builder
    private static class ModifierDTO {
        private JsonGenerator generator;
        private StringWriter stringWriter;
        private Deque<String> pathDeque;
        private boolean isRootObject;
        private boolean isRootArray;
        private List<PathModifier> modifiedPaths;
    }

    @Data
    @Builder
    public static class PathModifier {
        private Operation operation;
        private String oldPath;
        private String newPath;
        private Object object;
        private boolean processed;
        private String waitFor;
    }

    public enum Operation {
        DELETE,
        REPLACE,
        INSERT;
    }
}
