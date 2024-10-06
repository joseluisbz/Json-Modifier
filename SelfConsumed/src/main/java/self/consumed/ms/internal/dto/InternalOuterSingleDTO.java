package self.consumed.ms.internal.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InternalOuterSingleDTO {

    @JsonAlias("date")
    private LocalDateTime fecha;

    @JsonAlias("data")
    private InternalInnerSingleDTO dato;

    @JsonAlias("boolean")
    private boolean boleano = true;

    @Data
    @Builder
    public static class InternalInnerSingleDTO {

        @JsonAlias("integer")
        private Integer entero;

        @JsonAlias("actions")
        private String acciones;
    }

}
