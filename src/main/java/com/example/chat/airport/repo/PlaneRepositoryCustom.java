package com.example.chat.airport.repo;

import com.example.chat.airport.entity.Plane;
import lombok.RequiredArgsConstructor;

public interface PlaneRepositoryCustom {

    long deleteByScheduleDateStartsWith(String today);

}
