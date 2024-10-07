package self.consumed.ms.testing;

import self.consumed.ms.util.JsonMapperUtil;

import java.io.IOException;
import java.util.List;

import static self.consumed.ms.util.JsonMapperUtil.pathsJsonExcluder;

public class TestingDelete {
    public static void testDelete() throws IOException {
        String jsonInput = "{\"fecha\":\"2024-09-16T20:12:35.1034441\",\"dato\":{\"entero\":5,\"actions\":[{\"code\":\"1\",\"description\":\"one\"},{\"code\":\"2\",\"description\":\"two\"}]}}";
        System.out.println("jsonInput:" + System.lineSeparator() + jsonInput);
        List<JsonMapperUtil.PathModifier> listExcluded = List.of(
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.DELETE)
                        .oldPath("/dato/entero")
                        .build(),
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.DELETE)
                        .oldPath("/dato/actions/0")
                        .build()
        );
        String jsonOutput = pathsJsonExcluder(jsonInput, listExcluded);
        System.out.println("jsonOutput:" + System.lineSeparator() + jsonOutput);

        jsonInput = "[{\"nombre\": \"John\", \"edad\": 30, \"direccion\": {\"calle\": \"Primera\",\"ciudad\": \"BAQ\"}}, {\"nombre\": \"Peter\", \"edad\": 25, \"direccion\": {\"calle\": \"Sexta\",\"ciudad\": \"BOG\"}}, {\"nombre\": \"Pepe\", \"edad\": 15, \"direccion\": {\"calle\": \"Decima\",\"ciudad\": \"CLO\"}}]";
        System.out.println("jsonInput:" + System.lineSeparator() + jsonInput);
        listExcluded = List.of(
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.DELETE)
                        .oldPath("/1")
                        .build(),
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.DELETE)
                        .oldPath("/0/direccion")
                        .build(),
                JsonMapperUtil.PathModifier.builder()
                        .operation(JsonMapperUtil.Operation.DELETE)
                        .oldPath("/2/direccion/calle")
                        .build()
        );
        jsonOutput = pathsJsonExcluder(jsonInput, listExcluded);
        System.out.println("jsonOutput:" + System.lineSeparator() + jsonOutput);
    }
}
