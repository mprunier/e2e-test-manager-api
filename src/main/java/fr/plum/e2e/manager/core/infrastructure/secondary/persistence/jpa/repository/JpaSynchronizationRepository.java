package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.repository;

import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.synchronization.JpaSynchronizationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class JpaSynchronizationRepository
    implements PanacheRepositoryBase<JpaSynchronizationEntity, UUID> {}
