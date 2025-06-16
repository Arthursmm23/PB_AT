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
public class    VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final EmailService emailService;

    private final String EMAIL_MECANICO = "cguilherme052@gmail.com";

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository, EmailService emailService) {
        this.veiculoRepository = veiculoRepository;
        this.emailService = emailService;
    }

    public Page<Veiculo> listarTodos(Pageable pageable) {
        return veiculoRepository.findAll(pageable);
    }

    public Veiculo salvar(Veiculo veiculo) {
        boolean isNovo = !veiculoRepository.existsById(veiculo.getPlaca());
        Veiculo salvo = veiculoRepository.save(veiculo);

        if (isNovo) {
            String emailUsuario = salvo.getUsuarioEmail();

            
            emailService.enviarEmailSimples(
                    emailUsuario,
                    "Cadastro de veículo",
                    "Seu veículo foi registrado no sistema."
            );

            
            emailService.enviarEmailSimples(
                    EMAIL_MECANICO,
                    "Novo veículo registrado",
                    "Um novo veículo foi cadastrado:\n\n" +
                            "Placa: " + salvo.getPlaca() + "\n" +
                            "Modelo: " + salvo.getModelo() + "\n" +
                            "Tipo: " + salvo.getTipo() + "\n" +
                            "Ano: " + salvo.getAno() + "\n" +
                            "Quilometragem Atual: " + salvo.getQuilometragemAtual() + "\n" +
                            "Email do usuário: " + emailUsuario
            );
        }

        return salvo;
    }

    public Veiculo atualizar(String placa, Veiculo veiculoAtualizado) {
        Optional<Veiculo> existente = veiculoRepository.findById(placa);
        if (existente.isEmpty()) throw new RuntimeException("Veículo não encontrado");

        Veiculo veiculo = existente.get();
        veiculo.setModelo(veiculoAtualizado.getModelo());
        veiculo.setTipo(veiculoAtualizado.getTipo());
        veiculo.setAno(veiculoAtualizado.getAno());
        veiculo.setQuilometragemAtual(veiculoAtualizado.getQuilometragemAtual());
        veiculo.setUsuarioEmail(veiculoAtualizado.getUsuarioEmail());

        return veiculoRepository.save(veiculo);
    }

    public void excluir(String placa) {
        if (!veiculoRepository.existsById(placa)) {
            throw new RuntimeException("Veículo não encontrado");
        }
        veiculoRepository.deleteById(placa);
    }

    public Veiculo adicionarManutencao(String placa, Manutencao manutencao) {
        Veiculo veiculo = veiculoRepository.findById(placa)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

      
        veiculo.adicionarManutencao(manutencao);

       
        if (manutencao.getQuilometragem() > veiculo.getQuilometragemAtual()) {
            veiculo.setQuilometragemAtual(manutencao.getQuilometragem());
        }

      
        Veiculo salvo = veiculoRepository.save(veiculo);

       
        emailService.enviarEmailSimples(
                salvo.getUsuarioEmail(),
                "Aviso importante",
                "Seu carro pode estar em apuros, entre em contato."
        );

       
        verificarAlertasDeQuilometragem(salvo);

        return salvo;
    }


    private void verificarAlertasDeQuilometragem(Veiculo veiculo) {
        int kmAtual = veiculo.getQuilometragemAtual();

        int kmUltimaManutencao = veiculo.getManutencoes().stream()
                .mapToInt(Manutencao::getQuilometragem)
                .max()
                .orElse(kmAtual);

        int diferenca = kmAtual - kmUltimaManutencao;

        String placa = veiculo.getPlaca();
        String modelo = veiculo.getModelo();
        String tipo = veiculo.getTipo();
        int ano = veiculo.getAno();

        String baseMsg = "Veículo:\n" +
                "Placa: " + placa + "\n" +
                "Modelo: " + modelo + "\n" +
                "Tipo: " + tipo + "\n" +
                "Ano: " + ano + "\n" +
                "Quilometragem atual: " + kmAtual + " km\n" +
                "Última manutenção: " + kmUltimaManutencao + " km\n" +
                "Rodados desde a última manutenção: " + diferenca + " km\n\n";

        if (diferenca >= 5000) {
            emailService.enviarEmailSimples(
                    EMAIL_MECANICO,
                    "Troca de óleo recomendada",
                    baseMsg + "Alerta: Troca de óleo recomendada."
            );
        }

        if (diferenca >= 15000) {
            emailService.enviarEmailSimples(
                    EMAIL_MECANICO,
                    "Alinhamento recomendado",
                    baseMsg + "Alerta: Alinhamento recomendado."
            );
        }

        if (diferenca >= 25000) {
            emailService.enviarEmailSimples(
                    EMAIL_MECANICO,
                    "Revisão recomendada",
                    baseMsg + "Alerta: Revisão geral recomendada."
            );
        }
    }
}
