package org.example.service;

import org.example.model.Historico;
import org.example.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    public List<Historico> consultar(LocalDate dataInicial, LocalDate dataFinal) {
        LocalDateTime start = dataInicial.atStartOfDay();
        LocalDateTime end = dataFinal.plusDays(1).atStartOfDay();
        return historicoRepository.findByDataHoraBetween(start, end.minusNanos(1));
    }

    public void registrar(String placa, String acao) {
        Historico historico = new Historico(LocalDateTime.now(), acao, placa);
        historicoRepository.save(historico);
    }
}
