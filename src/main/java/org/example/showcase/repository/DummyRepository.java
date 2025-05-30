package org.example.showcase.repository;

import org.example.showcase.entity.Dummy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DummyRepository extends JpaRepository<Dummy, UUID> {
}
