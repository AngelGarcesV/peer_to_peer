package com.arquitectura.dominio.handlers;

import com.arquitectura.aplicacion.router.Handler;
import com.arquitectura.aplicacion.sesion.GestorServidoresPeer;
import com.arquitectura.mensajeria.Mensaje;
import com.arquitectura.mensajeria.Metadata;
import com.arquitectura.mensajeria.Respuesta;
import com.arquitectura.mensajeria.enums.Accion;
import com.arquitectura.mensajeria.enums.Estado;
import com.arquitectura.mensajeria.enums.TipoMensaje;
import com.arquitectura.mensajeria.payload.PayloadListarLogsRemoto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Redirige una solicitud LISTAR_LOGS a un servidor remoto via GestorServidoresPeer
 * y retorna la respuesta del peer al cliente local.
 */
public class ListarLogsRemotoHandler implements Handler<PayloadListarLogsRemoto> {

    private static final Logger LOGGER = Logger.getLogger(ListarLogsRemotoHandler.class.getName());

    @Override
    public Respuesta<?> handle(Mensaje<PayloadListarLogsRemoto> mensaje) {
        PayloadListarLogsRemoto payload = mensaje.getPayload();
        if (payload == null || payload.getServidorId() == null || payload.getServidorId().isBlank()) {
            Respuesta<?> error = new Respuesta<>();
            error.setEstado(Estado.ERROR);
            return error;
        }

        String servidorId = payload.getServidorId();
        int pagina = payload.getPagina();
        int tamanoPagina = payload.getTamanoPagina() > 0 ? payload.getTamanoPagina() : 50;

        // Construir el mensaje LISTAR_LOGS a reenviar al peer
        Map<String, Object> logsPayload = new HashMap<>();
        logsPayload.put("pagina", pagina);
        logsPayload.put("tamanoPagina", tamanoPagina);

        Mensaje<Map<String, Object>> mensajePeer = new Mensaje<>();
        mensajePeer.setTipo(TipoMensaje.REQUEST);
        mensajePeer.setAccion(Accion.LISTAR_LOGS);
        mensajePeer.setMetadata(crearMetadata());
        mensajePeer.setPayload(logsPayload);

        LOGGER.info(() -> "Reenviando solicitud de logs al peer " + servidorId
                + " | pagina=" + pagina + " | tamanoPagina=" + tamanoPagina);

        String respuestaPeer = GestorServidoresPeer.getInstance().enviarAPeerYEsperar(servidorId, mensajePeer);

        if (respuestaPeer == null) {
            LOGGER.warning(() -> "No se pudo contactar al peer " + servidorId + " para obtener logs");
            Respuesta<?> error = new Respuesta<>();
            error.setEstado(Estado.ERROR);
            return error;
        }

        // Retornar la respuesta del peer directamente al cliente como payload raw.
        // El cliente puede deserializarla usando la misma estructura que LISTAR_LOGS.
        Mensaje<String> mensajeRespuesta = new Mensaje<>();
        mensajeRespuesta.setTipo(TipoMensaje.RESPONSE);
        mensajeRespuesta.setAccion(Accion.LISTAR_LOGS_REMOTO);
        mensajeRespuesta.setMetadata(crearMetadata());
        mensajeRespuesta.setPayload(respuestaPeer);

        Respuesta<String> respuesta = new Respuesta<>();
        respuesta.setEstado(Estado.EXITO);
        respuesta.setMensaje(mensajeRespuesta);
        return respuesta;
    }

    @Override
    public Class<PayloadListarLogsRemoto> getPayloadClass() {
        return PayloadListarLogsRemoto.class;
    }

    private Metadata crearMetadata() {
        Metadata metadata = new Metadata();
        metadata.setIdMensaje(UUID.randomUUID().toString());
        metadata.setTimestamp(LocalDateTime.now());
        return metadata;
    }
}
