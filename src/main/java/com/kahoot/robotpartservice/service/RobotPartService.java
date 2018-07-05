package com.kahoot.robotpartservice.service;

import com.kahoot.robotpartservice.mapper.RobotPartMapper;
import com.kahoot.robotpartservice.model.RobotPart;
import com.kahoot.robotpartservice.repository.RobotPartRepository;
import com.kahoot.robotpartservice.resource.RobotPartResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RobotPartService {

    private RobotPartRepository robotPartRepository;

    private RobotPartMapper robotPartMapper;

    @Autowired
    public RobotPartService(RobotPartRepository robotPartRepository, RobotPartMapper robotPartMapper) {
        Assert.notNull(robotPartRepository, "robotPartRepository must not be null!");
        Assert.notNull(robotPartMapper, "robotPartMapper must not be null!");
        this.robotPartRepository = robotPartRepository;
        this.robotPartMapper = robotPartMapper;
    }

    /**
     * Creates a new RobotPart from a RobotPartResource and persists it to the database
     * @param robotPartResource the given RobotPartResource
     * @return the created RoboPart
     */
    public RobotPartResource createRobotPart(RobotPartResource robotPartResource) {
        RobotPart robotPart = robotPartMapper.mapToRobotPart(robotPartResource);
        RobotPart created = robotPartRepository.save(robotPart);
        return robotPartMapper.mapToRobotPartResource(created);
    }

    /**
     * Finds a robotPart by its serialnumber
     * @param serialNumber the given serialnumber
     * @return an Optional of the found robotPart
     */
    public Optional<RobotPartResource> findBySerialNumber(Long serialNumber) {
        Optional<RobotPart> robotPartOptional = robotPartRepository.findBySerialNumber(serialNumber);
        return robotPartOptional.map(robotPart -> robotPartMapper.mapToRobotPartResource(robotPart));
    }

    /**
     * Finds and returns all available robotParts
     * @return all available robotParts
     */
    public List<RobotPartResource> findAll() {
        List<RobotPart> allRobotParts = robotPartRepository.findAll();
        return allRobotParts.stream().map(robotPart -> robotPartMapper.mapToRobotPartResource(robotPart)).collect(Collectors.toList());
    }



    /**
     * Updates a robotPart with the given RobotPartResource
     * @param serialNumber the serialNumber of the robotPart you want to updates
     * @param robotPartResource the given RobotPartResource
     * @return A Optional of the updated RobotPart as a Resource or Optional.empty() if no existing RobotPart was found
     */
    public Optional<RobotPartResource> updateFull(Long serialNumber, RobotPartResource robotPartResource) {
        Optional<RobotPart> robotPartOptional = robotPartRepository.findBySerialNumber(serialNumber);
        return robotPartOptional.map(robotPart -> {
            robotPartMapper.mapInToRobotPart(robotPartResource, robotPart);
            RobotPart saved = robotPartRepository.save(robotPart);
            return Optional.of(robotPartMapper.mapToRobotPartResource(saved));
        }).orElseGet(Optional::empty);
    }

    /**
     * Deletes a robot part by the given serialNumber
     * @param serialNumber the given serialNumber
     * @return true if something was deleted, false if not
     */
    public boolean deleteBySerialNumber(Long serialNumber) {
        Optional<RobotPart> robotPartOptional = robotPartRepository.findBySerialNumber(serialNumber);
        if (robotPartOptional.isPresent()) {
            robotPartRepository.delete(robotPartOptional.get());
            return true;
        }
        return false;
    }

    /**
     * Finds a robotPart and gets all compatible robot parts that will be returned.
     * @param serialNumber the given serialNumber
     * @return A list with all compatible parts
     */
    public List<RobotPartResource> listCompatiblePartsForSerialNumber(Long serialNumber, Long limit) {
        Optional<RobotPart> robotPartOptional = robotPartRepository.findBySerialNumber(serialNumber);
        if(robotPartOptional.isPresent()){
            return robotPartOptional.get().getCompatibleParts().stream()
                    .limit(limit)
                    .map(c -> robotPartMapper.mapToRobotPartResource(c))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }




}
