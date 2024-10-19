package self.consumed.ms.external.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import self.consumed.ms.external.client.ExternalInternalClient;
import self.consumed.ms.external.dto.ExternalOuterDTO;
import self.consumed.ms.external.dto.ExternalTestExceptionDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final ExternalInternalClient externalInternalClient;

    @Override
    public ExternalOuterDTO getData(boolean multiple) {
        return externalInternalClient.getInternalData(multiple);
    }

    @Override
    public ExternalTestExceptionDTO getThrow(boolean testThrow) {
        return externalInternalClient.getInternalException(testThrow);
    }
}
