package self.consumed.ms.external.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.consumed.ms.external.service.ExternalService;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/${controllers.external}")
@RequiredArgsConstructor
public class ExternalController {

    private final ExternalService externalService;
    private HttpHeaders httpHeaders;

    @PostConstruct
    private void postConstruct() {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @GetMapping(value = "/data/{multiple}")
    public ResponseEntity<?> getInternalData(@PathVariable boolean multiple) {
        return ResponseEntity.ok(externalService.getData(multiple));
    }

    @GetMapping(value = "/exception/{testThrow}")
    public ResponseEntity<?> getInternalException(@PathVariable boolean testThrow) {
        return ResponseEntity.ok(externalService.getThrow(testThrow));
    }

    @GetMapping(value = "test-delete")
    public ResponseEntity<String> getTestDelete() {
        return new ResponseEntity<String>(externalService.testDelete(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "test-replace")
    public ResponseEntity<String> getTestReplace() {
        return new ResponseEntity<String>(externalService.testReplace(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping(value = "test-insert")
    public ResponseEntity<String> getTestInsert() {
        return new ResponseEntity<String>(externalService.testInsert(), httpHeaders, HttpStatus.OK);
    }
}
