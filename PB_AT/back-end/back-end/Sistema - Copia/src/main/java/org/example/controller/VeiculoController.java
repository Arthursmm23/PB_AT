package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.example.model.Manutencao;
import org.example.model.Veiculo;
import org.example.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/veiculos")
@CrossOrigin(origins = "*")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Lista todos os veículos", description = "Retorna uma lista paginada de veículos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<Veiculo>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(veiculoService.listarTodos(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria um novo veículo", description = "Salva um veículo no banco de dados")
    @ApiResponse(responseCode = "200", description = "Veículo criado com sucesso")
    public ResponseEntity<Veiculo> salvar(@Valid @RequestBody Veiculo v) {
        return ResponseEntity.ok(veiculoService.salvar(v));
    }

    @PutMapping("/{placa}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualiza um veículo", description = "Atualiza os dados de um veículo pela placa")
    @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso")
    public ResponseEntity<Veiculo> atualizar(@PathVariable String placa, @Valid @RequestBody Veiculo v) {
        return ResponseEntity.ok(veiculoService.atualizar(placa, v));
    }

    @DeleteMapping("/{placa}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exclui um veículo", description = "Remove um veículo pelo ID")
    @ApiResponse(responseCode = "204", description = "Veículo excluído com sucesso")
    public ResponseEntity<Void> excluir(@PathVariable String placa) {
        veiculoService.excluir(placa);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{placa}/manutencoes")
    @PreAuthorize("hasAnyRole('ADMIN', 'MECANICO')")
    @Operation(summary = "Adiciona uma manutenção", description = "Adiciona uma manutenção a um veículo")
    @ApiResponse(responseCode = "200", description = "Manutenção adicionada com sucesso")
    public ResponseEntity<Veiculo> adicionarManutencao(@PathVariable String placa, @Valid @RequestBody Manutencao m) {

        m.setVeiculo(null);
        return ResponseEntity.ok(veiculoService.adicionarManutencao(placa, m));
    }
}
