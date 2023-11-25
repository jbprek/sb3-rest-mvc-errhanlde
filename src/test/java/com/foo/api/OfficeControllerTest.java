package com.foo.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.foo.TestConfig;
import com.foo.TestUtil;
import com.foo.api.model.OfficeDto;
import com.foo.service.OfficeDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfficeController.class)
@Import(TestConfig.class)
class OfficeControllerTest {

    @MockBean
    OfficeDao dao;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TestUtil testUtil;

    @Test
    @DisplayName("Test  POST /offices - happy path")
    void create() throws Exception {

        mockMvc.perform(get("/offices/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(testUtil.getResourceAsString("com/foo/api/OfficeControllerTest/common/test-input.json"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void getByICode() {
    }

    @Test
    @DisplayName("Test  GET /offices/all - happy path")
    void getAll() throws Exception {

        var dataList = testUtil.unmarshall("/com/foo/api/OfficeControllerTest/getAll/test-data.json", new TypeReference<List<OfficeDto>>() {
        });

        Mockito.when(dao.findAll()).thenReturn(dataList);
        MvcResult mvcResult = mockMvc.perform(get("/offices/all")
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


}