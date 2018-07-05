package com.kahoot.robotpartservice.controller;

import com.kahoot.robotpartservice.mapper.JsonMergePatcher;
import com.kahoot.robotpartservice.resource.RobotPartResource;
import com.kahoot.robotpartservice.service.RobotPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(
        value = "v1/robotparts",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class RobotPartControllerImpl implements RobotPartController {

    private RobotPartService robotPartService;

    private JsonMergePatcher jsonMergePatcher;

    @Autowired
    public RobotPartControllerImpl(RobotPartService robotPartService, JsonMergePatcher jsonMergePatcher) {
        Assert.notNull(robotPartService, "robotPartService must not be null!");
        Assert.notNull(jsonMergePatcher, "jsonMergePatcher must not be null!");
        this.robotPartService = robotPartService;
        this.jsonMergePatcher = jsonMergePatcher;
    }

    @Override
    @GetMapping (value = "/{serialnumber}")
    public ResponseEntity<RobotPartResource> findBySerialNumber(@PathVariable("serialnumber") Long serialNumber) {
        Optional<RobotPartResource> robotPartResourceOptional = robotPartService.findBySerialNumber(serialNumber);
        return robotPartResourceOptional
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<RobotPartResource>> findAll() {
        return new ResponseEntity<>(robotPartService.findAll(), HttpStatus.OK);
    }

    @Override
    @GetMapping(value = "/{serialnumber}/compatible")
    public ResponseEntity<List<RobotPartResource>> findCompatiblePartsForSerialNumber(@PathVariable("serialnumber") Long serialNumber, @RequestParam(value = "limit") Long limit) {
        return new ResponseEntity<>(robotPartService.listCompatiblePartsForSerialNumber(serialNumber, limit), HttpStatus.OK);
    }

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RobotPartResource> create(@Valid @RequestBody RobotPartResource robotPartResource) {
        if (robotPartService.findBySerialNumber(robotPartResource.getSerialNumber()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        else {
            return new ResponseEntity<>(robotPartService.createRobotPart(robotPartResource), HttpStatus.CREATED);
        }
    }

    @Override
    @PatchMapping(value = "/{serialnumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RobotPartResource> updatePartial(@PathVariable("serialnumber") Long serialNumber, @RequestBody String robotPartResource, BindingResult bindingResult) throws BindException {
        Optional<RobotPartResource> targetOptional = robotPartService.findBySerialNumber(serialNumber);
        if (targetOptional.isPresent()) {
            Optional<RobotPartResource> patchedOptional = jsonMergePatcher.mergePatchAndValidate(robotPartResource, targetOptional.get(), bindingResult);
            if (patchedOptional.isPresent()) {
                Optional<RobotPartResource> robotPartResourceOptional = robotPartService.updateFull(serialNumber, patchedOptional.get());
                return robotPartResourceOptional
                        .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @PutMapping(value = "/{serialnumber}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RobotPartResource> updateFull(@PathVariable("serialnumber") Long serialNumber, @RequestBody @Valid RobotPartResource robotPartResource) {
        Optional<RobotPartResource> robotPartResourceOptional = robotPartService.updateFull(serialNumber, robotPartResource);
        return robotPartResourceOptional
                .map(c -> new ResponseEntity<>(c, HttpStatus.OK))
                .orElseGet(() ->new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Override
    @DeleteMapping(value = "/{serialnumber}")
    public ResponseEntity<Void> delete(@PathVariable(value = "serialnumber") Long serialNumber) {
        boolean deleted = robotPartService.deleteBySerialNumber(serialNumber);
        return deleted ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
