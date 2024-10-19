package self.consumed.ms.external.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Jacksonized
@Data
@Builder
public class ExternalTestExceptionDTO {
    String cadena;
    LocalDateTime localDateTime;
}
