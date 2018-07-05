package com.kahoot.robotpartservice.service;

import com.kahoot.robotpartservice.extension.MockitoExtension;
import com.kahoot.robotpartservice.mapper.RobotPartMapper;
import com.kahoot.robotpartservice.model.RobotPart;
import com.kahoot.robotpartservice.repository.RobotPartRepository;
import com.kahoot.robotpartservice.resource.RobotPartResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RobotPartServiceTest {
    @Mock
    RobotPartRepository robotPartRepositoryMock;

    @Mock
    RobotPartMapper robotPartMapperMock;

    @InjectMocks
    RobotPartService robotPartService;


    @BeforeEach
    void setup() {
        robotPartService = new RobotPartService(robotPartRepositoryMock, robotPartMapperMock);
    }


    @Test
    void testCreateRonbotPart() {
        RobotPart robotPart = new RobotPart();
        robotPart.setManufacturer("manufacturer");

        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setManufacturer("manufacturer");

        when(robotPartRepositoryMock.save(any(RobotPart.class))).thenReturn(robotPart);
        when(robotPartMapperMock.mapToRobotPartResource(any())).thenReturn(robotPartResource);

        RobotPartResource returned = robotPartService.createRobotPart(robotPartResource);

        assertEquals("manufacturer", returned.getManufacturer());
        verify(robotPartRepositoryMock, times(1)).save(any());
    }

    @Test
    void testFindById() {
        RobotPart robotPart = new RobotPart();
        robotPart.setManufacturer("manufacturer");
        robotPart.setSerialNumber(1L);
        robotPart.setPartName("partName");
        robotPart.setWeight(100);

        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setManufacturer("manufacturer");
        robotPartResource.setSerialNumber(1L);
        robotPartResource.setPartName("partName");
        robotPartResource.setWeight(100);

        when(robotPartRepositoryMock.findBySerialNumber(1L)).thenReturn(Optional.of(robotPart));
        when(robotPartMapperMock.mapToRobotPartResource(robotPart)).thenReturn(robotPartResource);
        Optional<RobotPartResource> returned = robotPartService.findBySerialNumber(1L);

        assertTrue(returned.isPresent());
        assertAll("returned",

                () -> assertEquals("manufacturer", returned.get().getManufacturer()),
                () -> assertEquals(1, returned.get().getSerialNumber().longValue()),
                () -> assertEquals("partName", returned.get().getPartName()),
                () -> assertEquals(100, returned.get().getWeight().intValue())
        );
    }


    @Test
    void testFindByIdNotFound() {
        when(robotPartRepositoryMock.findBySerialNumber(1L)).thenReturn(Optional.empty());
        Optional<RobotPartResource> returned = robotPartService.findBySerialNumber(1L);
        assertFalse(returned.isPresent());
    }

    @Test
    void testListCompatiblePartsForSerialNumber() {
        RobotPart compatible = new RobotPart();
        compatible.setManufacturer("manufacturer");
        compatible.setSerialNumber(2L);
        compatible.setPartName("partName");
        compatible.setWeight(100);

        RobotPart robotPart = new RobotPart();
        robotPart.setManufacturer("manufacturer");
        robotPart.setSerialNumber(1L);
        robotPart.setPartName("partName");
        robotPart.setWeight(100);
        robotPart.addToCompatibleParts(compatible);

        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setManufacturer("manufacturer");
        robotPartResource.setSerialNumber(1L);
        robotPartResource.setPartName("partName");
        robotPartResource.setWeight(100);
        robotPartResource.setCompatiblePartIds(Collections.singletonList(2L));

        when(robotPartRepositoryMock.findBySerialNumber(1L)).thenReturn(Optional.of(robotPart));
        when(robotPartMapperMock.mapToRobotPartResource(compatible)).thenReturn(robotPartResource);

        List<RobotPartResource> compatibleParts = robotPartService.listCompatiblePartsForSerialNumber(1L, 10L);

        assertEquals(1, compatibleParts.size());
        assertEquals(1, compatibleParts.get(0).getSerialNumber().longValue());
    }

    @Test
    void testFindAll() {
        RobotPart robotPart = new RobotPart();
        robotPart.setSerialNumber(1L);
        RobotPart robotPart2 = new RobotPart();
        robotPart2.setSerialNumber(2L);
        List<RobotPart> robotParts = Arrays.asList(robotPart, robotPart2);

        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);
        RobotPartResource robotPartResource2 = new RobotPartResource();
        robotPartResource2.setSerialNumber(2L);

        when(robotPartRepositoryMock.findAll()).thenReturn(robotParts);
        when(robotPartMapperMock.mapToRobotPartResource(robotPart)).thenReturn(robotPartResource);
        when(robotPartMapperMock.mapToRobotPartResource(robotPart2)).thenReturn(robotPartResource2);


        List<RobotPartResource> returned = robotPartService.findAll();

        assertEquals(2, returned.size());
        assertEquals(1, returned.get(0).getSerialNumber().longValue());
        assertEquals(2, returned.get(1).getSerialNumber().longValue());
    }

    @Test
    void testUpdateFull() {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setManufacturer("manufacturer");
        robotPartResource.setSerialNumber(1L);
        robotPartResource.setPartName("partName");
        robotPartResource.setWeight(100);

        RobotPart existingRobotPart = new RobotPart();
        existingRobotPart.setManufacturer("manufacturer");
        existingRobotPart.setSerialNumber(1L);
        existingRobotPart.setPartName("partName");
        existingRobotPart.setWeight(100);

        RobotPart savedRobotPart = new RobotPart();
        savedRobotPart.setManufacturer("xiaomi");
        savedRobotPart.setSerialNumber(1L);
        savedRobotPart.setPartName("head");
        savedRobotPart.setWeight(200);

        RobotPartResource savedRobotPartResource = new RobotPartResource();
        savedRobotPartResource.setManufacturer("xiaomi");
        savedRobotPartResource.setSerialNumber(1L);
        savedRobotPartResource.setPartName("head");
        savedRobotPartResource.setWeight(200);

        doNothing().when(robotPartMapperMock).mapInToRobotPart(robotPartResource, existingRobotPart);
        when(robotPartMapperMock.mapToRobotPartResource(savedRobotPart)).thenReturn(savedRobotPartResource);
        when(robotPartRepositoryMock.findBySerialNumber(1L)).thenReturn(Optional.of(existingRobotPart));
        when(robotPartRepositoryMock.save(any(RobotPart.class))).thenReturn(savedRobotPart);

        Optional<RobotPartResource> returned = robotPartService.updateFull(1L, robotPartResource);

        assertTrue(returned.isPresent());
        assertAll("returned",
                () -> assertEquals("xiaomi", returned.get().getManufacturer()),
                () -> assertEquals(1, returned.get().getSerialNumber().longValue()),
                () -> assertEquals("head", returned.get().getPartName()),
                () -> assertEquals(200, returned.get().getWeight().intValue())
        );
    }

    @Test
    void testDeleteBySerialNumber() {
        RobotPart existingRobotPart = new RobotPart();
        existingRobotPart.setManufacturer("manufacturer");
        existingRobotPart.setSerialNumber(1L);
        existingRobotPart.setPartName("partName");
        existingRobotPart.setWeight(100);

        when(robotPartRepositoryMock.findBySerialNumber(1L)).thenReturn(Optional.of(existingRobotPart));

        boolean deleteBySerialNumber = robotPartService.deleteBySerialNumber(1L);

        assertTrue(deleteBySerialNumber);
    }

    @Test
    void testDeleteBySerialNumber_notFound() {
        when(robotPartRepositoryMock.findBySerialNumber(1L)).thenReturn(Optional.empty());

        boolean deleteBySerialNumber = robotPartService.deleteBySerialNumber(1L);

        assertFalse(deleteBySerialNumber);
    }







}