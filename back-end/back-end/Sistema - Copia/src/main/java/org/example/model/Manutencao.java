package org.example.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Manutencao {
    @Id
    private String id;

    @NotBlank
    private String tipo;

    @NotNull
    private LocalDate data;

    @Positive
    private int quilometragem;

    @Positive
    private float custo;

    @ManyToOne
    @JoinColumn(name = "veiculo_placa")
    @JsonBackReference
    private Veiculo veiculo;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public Manutencao(String tipo, LocalDate data, int quilometragem, float custo) {
        this.tipo = tipo;
        this.data = data;
        this.quilometragem = quilometragem;
        this.custo = custo;
    }
}
