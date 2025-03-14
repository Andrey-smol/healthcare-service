package service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.medical.MedicalServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

public class MedicalServiceImplTestes {
    private final PatientInfo patientInfo = new PatientInfo("1", "Koly", "Petrov", LocalDate.of(2025, Month.MARCH, 5),
            new HealthInfo(BigDecimal.valueOf(36.6), new BloodPressure(120, 80)));

    public static Stream<Arguments> testCheckBloodPressureMessage() {
        return Stream.of(
                Arguments.arguments("1", new BloodPressure(100, 70)),
                Arguments.arguments("1", new BloodPressure(130, 80)),
                Arguments.arguments("1", new BloodPressure(120, 90)),
                Arguments.arguments("1", new BloodPressure(120, 70))
        );
    }

    public static Stream<Arguments> testCheckTemperatureMessage() {
        return Stream.of(
                Arguments.arguments("1", BigDecimal.valueOf(35.0)),
                Arguments.arguments("1", BigDecimal.valueOf(34.0)),
                Arguments.arguments("1", BigDecimal.valueOf(31.0)),
                Arguments.arguments("1", BigDecimal.valueOf(37.0)),
                Arguments.arguments("1", BigDecimal.valueOf(38.0))
        );
    }

    public static Stream<Arguments> testCheckTemperatureNotMessage() {
        return Stream.of(
                Arguments.arguments("1", BigDecimal.valueOf(36.6)),
                Arguments.arguments("1", BigDecimal.valueOf(36.8)),
                Arguments.arguments("1", BigDecimal.valueOf(35.5))
        );
    }


    @ParameterizedTest
    @MethodSource
    public void testCheckBloodPressureMessage(String patientId, BloodPressure bloodPressure) {

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkBloodPressure(patientId, bloodPressure);

        Mockito.verify(patientInfoRepository, Mockito.times(1)).getById(Mockito.anyString());
        Mockito.verify(alertService, Mockito.times(1)).send(Mockito.anyString());
    }

    @Test
    public void testCheckBloodPressureNotMessage() {

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkBloodPressure("1", new BloodPressure(120, 80));

        Mockito.verify(patientInfoRepository, Mockito.times(1)).getById(Mockito.anyString());
        Mockito.verify(alertService, Mockito.times(0)).send(Mockito.anyString());
    }

    @ParameterizedTest
    @MethodSource
    public void testCheckTemperatureMessage(String patientId, BigDecimal temperature) {

        String expected = "Warning, patient with id: 1, need help";
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkTemperature(patientId, temperature);

        Mockito.verify(patientInfoRepository, Mockito.times(1)).getById(Mockito.anyString());
        Mockito.verify(alertService, Mockito.times(1)).send(Mockito.anyString());

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(alertService).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    @ParameterizedTest
    @MethodSource
    public void testCheckTemperatureNotMessage(String patientId, BigDecimal temperature) {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);

        SendAlertService alertService = Mockito.mock(SendAlertService.class);
        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl medicalService = new MedicalServiceImpl(patientInfoRepository, alertService);
        medicalService.checkTemperature(patientId, temperature);

        Mockito.verify(patientInfoRepository, Mockito.times(1)).getById(Mockito.anyString());
        Mockito.verify(alertService, Mockito.times(0)).send(Mockito.anyString());
    }

}
