package com.foo.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.foo.api.model.OfficeDto;
import com.foo.service.OfficeDao;
import com.foo.service.OfficeDaoNotFoundException;
import com.foo.test.util.TestConfig;
import com.foo.test.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfficeController.class)
@Import(TestConfig.class)
class OfficeControllerTest {

    @MockBean
    OfficeDao dao;

    @Autowired
    MockMvc mockMvc;


    @Captor
    ArgumentCaptor<OfficeDto> dtoCaptor;

    @Autowired
    TestUtil testUtil;

    @Test
    @DisplayName("Test POST /offices - happy path")
    void create() throws Exception {
        var payloadJson = testUtil.getResourceAsString("com/foo/api/OfficeControllerTest/common/test-input.json");
        mockMvc.perform(post("/offices").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(payloadJson)
                )
                .andExpect(status().isOk());

        var payloadObject = testUtil.unmarshallJsonString(payloadJson, new TypeReference<OfficeDto>() {
        });
        verify(dao,times(1)).create(dtoCaptor.capture());
        assertThat(payloadObject).isEqualTo(dtoCaptor.getValue());
    }


    @Test
    @DisplayName("Test  GET /offices/code/{code} - happy path")
    void getByICode() throws Exception {
        var expectedObject = testUtil.unmarshall("com/foo/api/OfficeControllerTest/common/test-input.json", new TypeReference<OfficeDto>() {
                });
        when(dao.findByCode(any())).thenReturn(expectedObject);

        var mvcResult = mockMvc.perform(get("/offices/code/{code}",1L )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var actualResponseBody = mvcResult.getResponse().getContentAsString();
        var expectedResponseBody = testUtil.getResourceAsString("com/foo/api/OfficeControllerTest/common/test-input.json");
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);

        // Verify Invocation to Service Layer
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(dao,times(1)).findByCode(idCaptor.capture());
        assertThat(1L).isEqualTo(idCaptor.getValue());
    }

    @Test
    @DisplayName("Test  GET /offices/code/{code} - Not Found Error")
    void getByICodeNotFoundError() throws Exception {

        when(dao.findByCode(any())).thenThrow(new OfficeDaoNotFoundException("Failed to find Office, code does not exists:10"));

        mockMvc.perform(get("/offices/code/{code}",10L )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.status", is(Integer.valueOf(404))),
                        jsonPath("$.message", is("Failed to find Office, code does not exists:10")),
                        jsonPath("$.path", is("/offices/code/10")),
                        jsonPath("$.path", startsWith("/offices/code/10")),
                        jsonPath("$.method", is("GET"))
                        );

        // Verify Invocation to Service Layer
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(dao,times(1)).findByCode(idCaptor.capture());
        assertThat(10L).isEqualTo(idCaptor.getValue());
    }

    @Test
    @DisplayName("Test  GET /offices/code/{code} -Invalid Parameter Error")
    void getByICodeInvalidParameterError() throws Exception {

        mockMvc.perform(get("/offices/code/{code}",-20L )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.status", is(Integer.valueOf(400))),
                        jsonPath("$.message", is("Invalid URL parameters")),
                        jsonPath("$.path", startsWith("/offices/code/")),
                        jsonPath("$.method", is("GET")),
                        jsonPath("$.errors[0].field", is("code")),
                        jsonPath("$.errors[0].message", is("must be greater than 0"))
                );

        // Verify NO Invocation to Service Layer
        verify(dao,times(0)).findByCode(any());
    }

    @Test
    @DisplayName("Test  GET /offices/code/{code} -Invalid Type of Parameter Error")
    void getByICodeInvalidParameterTypeError() throws Exception {

        mockMvc.perform(get("/offices/code/{code}","unexpected")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.status", is(Integer.valueOf(400))),
                        jsonPath("$.message", startsWith("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'")),
                        jsonPath("$.path", startsWith("/offices/code/")),
                        jsonPath("$.method", is("GET"))
                );

        // Verify NO Invocation to Service Layer
        verify(dao,times(0)).findByCode(any());
    }

    @Test
    @DisplayName("Test GET /offices/all - happy path")
    void getAll() throws Exception {

        var dataList = testUtil.unmarshall("/com/foo/api/OfficeControllerTest/getAll/test-data.json", new TypeReference<List<OfficeDto>>() {
        });

        when(dao.findAll()).thenReturn(dataList);
        var mvcResult = mockMvc.perform(get("/offices/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.length()", is(2)))
                .andReturn();

        var actualResponseBody = mvcResult.getResponse().getContentAsString();
        var expectedResponseBody = testUtil.getResourceAsString("com/foo/api/OfficeControllerTest/getAll/exprected-output.json");
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(expectedResponseBody);
    }

    @Test
    @DisplayName("Test  PUT /offices - Not Supported Method Error")
    void unsupportedMethodError() throws Exception {

        mockMvc.perform(put("/offices")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isMethodNotAllowed(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.status", is(Integer.valueOf(405))),
                        jsonPath("$.message", is("Request method 'PUT' is not supported")),
                        jsonPath("$.path", is("/offices")),
                        jsonPath("$.method", is("PUT"))
                );

    }



}