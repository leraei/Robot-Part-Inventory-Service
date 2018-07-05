package com.kahoot.robotpartservice.mapper;

import com.kahoot.robotpartservice.model.RobotPart;
import com.kahoot.robotpartservice.repository.RobotPartRepository;
import com.kahoot.robotpartservice.resource.RobotPartResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RobotPartMapper {

    private RobotPartRepository robotPartRepository;

    @Autowired
    public RobotPartMapper(RobotPartRepository robotPartRepository) {
        Assert.notNull(robotPartRepository, "robotPartRepository must not be null");
        this.robotPartRepository = robotPartRepository;
    }

    public RobotPartResource mapToRobotPartResource(RobotPart robotPart) {
        RobotPartResource robotPartResource = new RobotPartResource();
        robotPartResource.setSerialNumber(robotPart.getSerialNumber());
        robotPartResource.setManufacturer(robotPart.getManufacturer());
        robotPartResource.setWeight(robotPart.getWeight());
        robotPartResource.setPartName(robotPart.getPartName());

        robotPartResource.setCompatiblePartIds(robotPart.getCompatibleParts().stream().map(RobotPart::getSerialNumber).collect(Collectors.toList()));

        return robotPartResource;
    }

    public RobotPart mapToRobotPart(RobotPartResource robotPartResource) {
        RobotPart robotPart = new RobotPart();
        robotPart.setSerialNumber(robotPartResource.getSerialNumber());
        robotPart.setManufacturer(robotPartResource.getManufacturer());
        robotPart.setWeight(robotPartResource.getWeight());
        robotPart.setPartName(robotPartResource.getPartName());

        robotPartResource.getCompatiblePartIds().stream()
                .map(serialNumber -> robotPartRepository.findBySerialNumber(serialNumber))
                .filter(Optional::isPresent)
                .forEach(compatibelPart -> robotPart.addToCompatibleParts(compatibelPart.get()));

        return robotPart;
    }

    public void mapInToRobotPart(RobotPartResource robotPartResource, RobotPart robotPart) {
        robotPart.setManufacturer(robotPartResource.getManufacturer());
        robotPart.setWeight(robotPartResource.getWeight());
        robotPart.setPartName(robotPartResource.getPartName());

        robotPartResource.getCompatiblePartIds().stream()
                .map(serialNumber -> robotPartRepository.findBySerialNumber(serialNumber))
                .filter(Optional::isPresent)
                .forEach(compatibelPart -> robotPart.addToCompatibleParts(compatibelPart.get()));
    }
}
