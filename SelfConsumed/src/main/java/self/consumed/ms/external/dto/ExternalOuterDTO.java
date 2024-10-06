package self.consumed.ms.external.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static self.consumed.ms.util.JsonMapperUtil.writeStringAsObject;
import static self.consumed.ms.util.JsonMapperUtil.writeValueAsString;

//@Slf4j
@Jacksonized
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalOuterDTO {

    @JsonAlias("date")
    private LocalDateTime fecha;

    @JsonAlias("data")
    private ExternalInnerDataDTO dato;

    @Jacksonized
    @Data
    @Builder
    public static class ExternalInnerDataDTO {

        @JsonAlias("integer")
        private Integer entero;

        @JsonAlias("actions")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private Object acciones;

        @JsonIgnore
        private String single;

        @JsonIgnore
        private List<ExternalActionDTO> lista;

        @JsonGetter("actions")
        public List<ExternalActionDTO> getLista() {

            List<ExternalActionDTO> listExternalAction = new ArrayList<>();
            if (this.acciones instanceof List) {
                List<?> temporalObjectsList = new ArrayList<>((List<?>) this.acciones);
                temporalObjectsList.forEach(o -> {
                    String string = writeValueAsString(o);
                    ExternalActionDTO externalActionDTO = writeStringAsObject(string, ExternalActionDTO.class);
                    /*
                    log.info(System.lineSeparator() + "Object: {}" +
                                    System.lineSeparator() + "String: {}" +
                                    System.lineSeparator() + "externalActionDTO: {}",
                            o, string, externalActionDTO);
                    */
                    listExternalAction.add(externalActionDTO);
                });
                this.lista = listExternalAction;
                this.single = null;
            }
            if (this.acciones instanceof String) {
                this.lista = Collections.emptyList();
                this.single = (String) acciones;
            }
            return this.lista;
        }

        @Jacksonized
        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public static class ExternalActionDTO {
            @JsonProperty("code")
            private String codigo;

            @JsonProperty("description")
            private String descripcion;
        }

    }

}
