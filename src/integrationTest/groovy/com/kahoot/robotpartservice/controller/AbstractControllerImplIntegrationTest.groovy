package com.kahoot.robotpartservice.controller

import com.kahoot.robotpartservice.model.RobotPart
import com.kahoot.robotpartservice.repository.RobotPartRepository
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import javax.persistence.EntityManagerFactory

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given

@ActiveProfiles(profiles = "test")
@TestExecutionListeners([
        DependencyInjectionTestExecutionListener
])
abstract class AbstractControllerImplIntegrationTest extends Specification {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected RobotPartRepository robotPartRepository

    protected MockMvcRequestSpecification mockMvcRequestSpecification

    @Autowired
    protected EntityManagerFactory entityManagerFactory

    protected DefaultMockMvcBuilder mockMvcBuilder

    void setupMockMvc() {
        mockMvcBuilder = MockMvcBuilders.webAppContextSetup(context)

        assert mockMvcBuilder != null: "integration test must set mockMvcBuilder @Before..."

        mockMvcRequestSpecification = given().standaloneSetup(mockMvcBuilder)
    }

    void cleanup() {
        robotPartRepository.deleteAll()
    }


    RobotPart createRobotPart(Long serialNumber) {
        final RobotPart robotPart = new RobotPart()
        robotPart.setSerialNumber(serialNumber)
        robotPart.setManufacturer("manufacturer")
        robotPart.setPartName("partName")
        robotPart.setWeight(100)
        return robotPart
    }

}
