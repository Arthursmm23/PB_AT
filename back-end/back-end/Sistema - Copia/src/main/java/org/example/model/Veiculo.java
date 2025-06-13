package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Veiculo {
    @Id
    @NotBlank
    private String placa;

    @NotBlank
    private String modelo;

    @NotBlank
    private String tipo;

    @NotNull
    private Integer ano;

    @NotNull
    private Integer quilometragemAtual;

    @NotBlank
    private String usuarioEmail; // Campo para o e-mail do usuário

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Manutencao> manutencoes = new ArrayList<>();



    public Veiculo(String modelo, String placa, int kmAtual, String tipo, int ano) {
        this.modelo = modelo;
        this.placa = placa;
        this.quilometragemAtual = kmAtual;
        this.tipo = tipo;
        this.ano = ano;
    }

    public void adicionarManutencao(Manutencao m) {
        manutencoes.add(m);
        m.setVeiculo(this);
    }

    public float calcularCustoTotalManutencoes() {
        return manutencoes.stream().map(Manutencao::getCusto).reduce(0f, Float::sum);
    }
}
