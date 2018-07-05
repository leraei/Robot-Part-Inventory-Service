package com.kahoot.robotpartservice.controller;

import com.kahoot.robotpartservice.model.RobotPart;
import com.kahoot.robotpartservice.resource.RobotPartResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import java.util.List;

@Api(tags = "RobotPart", consumes = "application/json", produces = "application/json")
public interface RobotPartController {

    @ApiOperation(
            value = "Finds a robot part by its serialnumber.",
            response = RobotPartResource.class)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Robot part for the given serialnumber was found"),
            @ApiResponse(
                    code = 404,
                    message = "No Robot part for the given serialnumber found")
    })
    ResponseEntity<RobotPartResource> findBySerialNumber(Long serialNumber);

    @ApiOperation(
            value = "Get all robot parts by its serialnumber.",
            response = RobotPartResource.class)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "0-n robot parts for the given serialnumber was found"),
    })
    ResponseEntity<List<RobotPartResource>> findAll();

    @ApiOperation(
            value = "Get all robot parts that are compatible with a given serialNumber.",
            response = RobotPartResource.class)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "0-n robot parts for the given serialnumber were found"),
    })
    ResponseEntity<List<RobotPartResource>> findCompatiblePartsForSerialNumber(Long serialNumber, Long limit);

    @ApiOperation(
            value = "Adds a new robot part to the inventory",
            response = RobotPartResource.class)
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "robot part was created"),
            @ApiResponse(
                    code = 409,
                    message = "No robot part was created. The given serialnumber is already in use")
    })
    ResponseEntity<RobotPartResource> create(RobotPartResource robotPartResource);

    @ApiOperation(
            value = "Updates a robot part with the given attributes.",
            notes = "Patch strategy is defined by RFC 7386 (https://tools.ietf.org/html/rfc7386).",
            response = RobotPart.class)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Robot part was updated with the given attributes"),
            @ApiResponse(
                    code = 404,
                    message = "No robot part for the given serialnumber found"),
            @ApiResponse(
                    code = 400,
                    message = "robot part could not get updated with the given attributes")
    })
    ResponseEntity<RobotPartResource> updatePartial(Long serialNumber, String robotPartResource, BindingResult bindingResult) throws BindException;

    @ApiOperation(
            value = "Replaces a existing robot part with the given RobotPartResource.",
            response = RobotPartResource.class)
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Robot part was updated with the given RobotPartResource"),
            @ApiResponse(
                    code = 404,
                    message = "No Robot part for the given serialnumber was found"),
    })
    ResponseEntity<RobotPartResource> updateFull(Long serialNumber, RobotPartResource robotPartResource);

    @ApiOperation(value = "Deletes a robot part")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Robot part was deleted"),
            @ApiResponse(
                    code = 404,
                    message = "No Robot part for the given serialnumber was found"),
    })
    ResponseEntity<Void> delete(Long serialNumber);

}
