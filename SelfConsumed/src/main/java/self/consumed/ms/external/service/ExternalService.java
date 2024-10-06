package self.consumed.ms.external.service;

import self.consumed.ms.external.dto.ExternalOuterDTO;

public interface ExternalService {
    ExternalOuterDTO getData(boolean multiple);
}
