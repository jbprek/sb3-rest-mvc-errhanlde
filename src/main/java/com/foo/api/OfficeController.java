package com.foo.api;

import com.foo.api.model.OfficeDto;
import com.foo.service.OfficeDao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping(value = "/offices", produces = MediaType.APPLICATION_JSON_VALUE
//       , produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class OfficeController {


    private final OfficeDao dao;


    @PostMapping
    public void create(@RequestBody @NotNull @Valid OfficeDto dto){
        dao.create(dto);
    }

    @GetMapping("/code/{code}")
    public OfficeDto getByICode(@PathVariable @NotNull  @Positive String code) {
        return dao.findByCode(Integer.valueOf(code));
    }

    @GetMapping("/query")
    public OfficeDto getByCity( @RequestParam @NotBlank @Positive String code, @RequestParam @NotBlank String city) {
        return dao.query(Integer.valueOf(code),city);
    }

    @GetMapping("/all")
    public List<OfficeDto> getAll() {
        return dao.findAll();
    }

}
