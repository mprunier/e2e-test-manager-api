package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.repository;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.schdulerconfiguration.JpaSchedulerConfigurationEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class JpaSchedulerConfigurationRepository
    implements PanacheRepositoryBase<JpaSchedulerConfigurationEntity, UUID> {}
