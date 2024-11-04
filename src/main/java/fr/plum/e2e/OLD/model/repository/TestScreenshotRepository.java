package fr.plum.e2e.OLD.model.repository;

import fr.plum.e2e.OLD.model.entity.TestScreenshotEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TestScreenshotRepository
    implements PanacheRepositoryBase<TestScreenshotEntity, Long> {}
