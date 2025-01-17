package com.andreozzi.repos;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.andreozzi.entities.Card;

import jakarta.persistence.Tuple;

public interface CardDAO extends JpaRepository<Card, Integer> {
    Optional<Card> findByName(String name);

    @Query("SELECT c.name, c.blueprintID FROM Card c WHERE c.name LIKE :query%")
    List<Tuple> findSuggestions(@Param("query") String query);

}
