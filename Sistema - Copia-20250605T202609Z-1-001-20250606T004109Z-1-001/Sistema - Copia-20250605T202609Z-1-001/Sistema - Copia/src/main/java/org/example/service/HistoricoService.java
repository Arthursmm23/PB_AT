package org.example.service;

import org.example.model.Historico;
import org.example.repository.HistoricoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoricoService {
    private static final Logger logger = LoggerFactory.getLogger(HistoricoService.class);

    @Autowired
    private HistoricoRepository historicoRepository;

    public List<Historico> consultar(LocalDate dataInicial, LocalDate dataFinal, String tipoConsulta) {
        logger.info("Consultando histórico de {} a {} com tipo: {}", dataInicial, dataFinal, tipoConsulta);
        LocalDateTime start = dataInicial.atStartOfDay();
        LocalDateTime end = dataFinal.atTime(23, 59, 59);
        return historicoRepository.findByDataHoraBetweenAndAcaoContainingIgnoreCase(start, end, tipoConsulta != null ? tipoConsulta : "");
    }

    public void registrar(String placa, String acao) {
        logger.info("Registrando ação no histórico: {} para placa {}", acao, placa);
        Historico historico = new Historico(LocalDateTime.now(), acao, placa);
        historicoRepository.save(historico);
    }
}