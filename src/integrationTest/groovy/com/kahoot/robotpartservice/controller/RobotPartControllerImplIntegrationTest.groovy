package com.kahoot.robotpartservice.controller

import com.kahoot.robotpartservice.repository.RobotPartRepository
import io.restassured.http.ContentType
import org.hamcrest.Matchers
import org.hamcrest.core.IsNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration

import static org.hamcrest.core.IsEqual.equalTo

@SpringBootTest()
@WebAppConfiguration
@TestExecutionListeners([
        DependencyInjectionTestExecutionListener
])
class RobotPartControllerImplIntegrationTest extends AbstractControllerImplIntegrationTest {

    @Autowired
    private RobotPartRepository robotPartRepository

    void setup() {
        setupMockMvc()
    }


    void findBySerialNumber() {
        expect:
        def robotPart = robotPartRepository.save(createRobotPart(123))

        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .get("v1/robotparts/123")
                .then()
                .statusCode(200)
                .body("serialNumber", { response -> equalTo(123) })
                .body("manufacturer", { response -> equalTo(robotPart.manufacturer) })
                .body("partName", { response -> equalTo(robotPart.partName) })
                .body("weight", { response -> equalTo(robotPart.weight) })
    }

    void findByWithNoneExistingSerialNumber() {
        expect:
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .get("v1/robotparts/123")
                .then()
                .statusCode(404)
    }

    void findAll_noneExisting() {
        expect:
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .get("v1/robotparts")
                .then()
                .statusCode(200)
                .body(Matchers.is("[]"))

    }

    void findAll() {
        expect:
        robotPartRepository.save(createRobotPart(123))
        robotPartRepository.save(createRobotPart(456))

        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .get("v1/robotparts")
                .then()
                .statusCode(200)
                .body(Matchers.is("[{\"serialNumber\":123,\"manufacturer\":\"manufacturer\",\"weight\":100,\"partName\":\"partName\",\"compatiblePartIds\":[]},{\"serialNumber\":456,\"manufacturer\":\"manufacturer\",\"weight\":100,\"partName\":\"partName\",\"compatiblePartIds\":[]}]"))
    }

    void create() {
        expect:
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"serialNumber\":123,\"manufacturer\":\"manufacturer\",\"weight\":100,\"partName\":\"partName\",\"compatiblePartIds\":[]}")
                .when()
                .post("v1/robotparts/")
                .then()
                .statusCode(201)
                .body("serialNumber", { response -> equalTo(123) })
                .body("manufacturer", { response -> equalTo("manufacturer") })
                .body("partName", { response -> equalTo("partName") })
                .body("weight", { response -> equalTo(100) })
    }

    void create_with_compatibel_part() {
        robotPartRepository.save(createRobotPart(456))

        expect:
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("""{
                                "serialNumber" : 123,
                                "manufacturer" : "manufacturer",
                                "weight" : 100,
                                "partName": "partName",
                                "compatiblePartIds":[456]
                            }""")
                .when()
                .post("v1/robotparts/")
                .then()
                .statusCode(201)
                .body("compatiblePartIds[0]", { response -> equalTo(456) })
    }

    void create_conflict() {
        expect:
        robotPartRepository.save(createRobotPart(123))

        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"serialNumber\":123,\"manufacturer\":\"manufacturer\",\"weight\":100,\"partName\":\"partName\",\"compatiblePartIds\":[]}")
                .when()
                .post("v1/robotparts/")
                .then()
                .statusCode(409)
    }

    void updatePartial() {
        expect:
        def robotPart = robotPartRepository.save(createRobotPart(123))
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"manufacturer\": \"xiaomi\"}")
                .when()
                .patch("v1/robotparts/123")
                .then()
                .statusCode(200)
                .body("manufacturer", { response -> equalTo("xiaomi") })
                .body("partName", { response -> equalTo(robotPart.partName) })
                .body("weight", { response -> equalTo(robotPart.weight) })
    }

    void updatePartial_addCompatibelPart() {
        expect:
        def existingRobotPart = robotPartRepository.save(createRobotPart(123))
        def compatibelRobotPart = robotPartRepository.save(createRobotPart(456))
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"compatiblePartIds\": [456]}")
                .when()
                .patch("v1/robotparts/123")
                .then()
                .statusCode(200)
                .body("compatiblePartIds[0]", { response -> equalTo(456) })
    }

    void updatePartial_addCompatibelParts() {
        expect:
        def existingRobotPart = robotPartRepository.save(createRobotPart(123))
        def compatibelRobotPart = robotPartRepository.save(createRobotPart(456))
        def compatibelRobotPart2 = robotPartRepository.save(createRobotPart(789))
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"compatiblePartIds\": [456,789]}")
                .when()
                .patch("v1/robotparts/123")
                .then()
                .statusCode(200)
                .body("compatiblePartIds[0]", { response -> equalTo(456) })
                .body("compatiblePartIds[1]", { response -> equalTo(789) })
    }
    
    void updateFull() {
        expect:
        robotPartRepository.save(createRobotPart(123))
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"manufacturer\": \"xiaomi\"}")
                .when()
                .put("v1/robotparts/123")
                .then()
                .statusCode(200)
                .body("manufacturer", { response -> equalTo("xiaomi") })
                .body("partName", { response -> IsNull.nullValue() })
                .body("weight", { response -> IsNull.nullValue() })
    }

    void updateFull_not_existing() {
        expect:
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"manufacturer\": \"xiaomi\"}")
                .when()
                .put("v1/robotparts/123")
                .then()
                .statusCode(404)
    }

    void delete() {
        expect:
        robotPartRepository.save(createRobotPart(123))
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .delete("v1/robotparts/123")
                .then()
                .statusCode(200)
    }


    void delete_not_existing() {
        expect:
        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .delete("v1/robotparts/123")
                .then()
                .statusCode(404)
    }


    void findCompatibleParts() {
        expect:
        robotPartRepository.save(createRobotPart(123))
        robotPartRepository.save(createRobotPart(456))
        robotPartRepository.save(createRobotPart(789))

        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .body("{\"compatiblePartIds\": [456,789]}")
                .when()
                .patch("v1/robotparts/123")
                .then()
                .statusCode(200)

        mockMvcRequestSpecification.contentType(ContentType.JSON)
                .get("v1/robotparts/123/compatible?limit=2")
                .then()
                .statusCode(200)
                .body("[0].serialNumber", { response -> equalTo(456) })
                .body("[1].serialNumber", { response -> equalTo(789) })
    }
}
