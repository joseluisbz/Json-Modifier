package self.consumed.ms.testing;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import self.consumed.ms.util.JsonMapperUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static self.consumed.ms.util.JsonMapperUtil.pathsJsonExcluder;

public class TestingReplace {
    public static void testReplace() throws IOException {

        String jsonInput = "{\"fecha\":\"2024-09-16T20:12:35.1034441\",\"dato\":{\"entero\":5,\"actions\":[{\"code\":\"1\",\"description\":\"one\"},{\"code\":\"2\",\"description\":\"two\"}]}, \"last\":100000}";
        System.out.println("jsonInput:" + System.lineSeparator() + jsonInput);
        List<String> listExcluded = List.of("/dato/entero", "/dato/actions/0");
        // https://stackoverflow.com/a/74188917/811293
        ObjectNode objectNode = JsonMapper.builder().build().createObjectNode()
                .put("entero", 7)
                .put("cadena", "Cadena");

        ArrayNode arrayNode = JsonMapper.builder().build().createArrayNode();
        arrayNode.add(
                JsonMapper.builder().build().createObjectNode()
                        .put("big_decimal", BigDecimal.valueOf(345.7))
                        .putNull("nulo")
        );
        arrayNode.add(
                JsonMapper.builder().build().createObjectNode()
                        .put("reviewed", true)
                        .put("nueva_fecha", LocalDateTime.now().toString())
        );


        List<JsonMapperUtil.PathModifier> listModified = List.of(
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.REPLACE)
                        .oldPath("/fecha")
                        .newPath("Arreglo")
                        .object(arrayNode)
                        .build(),
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.REPLACE)
                        .oldPath("/dato/entero")
                        .newPath("long")
                        .object(9L)
                        .build(),
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.REPLACE)
                        .oldPath("/dato/actions/0")
                        .object(objectNode)
                        .build(),
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.REPLACE)
                        .oldPath("/last")
                        .newPath("ultimo")
                        .object(objectNode)
                        .build());
        String jsonOutput = pathsJsonExcluder(jsonInput, listModified);
        System.out.println("jsonOutput:" + System.lineSeparator() + jsonOutput);
    }
}
