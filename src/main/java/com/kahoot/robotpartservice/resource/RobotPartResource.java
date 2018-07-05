package com.kahoot.robotpartservice.resource;

import java.util.ArrayList;
import java.util.List;

public class RobotPartResource {
    private Long serialNumber;
    private String manufacturer;
    private Integer weight;
    private String partName;
    private List<Long> compatiblePartIds = new ArrayList<>();

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

    public List<Long> getCompatiblePartIds() {
        return compatiblePartIds;
    }

    public void setCompatiblePartIds(List<Long> compatiblePartIds) {
        this.compatiblePartIds = compatiblePartIds;
    }
}
