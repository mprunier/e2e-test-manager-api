package fr.njj.galaxion.endtoendtesting.model.entity;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SynchronizationStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "configuration_synchronization")
@EqualsAndHashCode(callSuper = true)
public class ConfigurationSynchronizationEntity extends PanacheEntityBase {

    @Id
    @ManyToOne
    @JoinColumn(name = "environment_id", foreignKey = @ForeignKey(name = "fk__configuration_suite__environment_id"), nullable = false)
    private EnvironmentEntity environment;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SynchronizationStatus status = SynchronizationStatus.NEVER_SYNC;

    @Setter
    @Column(name = "last_synchronization_at")
    private ZonedDateTime lastSynchronization;

    @Setter
    @Column(name = "error", nullable = false)
    private String error;
}
