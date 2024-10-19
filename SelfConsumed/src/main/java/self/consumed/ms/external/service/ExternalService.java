package self.consumed.ms.external.service;

import self.consumed.ms.external.dto.ExternalOuterDTO;
import self.consumed.ms.external.dto.ExternalTestExceptionDTO;
import self.consumed.ms.testing.TestingDelete;
import self.consumed.ms.testing.TestingInsert;
import self.consumed.ms.testing.TestingReplace;

public interface ExternalService {
    ExternalOuterDTO getData(boolean multiple);
    ExternalTestExceptionDTO getThrow(boolean testThrow);

    default String testDelete() {
        return TestingDelete.testDelete();
    }
    default String testReplace() {
        return TestingReplace.testReplace();
    }
    default String testInsert() {
        return TestingInsert.testInsert();
    }
}
