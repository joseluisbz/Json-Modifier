package self.consumed.ms.internal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Jacksonized
@Data
@Builder
public class InternalOuterListDTO {

    @JsonAlias("date")
    private LocalDateTime fecha;

    @JsonAlias("data")
    private InternalInnerListDTO dato;

    @JsonAlias("boolean")
    private boolean boleano = true;

    @Jacksonized
    @Data
    @Builder
    public static class InternalInnerListDTO {

        @JsonAlias("integer")
        private Integer entero;

        @JsonAlias("actions")
        private List<InternalActionDTO> acciones;

        @Data
        @Builder
        @JsonInclude(JsonInclude.Include.NON_DEFAULT)
        public static class InternalActionDTO {
            @JsonProperty("code")
            private String codigo;

            @JsonProperty("description")
            private String descripcion;
        }

    }
}
