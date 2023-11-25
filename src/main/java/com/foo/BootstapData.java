package com.foo;

import com.foo.api.model.OfficeDto;
import com.foo.service.OfficeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BootstapData implements CommandLineRunner {

    private final OfficeDao
            dao;

    @Override
    public void run(String... args) throws Exception {

        var cities = List.of(
                new OfficeDto(1, "Athens", "GR"),
                new OfficeDto(2, "DUBLIN", "IE"),
                new OfficeDto(3, "PARIS", "FR"),
                new OfficeDto(4, "ROME", "IT"),
                new OfficeDto(5, "LONDON", "UK"));

        dao.bulkCreate(cities);
    }
}
