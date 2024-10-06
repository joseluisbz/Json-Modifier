package self.consumed.ms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import self.consumed.ms.util.JsonMapperUtil;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static self.consumed.ms.util.JsonMapperUtil.getMapper;
import static self.consumed.ms.util.JsonMapperUtil.pathsJsonExcluder;

@Slf4j
@SpringBootApplication
public class SelfConsumedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelfConsumedApplication.class, args);
        try {
            File resource = new ClassPathResource(
                    "CertificadoLiquidacion.json").getFile();
            String contentFile = new String(
                    Files.readAllBytes(resource.toPath()));

            //log.info(System.lineSeparator() + "contentFile: {}" + System.lineSeparator(), contentFile);
            JsonNode parentJsonNode = getMapper().readTree(contentFile);
            //log.info(System.lineSeparator() + "parentJsonNode: {}" + System.lineSeparator(), parentJsonNode.asText());

            JsonNode jsonJsonNode = parentJsonNode.get("json");

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


            List<JsonMapperUtil.PathReplacer> listReplaced = List.of(

                    JsonMapperUtil.PathReplacer.builder()
                            .delete(false)
                            .oldPath("/fecha")
                            .newPath("Arreglo")
                            .object(arrayNode)
                            .build(),
                    JsonMapperUtil.PathReplacer.builder()
                            .delete(false)
                            .oldPath("/dato/entero")
                            .newPath("long")
                            .object(9L)
                            .build(),
                    JsonMapperUtil.PathReplacer.builder()
                            .delete(false)
                            .oldPath("/dato/actions/0")
                            .object(objectNode)
                            .build(),
                    JsonMapperUtil.PathReplacer.builder()
                            .delete(false)
                            .oldPath("/last")
                            .newPath("ultimo")
                            .object(objectNode)
                            .build());
            String jsonOutput = pathsJsonExcluder(jsonInput, listReplaced);
            System.out.println("jsonOutput:" + System.lineSeparator() + jsonOutput);

            jsonInput = "[{\"nombre\": \"John\", \"edad\": 30, \"direccion\": {\"calle\": \"Primera\",\"ciudad\": \"BAQ\"}}, {\"nombre\": \"Peter\", \"edad\": 25, \"direccion\": {\"calle\": \"Sexta\",\"ciudad\": \"BOG\"}}, {\"nombre\": \"Pepe\", \"edad\": 15, \"direccion\": {\"calle\": \"Decima\",\"ciudad\": \"CLO\"}}]";
            //System.out.println("jsonInput:" + System.lineSeparator() + jsonInput);
            //listExcluded = List.of("/1", "/0/direccion", "/2/direccion/calle");
            //jsonOutput = pathsJsonExcluder(jsonInput, listExcluded);
            //System.out.println("jsonOutput:" + System.lineSeparator() + jsonOutput);

        } catch (Exception e) {
            log.error("Error e: {}", e.getMessage(), e);
        }
    }

}
