package self.consumed.ms.external.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.consumed.ms.external.service.ExternalService;

@RestController
@RequestMapping("/${controllers.external}")
@RequiredArgsConstructor
public class ExternalController {

    private final ExternalService externalService;

    @GetMapping(value = "/data/{multiple}")
    public ResponseEntity<?> getInternalData(@PathVariable boolean multiple) {
        return ResponseEntity.ok(externalService.getData(multiple));
    }
}
