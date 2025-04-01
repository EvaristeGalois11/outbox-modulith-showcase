package org.example.showcase.repository;

import org.example.showcase.entity.Bar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BarRepository extends JpaRepository<Bar, UUID> {
}
