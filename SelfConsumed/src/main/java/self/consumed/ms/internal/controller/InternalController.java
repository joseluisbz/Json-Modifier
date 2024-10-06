package self.consumed.ms.internal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import self.consumed.ms.internal.service.InternalService;

@RestController
@RequestMapping("/${controllers.internal}")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping(value = "data/multiple/true")
    public ResponseEntity<?> getListData() {
        return ResponseEntity.ok(internalService.getListData());
    }

    @GetMapping(value = "data/multiple/false")
    public ResponseEntity<?> getSingleData() {
        return ResponseEntity.ok(internalService.getSingleData());
    }
}
