package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MedicalServiceImplTest {

    private PatientInfo patient = new PatientInfo("12345", "Семен", "Михайлов", LocalDate.of(1982, 1, 16),
            new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78)));
    private String id = "12345";
    private String message = String.format("Warning, patient with id: %s, need help", id);
    private BigDecimal currentTemperature = new BigDecimal("33");
    private BloodPressure currentPressure = new BloodPressure(120, 80);

    @Test
    void sendTest() {
        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        sendAlertService.send("123");
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals("123", argumentCaptor.getValue());
    }

    @Test
    void normalPressureAndTempTest() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patient);
        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure(id, patient.getHealthInfo().getBloodPressure());
        medicalService.checkTemperature(id, patient.getHealthInfo().getNormalTemperature());
        Mockito.verify(sendAlertService, Mockito.times(0)).send(argumentCaptor.capture());


    }

    @Test
    void checkBloodPressureTestWarning() {

        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patient);
        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkBloodPressure(id, currentPressure);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals(message, argumentCaptor.getValue());

    }

    @Test
    void checkTemperatureTestWarning() {
/**
 * Почему то проверка температуры, в отличии от давления, реализована в одну сторону (на понижение)
 *   if (patientInfo.getHealthInfo().getNormalTemperature().subtract(new BigDecimal("1.5")).compareTo(temperature) > 0)
 * */
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(id)).thenReturn(patient);
        SendAlertServiceImpl sendAlertService = Mockito.mock(SendAlertServiceImpl.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository, sendAlertService);
        medicalService.checkTemperature(id, currentTemperature);
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals(message, argumentCaptor.getValue());
    }
}