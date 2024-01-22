package fr.njj.galaxion.endtoendtesting.model.entity;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheduler")
public class SchedulerEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "environment_id", foreignKey = @ForeignKey(name = "fk__configuration__environment_id"))
    private EnvironmentEntity environment;

    @Setter
    @Column(name = "pipeline_id")
    private String pipelineId;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SchedulerStatus status = SchedulerStatus.IN_PROGRESS;

    @Setter
    @Column(name = "suites")
    private Integer suites;

    @Setter
    @Column(name = "tests")
    private Integer tests;

    @Setter
    @Column(name = "passes")
    private Integer passes;

    @Setter
    @Column(name = "failures")
    private Integer failures;

    @Setter
    @Column(name = "skipped")
    private Integer skipped;

    @Setter
    @Column(name = "pass_percent")
    private Integer passPercent;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Setter
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;
}
