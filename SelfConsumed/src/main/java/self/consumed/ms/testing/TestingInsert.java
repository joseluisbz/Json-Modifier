package self.consumed.ms.testing;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import self.consumed.ms.util.JsonMapperUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static self.consumed.ms.util.JsonMapperUtil.pathsJsonModifier;

public class TestingInsert {
    public static String testInsert() {
        System.out.println();
        System.out.println("testInsert");

        String jsonInput = "{\"fecha\":\"2024-09-16T20:12:35.1034441\",\"dato\":{\"entero\":5,\"actions\":[{\"code\":\"1\",\"description\":\"one\"},{\"code\":\"2\",\"description\":\"two\"}]}}";
        System.out.println("jsonInput:" + System.lineSeparator() + jsonInput);

        // https://stackoverflow.com/a/74188917/811293
        ObjectNode objectNode = JsonMapper.builder().build().createObjectNode()
                .put("calle", 8)
                .put("carrera", 4)
                .put("numero", "12");

        ArrayNode arrayNode = JsonMapper.builder().build().createArrayNode()
                .add(JsonMapper.builder().build().createObjectNode()
                        .put("celular", Long.valueOf(3217209548L))
                        .putNull("extension")
                ).add(JsonMapper.builder().build().createObjectNode()
                        .put("codigo", "302")
                        .put("disponible", true)
                        .put("fecha_actualizacion", LocalDate.now().toString())
                        .put("hora_actualizacion", LocalTime.now().toString())
                );

        List<JsonMapperUtil.PathModifier> listPathModifier = List.of(
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.INSERT)
                        .newPath("/contacts")
                        .object(arrayNode)
                        .build(),
                /*Revisar*/

                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.INSERT)
                        .newPath("/dato/actions/1/arreglo")
                        .object(arrayNode)
                        .build()
                ,

                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.INSERT)
                        .newPath("/dato/actions/2")
                        .object(objectNode)
                        .build()

                ,
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.INSERT)
                        .newPath("/dato/address")
                        .object(objectNode)
                        .build()

        );

        String jsonOutput = pathsJsonModifier(jsonInput, listPathModifier);
        System.out.println("jsonOutput:" + System.lineSeparator() + jsonOutput);

        System.out.println("testInsert" );
        System.out.println();
        return jsonOutput;
    }
}
