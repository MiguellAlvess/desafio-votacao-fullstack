package br.com.miguelalves.voting.associate.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.miguelalves.voting.associate.dto.AssociateResponse;
import br.com.miguelalves.voting.associate.dto.CreateAssociateRequest;
import br.com.miguelalves.voting.associate.service.AssociateService;
import br.com.miguelalves.voting.core.exceptions.CpfAlreadyExistsException;
import br.com.miguelalves.voting.core.exceptions.GlobalExceptionHandler;

@WebMvcTest(AssociateController.class)
@Import(GlobalExceptionHandler.class)
class AssociateControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AssociateService associateService;

        @Test
        void shouldCreateAssociate() throws Exception {
                var response = new AssociateResponse(
                                1L,
                                "12345678901");

                when(associateService.create(any()))
                                .thenReturn(response);

                mockMvc.perform(
                                post("/api/v1/associates")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "cpf": "12345678901"
                                                                }
                                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.cpf")
                                                .value("12345678901"));
        }

        @Test
        void shouldReturnConflictWhenCpfAlreadyExists()
                        throws Exception {

                when(associateService.create(any()))
                                .thenThrow(
                                                new CpfAlreadyExistsException(
                                                                "12345678901"));

                mockMvc.perform(
                                post("/api/v1/associates")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "cpf": "12345678901"
                                                                }
                                                                """))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409))
                                .andExpect(jsonPath("$.error")
                                                .value("Conflict"))
                                .andExpect(jsonPath("$.message")
                                                .value(
                                                                "An associate with CPF 12345678901 already exists"))
                                .andExpect(jsonPath("$.path")
                                                .value("/api/v1/associates"));
        }

        @Test
        void shouldReturnBadRequestWhenCpfIsBlank()
                        throws Exception {

                mockMvc.perform(
                                post("/api/v1/associates")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                  "cpf": ""
                                                                }
                                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error")
                                                .value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("cpf: CPF cannot be blank"))
                                .andExpect(jsonPath("$.path")
                                                .value("/api/v1/associates"))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldIdentifyExistingAssociate() throws Exception {
                var response = new AssociateResponse(1L, "12345678909");
                when(associateService.identify(new CreateAssociateRequest("12345678909")))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/associates/identify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {"cpf": "12345678909"}
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.cpf").value("12345678909"));
                verify(associateService).identify(new CreateAssociateRequest("12345678909"));
        }

        @Test
        void shouldIdentifyNewAssociate() throws Exception {
                var response = new AssociateResponse(2L, "52998224725");
                when(associateService.identify(new CreateAssociateRequest("52998224725")))
                                .thenReturn(response);

                mockMvc.perform(post("/api/v1/associates/identify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {"cpf": "52998224725"}
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(2))
                                .andExpect(jsonPath("$.cpf").value("52998224725"));
        }

        @Test
        void shouldReturnBadRequestWhenIdentificationCpfIsBlank() throws Exception {
                mockMvc.perform(post("/api/v1/associates/identify")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {"cpf": ""}
                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("cpf: CPF cannot be blank"))
                                .andExpect(jsonPath("$.path")
                                                .value("/api/v1/associates/identify"))
                                .andExpect(jsonPath("$.timestamp").exists());
                verifyNoInteractions(associateService);
        }
}
