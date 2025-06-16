package org.example.repository;

import org.example.model.Historico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoRepository extends JpaRepository<Historico, String> {
    List<Historico> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);
}
