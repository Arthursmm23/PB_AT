package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Alerta {
    @Id
    private String id;

    @NotBlank
    private String tipo;

    @NotBlank
    private String status;

    private String mensagem;

    public Alerta(String id, String tipo, String status, String mensagem) {
        this.id = id;
        this.tipo = tipo;
        this.status = status;
        this.mensagem = mensagem;
    }
}