package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestScreenshotEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestScreenshotRepository
    implements PanacheRepositoryBase<JpaTestScreenshotEntity, Long> {}
