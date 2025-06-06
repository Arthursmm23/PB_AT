package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Historico {
    @Id
    private String id;

    @NotNull
    private LocalDateTime dataHora;

    @NotBlank
    private String acao;

    @NotBlank
    private String placa;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }

    public Historico(LocalDateTime dataHora, String acao, String placa) {
        this.dataHora = dataHora;
        this.acao = acao;
        this.placa = placa;
    }
}
