package com.example.chat.airport.Departure.repository;

import com.example.chat.airport.Departure.Departure;

import java.util.List;

public interface DepartureCustomRepository {

    List<Departure> findAllCustom();
}
