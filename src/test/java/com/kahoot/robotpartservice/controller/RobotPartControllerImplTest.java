package com.kahoot.robotpartservice.controller;

import com.kahoot.robotpartservice.extension.MockitoExtension;
import com.kahoot.robotpartservice.mapper.JsonMergePatcher;
import com.kahoot.robotpartservice.resource.RobotPartResource;
import com.kahoot.robotpartservice.service.RobotPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RobotPartControllerImplTest {

    @Mock
    RobotPartService robotPartServiceMock;

    @Mock
    JsonMergePatcher jsonMergePatcherMock;

    @Mock
    BindingResult bindingResultMock;

    @InjectMocks
    RobotPartControllerImpl robotPartController;


    @BeforeEach
    void setup() {
        robotPartController = new RobotPartControllerImpl(robotPartServiceMock, jsonMergePatcherMock);
    }


    @Test
    void testFindBySerialNumber() {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.findBySerialNumber(1L)).thenReturn(Optional.of(robotPartResource));

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.findBySerialNumber(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1L, responseEntity.getBody().getSerialNumber().longValue());
    }

    @Test
    void testFindBySerialNumber_notFound() {
        when(robotPartServiceMock.findBySerialNumber(1L)).thenReturn(Optional.empty());

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.findBySerialNumber(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testCreate() {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.createRobotPart(robotPartResource)).thenReturn(robotPartResource);

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.create(robotPartResource);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void testCreate_conflict() {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.findBySerialNumber(1L)).thenReturn(Optional.of(robotPartResource));

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.create(robotPartResource);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testUpdateFull() {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.updateFull(1L, robotPartResource)).thenReturn(Optional.of(robotPartResource));

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.updateFull(1L, robotPartResource);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }


    @Test
    void testUpdateFull_notFound() {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.updateFull(1L, robotPartResource)).thenReturn(Optional.empty());

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.updateFull( 1L, robotPartResource);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void testUpdatePartial() throws BindException {
        String robotPartResourceString = "{'serialNumber' : 1}";

        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.findBySerialNumber(1L)).thenReturn(Optional.of(robotPartResource));
        when(jsonMergePatcherMock.mergePatchAndValidate(robotPartResourceString, robotPartResource, bindingResultMock)).thenReturn(Optional.of(robotPartResource));
        when(robotPartServiceMock.updateFull(1L, robotPartResource)).thenReturn(Optional.of(robotPartResource));

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.updatePartial(1L, robotPartResourceString, bindingResultMock);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }


    @Test
    void testUpdatePartial_NotFound() throws BindException {
        String robotPartResourceString =  "{'serialNumber' : 1}";

        when(robotPartServiceMock.findBySerialNumber(1L)).thenReturn(Optional.empty());

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.updatePartial( 1L, robotPartResourceString, bindingResultMock);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


    @Test
    void testUpdatePartial_badJson() throws BindException {
        String robotPartResourceString =  "this is a bad json";

        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(1L);

        when(robotPartServiceMock.findBySerialNumber(1L)).thenReturn(Optional.of(robotPartResource));
        when(jsonMergePatcherMock.mergePatchAndValidate(robotPartResourceString, robotPartResource, bindingResultMock)).thenReturn(Optional.empty());

        ResponseEntity<RobotPartResource> responseEntity = robotPartController.updatePartial( 1L, robotPartResourceString, bindingResultMock);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }




}