package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Notificacao {
    @Id
    private String id;

    @NotBlank
    private String tipo;

    private boolean enviada;

    @NotBlank
    private String destinatario;

    private String mensagem;

    public Notificacao(String id, String tipo, String destinatario, String mensagem) {
        this.id = id;
        this.tipo = tipo;
        this.destinatario = destinatario;
        this.mensagem = mensagem;
        this.enviada = false;
    }
}