package self.consumed.ms.external.service;

import self.consumed.ms.external.dto.ExternalOuterDTO;
import self.consumed.ms.external.dto.ExternalTestExceptionDTO;

public interface ExternalService {
    ExternalOuterDTO getData(boolean multiple);
    ExternalTestExceptionDTO getThrow(boolean testThrow);
}
