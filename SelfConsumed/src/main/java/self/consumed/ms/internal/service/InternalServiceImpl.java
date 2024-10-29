package self.consumed.ms.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import self.consumed.ms.configurations.LogsEnabler;
import self.consumed.ms.internal.dto.InternalExceptionThrowerDTO;
import self.consumed.ms.internal.dto.InternalOuterListDTO;
import self.consumed.ms.internal.dto.InternalOuterSingleDTO;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static self.consumed.ms.configurations.LogsEnabler.*;
import static self.consumed.ms.util.JsonMapperUtil.writeValueAsString;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalServiceImpl implements InternalService {
    private final LogsEnabler logsEnabler;
    private LogsEnabler.ClassRange classRange;

    @PostConstruct
    private void postConstruct() {
        classRange = logsEnabler.getClassRange(this.getClass());
    }

    @Override
    public InternalOuterListDTO getListData() {

        List<InternalOuterListDTO.InternalInnerListDTO.InternalActionDTO> listaAcciones =
                Arrays.asList(
                        InternalOuterListDTO.InternalInnerListDTO.InternalActionDTO
                                .builder()
                                .codigo("1")
                                .descripcion("one")
                                .build(),
                        InternalOuterListDTO.InternalInnerListDTO.InternalActionDTO
                                .builder()
                                .codigo("2")
                                .descripcion("two")
                                .build()
                );

        conditionalLog(getStackWalker().walk(infoInvokerStackFrameFunction), classRange,
                "VISIBILIDAD DE LOG CONTROLADO POR CONFIGURACION  ");

        InternalOuterListDTO.InternalInnerListDTO dato = InternalOuterListDTO.InternalInnerListDTO
                .builder()
                .entero(new Random().nextInt(10))
                .acciones(listaAcciones)
                .build();

        InternalOuterListDTO salida = InternalOuterListDTO
                .builder()
                .fecha(LocalDateTime.now())
                .dato(dato)
                .build();

        log.info("salida: {}", writeValueAsString(salida));
        return salida;
    }

    @Override
    public InternalOuterSingleDTO getSingleData() {
        String acciones = "One Action";

        InternalOuterSingleDTO.InternalInnerSingleDTO dato = InternalOuterSingleDTO.InternalInnerSingleDTO
                .builder()
                .entero(new Random().nextInt(10))
                .acciones(acciones)
                .build();

        InternalOuterSingleDTO salida = InternalOuterSingleDTO
                .builder()
                .fecha(LocalDateTime.now())
                .dato(dato)
                .build();

        log.info("salida: {}", writeValueAsString(salida));
        return salida;
    }

    @Override
    public InternalExceptionThrowerDTO getSimpleDTO() {
        var salida = InternalExceptionThrowerDTO.builder()
                .cadena("someString")
                .localDateTime(LocalDateTime.now())
                .build();

        log.info("salida: {}", writeValueAsString(salida));
        return salida;
    }

    @Override
    public InternalExceptionThrowerDTO getException() {
        var exception = new RuntimeException("internalExceptionInService");
        log.info("exception: {}", "RuntimeException(\"internalExceptionInService\")");
        throw exception;
    }

    private void privateMethod(String string, int number) {

    }
}
