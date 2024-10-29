package self.consumed.ms.external.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import self.consumed.ms.configurations.LogsEnabler;
import self.consumed.ms.external.client.ExternalInternalClient;
import self.consumed.ms.external.dto.ExternalOuterDTO;
import self.consumed.ms.external.dto.ExternalTestExceptionDTO;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final ExternalInternalClient externalInternalClient;
    private final LogsEnabler logsEnabler;
    private LogsEnabler.ClassRange classRange;

    @PostConstruct
    private void postConstruct() {
        classRange = logsEnabler.getClassRange(this.getClass());
        try {
            log.info("class: {}", this.getClass().getCanonicalName());
        } catch (Exception e) {
            log.error("exception-message: {}", e.getMessage());
        }
    }

    @Override
    public ExternalOuterDTO getData(boolean multiple) {
        return externalInternalClient.getInternalData(multiple);
    }

    @Override
    public ExternalTestExceptionDTO getThrow(boolean testThrow) {
        return externalInternalClient.getInternalException(testThrow);
    }
}
