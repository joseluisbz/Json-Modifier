package self.consumed.ms.configurations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "logs-enabler")
@RequiredArgsConstructor
public class LogsEnabler {

    private final ObjectMapper objectMapper;

    private final static StackWalker sw = StackWalker.getInstance();

    public static Function<? super Stream<StackWalker.StackFrame>, String> infoInvokerStackFrameFunction = inputStreamStackFrame -> {
        Optional<StackWalker.StackFrame> optionalStackFrame = inputStreamStackFrame.findFirst();
        return optionalStackFrame
                .map(stackFrame -> stackFrame.getClassName().substring(0, stackFrame.getClassName().lastIndexOf('.'))
                        + ":" +
                        stackFrame.getClassName().substring(stackFrame.getClassName().lastIndexOf('.') + 1)
                        + ":" + stackFrame.getFileName()
                        + ":" + stackFrame.getMethodName()
                        + ":" + stackFrame.getLineNumber()
                ).orElse("");
    };

    private List<String> packages;
    private List<String> classes;
    private List<String> entriesRanges;
    private List<ClassRange> classesRanges;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassRange {
        private String clazz;
        private List<Range> ranges;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Range {
        private Integer min;
        private Integer max;
    }

    @PostConstruct
    private void postConstruct() {
        remappingValuesClassesRanges();
        try {
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        } catch (Exception e) {
            log.error("exception-message: {}", e.getMessage());
        }
    }

    private void remappingValuesClassesRanges() {
        //Order values
        classesRanges.forEach(cr -> cr.ranges.forEach(r -> {
            int int0 = r.min;
            int int1 = r.max;
            r.min = Math.min(int0, int1);
            r.max = Math.max(int0, int1);
        }));

        if (entriesRanges != null && !entriesRanges.isEmpty()) {
            List<ClassRange> newClassesRanges = entriesRanges.stream()
                    .map(entryRange ->
                    {
                        String er = entryRange.replace(" ", "");
                        String clazz = er.split(":")[0];
                        List<String> stringRanges = List.of(er.split(":")[1].split(";"));
                        var ranges = stringRanges.stream()
                                .map(range -> range
                                        .replace(")", "")
                                        .replace("(", "")
                                )
                                .map(range -> {
                                            int int0 = Integer.parseInt(range.split(",")[0]);
                                            int int1 = Integer.parseInt(range.split(",")[1]);
                                            return Range.builder()
                                                    .min(Math.min(int0, int1))
                                                    .max(Math.max(int0, int1))
                                                    .build();
                                        }
                                ).collect(Collectors.toList());

                        return ClassRange.builder().clazz(clazz).ranges(ranges).build();
                    }).collect(Collectors.toList());

            //Mezclar los rangos para una clase que ya estaba
            classesRanges.forEach(crOld ->
                    newClassesRanges.forEach(crNew -> {
                        if (crNew.getClazz().equals(crOld.getClazz())) {
                            crOld.ranges.addAll(crNew.ranges);
                        }
                    }));

            //Agregar los clases que no estaban
            newClassesRanges.stream()
                    .filter(crNew -> classesRanges.stream()
                            .noneMatch(crOld -> crOld.getClazz().equals(crNew.getClazz()))
                    ).forEach(crNew -> classesRanges.add(crNew));
        }
    }

    public static StackWalker getStackWalker() {
        return sw;
    }

    public ClassRange getClassRange(Class clazz) {
        String className = clazz.getName();
        String packageName = className.substring(0, className.lastIndexOf('.'));
        if (classes.contains(className) || packages.contains(packageName)) {
            Range range = Range.builder().min(Integer.MIN_VALUE).max(Integer.MAX_VALUE).build();
            return ClassRange.builder()
                    .clazz(className)
                    .ranges(List.of(range))
                    .build();
        }
        return classesRanges.stream()
                .filter(cr -> cr.clazz.equals(className))
                .findFirst().orElse(null);
    }

    public static void conditionalLog(String stackFrameInfo, ClassRange classRange, String message) {
        int lineToLog = Integer.parseInt(stackFrameInfo.split(":")[4]);
        classRange.ranges.stream()
                .filter(r -> (r.min <= lineToLog && lineToLog <= r.max))
                .forEach(r -> log.info(String.format("\n%s - message: \n\t%s", stackFrameInfo, message)));
    }

}

