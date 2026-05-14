package com.arquitectura.dominio.repositorios;

import com.arquitectura.dominio.modelo.MensajeModel;
import com.arquitectura.infraestructura.persistencia.HibernateManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDateTime;
import java.util.List;

public class JpaMensajeRepository implements MensajeRepository {

    @Override
    public void guardar(String mensajeId, String autor, String ipRemitente, String contenido,
                        String hashSha256, String contenidoCifrado, LocalDateTime fechaEnvio,
                        String servidorOrigen) {
        MensajeModel entity = new MensajeModel();
        entity.setId(mensajeId);
        entity.setAutor(autor);
        entity.setIpRemitente(ipRemitente);
        entity.setContenido(contenido);
        entity.setHashSha256(hashSha256);
        entity.setContenidoCifrado(contenidoCifrado);
        entity.setFechaEnvio(fechaEnvio);
        entity.setServidorOrigen(servidorOrigen);

        EntityManager entityManager = HibernateManager.crearEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();
            entityManager.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new IllegalStateException("No fue posible guardar el mensaje en MySQL", e);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean existePorId(String id) {
        EntityManager em = HibernateManager.crearEntityManager();
        try {
            return em.find(MensajeModel.class, id) != null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<MensajeModel> listarTodos() {
        EntityManager em = HibernateManager.crearEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM MensajeModel m ORDER BY m.fechaEnvio DESC",
                MensajeModel.class
            ).getResultList();
        } finally {
            em.close();
        }
    }
}
