package self.consumed.ms.external.client;

import self.consumed.ms.external.dto.ExternalOuterDTO;

public interface ExternalInternalClient {

    ExternalOuterDTO getInternalData(boolean multiple);
}
