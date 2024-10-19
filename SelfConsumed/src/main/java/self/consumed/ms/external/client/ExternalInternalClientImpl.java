package self.consumed.ms.external.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import self.consumed.ms.external.dto.ExternalOuterDTO;
import self.consumed.ms.external.dto.ExternalTestExceptionDTO;

import java.net.URI;

import static self.consumed.ms.util.JsonMapperUtil.writeValueAsString;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalInternalClientImpl implements ExternalInternalClient {

    @Value("${server.port}")
    private String serverPort;

    @Value("${controllers.internal}")
    private String controllerMapping;

    private final RestTemplate restTemplate;

    @Override
    public ExternalOuterDTO getInternalData(boolean multiple) {
        log.info("calling multiple: {}", multiple);

        String urlEndPoint = "http://localhost:{serverPort}/{controller}/data/multiple/{multiple}";
        String url = UriComponentsBuilder.fromUriString(urlEndPoint)
                .buildAndExpand(serverPort, controllerMapping, multiple)
                .toUriString();
        log.info("url: {}", url);
        URI uri = URI.create(url);

        HttpEntity<Object> entity = new HttpEntity<>(HttpHeaders.class);

        ExternalOuterDTO response =
                restTemplate.exchange(uri, HttpMethod.GET, entity, ExternalOuterDTO.class)
                        .getBody();
        log.info("response: {}", writeValueAsString(response));
        /*
        Object objectResponse =
                restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class)
                        .getBody();
        log.info("objectResponse: {}", objectResponse);

        String stringResponse = writeValueAsString(objectResponse);
        log.info("stringResponse: {}", stringResponse);
        ExternalOuterDTO ExternalOuterDTOresponse = writeStringAsObject(stringResponse, ExternalOuterDTO.class);
        log.info("ExternalOuterDTOresponse: {}", writeValueAsString(ExternalOuterDTOresponse));*/
        return response;
    }

    @Override
    public ExternalTestExceptionDTO getInternalException(boolean testThrow) {
        log.info("calling testThrow: {}", testThrow);

        String urlEndPoint = "http://localhost:{serverPort}/{controller}/exception/throw/{testThrow}";
        String url = UriComponentsBuilder.fromUriString(urlEndPoint)
                .buildAndExpand(serverPort, controllerMapping, testThrow)
                .toUriString();
        log.info("url: {}", url);
        URI uri = URI.create(url);

        HttpEntity<Object> entity = new HttpEntity<>(HttpHeaders.class);

        ExternalTestExceptionDTO response =
                restTemplate.exchange(uri, HttpMethod.GET, entity, ExternalTestExceptionDTO.class)
                        .getBody();
        log.info("response: {}", writeValueAsString(response));
        return response;
    }
}
