package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.model.Historico;
import org.example.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/historico")
@CrossOrigin(origins = "*")
public class HistoricoController {
    @Autowired
    private HistoricoService historicoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Consulta o histórico de manutenções", description = "Retorna uma lista de registros de histórico")
    @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso")
    public ResponseEntity<List<Historico>> consultar(
            @RequestParam String dataInicial,
            @RequestParam String dataFinal,
            @RequestParam(required = false) String tipoConsulta) {
        LocalDate ini = LocalDate.parse(dataInicial);
        LocalDate fim = LocalDate.parse(dataFinal);
        return ResponseEntity.ok(historicoService.consultar(ini, fim, tipoConsulta));
    }
}