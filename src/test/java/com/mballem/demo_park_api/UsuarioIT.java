package com.mballem.demo_park_api;

import com.mballem.demo_park_api.web.dto.UsuarioCreateDto;
import com.mballem.demo_park_api.web.dto.UsuarioResponseDto;
import com.mballem.demo_park_api.web.dto.UsuarioSenhaDto;
import com.mballem.demo_park_api.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @LocalServerPort
    int port;

    WebTestClient testClient;

    @BeforeEach
    void setUp(){
        this.testClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    public void createUsuario_ComUsernameEPasswordValidos_RetornarUsuarioCriadoComStatus201(){
        UsuarioResponseDto responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@gmail.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isNotNull();
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("ben@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUsuario_ComUsernameInvalido_RetornarErrorMessageStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@email", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComPasswordInvalido_RetornarErrorMessageStatus422(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@gmail.com", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@gmail.com", "1234566"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ben@gmail.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

    }

    @Test
    public void createUsuario_ComUsernameRepetido_RetornarErrorMessageComStatus409(){
        ErrorMessage responseBody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(409);
    }

    @Test
    public void buscarUsuario_ComIdExistente_RetornarUsuarioComStatus200(){
        UsuarioResponseDto responseBody = testClient.get()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(100);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("ana@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("ADMIN");

        responseBody = testClient.get()
                .uri("api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("jonas@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");

        responseBody = testClient.get()
                .uri("api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jonas@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getId()).isEqualTo(101);
        Assertions.assertThat(responseBody.getUsername()).isEqualTo("jonas@gmail.com");
        Assertions.assertThat(responseBody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void buscarUsuario_ComIdInexistente_RetornarErrorMessageComStatus404(){
        ErrorMessage responseBody = testClient.get()
                .uri("api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarUsuario_ComUsuarioClienteBuscandoOutroCliente_RetornarErrorMessageComStatus403(){
        ErrorMessage responseBody = testClient.get()
                .uri("api/v1/usuarios/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jonas@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editarSenha_ComDadosValidos_RetornarStatus204(){
        testClient.patch()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "101010", "101010"))
                .exchange()
                .expectStatus().isNoContent();

        testClient.patch()
                .uri("api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jonas@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "101010", "101010"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void editarSenha_ComSenhasInvalidas_RetornarErrorMessageComStatus400(){
        ErrorMessage responseBody = testClient.patch()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123455"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);

        responseBody = testClient.patch()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123457", "123456", "123456"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(400);
    }

    @Test
    public void editarSenha_ComUsuarioSemPermissao_RetornarErrorMessageComStatus403(){
        ErrorMessage responseBody = testClient.patch()
                .uri("api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);

        responseBody = testClient.patch()
                .uri("api/v1/usuarios/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jonas@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }

    @Test
    public void editarSenha_ComCamposInvalidos_RetornarErrorMessageStatus422(){
        ErrorMessage responseBody = testClient.patch()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.patch()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("12345", "12345", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);

        responseBody = testClient.patch()
                .uri("api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("1234567", "1234567", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(422);
    }

    @Test
    public void listarUsuarios_SemQualquerParametro_RetornarListaDeUsuariosComStatus200(){
        List<UsuarioResponseDto> responseBody = testClient.get()
                .uri("api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody).hasSize(3);
    }

    @Test public void listarUsuarios_ComUsuarioSemPermissao_RetornarErrorMessageComStatus403(){
        ErrorMessage responseBody = testClient.get()
                .uri("api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "jonas@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responseBody).isNotNull();
        Assertions.assertThat(responseBody.getStatus()).isEqualTo(403);
    }
}
