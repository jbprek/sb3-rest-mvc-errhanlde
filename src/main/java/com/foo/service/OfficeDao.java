package com.foo.service;


import com.foo.api.model.OfficeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfficeDao {

    private final Map<Integer, OfficeDto> storage = new ConcurrentHashMap<>();


    public  void create(OfficeDto dto) {
        if (storage.containsKey(dto.code()))
            throw new OfficeDaoCreateValidationException("Failed to create Office, code already exists:"+dto.code());

        if (dto.code() == 9999)
            throw new RuntimeException("Failed to create Office, Unexpected Error");

        storage.put(dto.code(), dto);
    }

    public  void bulkCreate(List<OfficeDto> dtList) {
        dtList.forEach(this::create);
    }

    public synchronized OfficeDto findByCode(Integer code) {
        return Optional.ofNullable( storage.get(code))
                .orElseThrow(()->
                        new OfficeDaoNotFoundException("Failed to find Office, code does not exists:"+code));
    }


    public  OfficeDto query(Integer code, String city) {
        var ent =  Optional.ofNullable( storage.get(code))
                .orElseThrow(()->
                        new OfficeDaoNotFoundException("Failed to find Office, code does not exists:"+code));
        if ( !ent.code().equals(city))
            throw new OfficeDaoNotFoundException("Failed to find Office, city does not exists:" + city);
        return ent;
    }

    public  List<OfficeDto> findAll() {
        return new ArrayList<>(storage.values());
    }
}
