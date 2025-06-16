package org.example.service;

import org.example.model.Notificacao;
import org.example.model.EmailUtil;
import org.example.repository.NotificacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {
    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private EmailUtil emailUtil;

    public void enviarNotificacao(Notificacao notificacao) {
        logger.info("Enviando notificação para: {}", notificacao.getDestinatario());
        emailUtil.enviarEmail(notificacao.getDestinatario(), "Notificação: " + notificacao.getTipo(), notificacao.getMensagem());
        notificacao.setEnviada(true);
        notificacaoRepository.save(notificacao);
    }
}