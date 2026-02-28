package com.example.chat.airport.departure.repository;

import com.example.chat.airport.departure.Departure;

import java.util.List;

public interface DepartureCustomRepository {

    List<Departure> findAllCustom();
}
