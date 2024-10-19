package self.consumed.ms.internal.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Builder
public class InternalExceptionThrowerDTO {
    String cadena;
    LocalDateTime localDateTime;
}
