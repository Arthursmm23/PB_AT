package org.example.model;

public class Mecanico extends Usuario {
    private String especialidade;
    private int cargaHoraria;

    public void realizarManutencao() {
        System.out.println("Manutenção realizada.");
    }
}
