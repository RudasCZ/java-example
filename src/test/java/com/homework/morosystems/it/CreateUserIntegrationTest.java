package com.homework.morosystems.it;


import com.homework.morosystems.model.UserCreateUpdateDto;
import com.homework.morosystems.model.UserGetDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateUserIntegrationTest extends PostgresSQLTestContainerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void createUser_shouldReturns201andPersists() {
        // Arrange
        UserCreateUpdateDto request = new UserCreateUpdateDto()
                .name("Alice")
                .username("alice123")
                .password("secret12345");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserCreateUpdateDto> httpEntity = new HttpEntity<>(request, headers);

        String baseUrl = "http://localhost:%s/users".formatted(port);

        // Act
        ResponseEntity<UserGetDto> postResp =
                rest.postForEntity(baseUrl, httpEntity, UserGetDto.class);

        // Assert
        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserGetDto body = postResp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getUsername()).isEqualTo("alice123");

        ResponseEntity<UserGetDto> getResp =
                rest.getForEntity(baseUrl + "/" + body.getId(), UserGetDto.class);

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody())
                .extracting(UserGetDto::getId, UserGetDto::getUsername)
                .containsExactly(body.getId(), "alice123");
    }
}
