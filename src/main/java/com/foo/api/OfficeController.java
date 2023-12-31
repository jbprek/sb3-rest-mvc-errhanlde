package com.foo.api;

import com.foo.api.model.OfficeDto;
import com.foo.service.OfficeDao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/offices", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OfficeController {


    private final OfficeDao dao;


    @PostMapping
    public void create(@RequestBody @NotNull @Valid OfficeDto dto) {
        dao.create(dto);
    }

    @GetMapping("/code/{code}")
    public OfficeDto getByICode(@PathVariable @NotNull @Positive String code) {
        return dao.findByCode(Integer.valueOf(code));
    }

    @GetMapping("/all")
    public List<OfficeDto> getAll() {
        return dao.findAll();
    }

}
