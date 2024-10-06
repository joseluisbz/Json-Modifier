package self.consumed.ms.external.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import self.consumed.ms.external.client.ExternalInternalClient;
import self.consumed.ms.external.dto.ExternalOuterDTO;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final ExternalInternalClient externalInternalClient;

    @Override
    public ExternalOuterDTO getData(boolean multiple) {
        return externalInternalClient.getInternalData(multiple);
    }
}
