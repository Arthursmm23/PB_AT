package org.example.service;

import org.example.model.Manutencao;
import org.example.model.Veiculo;
import org.example.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public Page<Veiculo> listarTodos(Pageable pageable) {
        return veiculoRepository.findAll(pageable);
    }

    public Veiculo salvar(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Veiculo atualizar(String placa, Veiculo veiculoAtualizado) {
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(placa);
        if (veiculoOptional.isEmpty()) {
            throw new RuntimeException("Veículo não encontrado");
        }

        Veiculo veiculoExistente = veiculoOptional.get();
        veiculoExistente.setModelo(veiculoAtualizado.getModelo());
        veiculoExistente.setTipo(veiculoAtualizado.getTipo());
        veiculoExistente.setAno(veiculoAtualizado.getAno());
        veiculoExistente.setQuilometragemAtual(veiculoAtualizado.getQuilometragemAtual());

        return veiculoRepository.save(veiculoExistente);
    }

    public void excluir(String placa) {
        veiculoRepository.deleteById(placa);
    }

    public Veiculo adicionarManutencao(String placa, Manutencao manutencao) {
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(placa);
        if (veiculoOptional.isEmpty()) {
            throw new RuntimeException("Veículo não encontrado");
        }

        Veiculo veiculo = veiculoOptional.get();
        veiculo.adicionarManutencao(manutencao);

        return veiculoRepository.save(veiculo);
    }
}
