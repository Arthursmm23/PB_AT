package org.example.repository;

import org.example.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, String> {
    List<Veiculo> findByTipo(String tipo);
}