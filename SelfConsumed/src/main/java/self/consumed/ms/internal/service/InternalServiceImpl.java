package self.consumed.ms.internal.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import self.consumed.ms.internal.dto.InternalOuterListDTO;
import self.consumed.ms.internal.dto.InternalOuterSingleDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static self.consumed.ms.util.JsonMapperUtil.writeValueAsString;

@Slf4j
@Service
public class InternalServiceImpl implements InternalService {

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

}
