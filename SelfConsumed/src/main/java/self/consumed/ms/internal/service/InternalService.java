package self.consumed.ms.internal.service;

import self.consumed.ms.internal.dto.InternalExceptionThrowerDTO;
import self.consumed.ms.internal.dto.InternalOuterListDTO;
import self.consumed.ms.internal.dto.InternalOuterSingleDTO;

public interface InternalService {
    InternalOuterListDTO getListData();
    InternalOuterSingleDTO getSingleData();
    InternalExceptionThrowerDTO getSimpleDTO();
    InternalExceptionThrowerDTO getException();
}
