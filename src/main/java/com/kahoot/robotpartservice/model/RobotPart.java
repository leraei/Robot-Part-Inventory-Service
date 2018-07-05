package com.kahoot.robotpartservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class RobotPart {

    @Id
    private Long serialNumber;

    private String manufacturer;

    private Integer weight;

    private String partName;

    @JoinTable(name = "part_has_compatible_parts", joinColumns = {
            @JoinColumn(name = "original", referencedColumnName = "serialNumber", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "compatible", referencedColumnName = "serialNumber", nullable = false)})
    @ManyToMany
    private List<RobotPart> compatibleParts = new ArrayList<>();

    public  void  addToCompatibleParts(RobotPart robotPart) {
        compatibleParts.add(robotPart);
    }


    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public List<RobotPart> getCompatibleParts() {
        return compatibleParts;
    }

    public void setCompatibleParts(List<RobotPart> compatibleParts) {
        this.compatibleParts = compatibleParts;
    }
}
