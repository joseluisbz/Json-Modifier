package self.consumed.ms;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.nio.file.Files;

import static self.consumed.ms.util.JsonMapperUtil.getMapper;

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

        } catch (Exception e) {
            log.error("Error e: {}", e.getMessage(), e);
        }
    }

}
