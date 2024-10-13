package fr.njj.galaxion.endtoendtesting.model.repository;

import fr.njj.galaxion.endtoendtesting.model.entity.ParallelPipelineProgressEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ParallelPipelineProgressRepository
    implements PanacheRepositoryBase<ParallelPipelineProgressEntity, Long> {}
