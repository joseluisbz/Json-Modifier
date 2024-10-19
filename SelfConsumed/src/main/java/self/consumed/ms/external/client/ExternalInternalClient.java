package self.consumed.ms.external.client;

import self.consumed.ms.external.dto.ExternalOuterDTO;
import self.consumed.ms.external.dto.ExternalTestExceptionDTO;

public interface ExternalInternalClient {

    ExternalOuterDTO getInternalData(boolean multiple);

    public ExternalTestExceptionDTO getInternalException(boolean testThrow);
}
