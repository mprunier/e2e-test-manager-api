package fr.njj.galaxion.endtoendtesting.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "environment_synchronization_error")
@EqualsAndHashCode(callSuper = true)
public class EnvironmentSynchronizationErrorEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__environment_synchronization_error__environment_id"),
      nullable = false)
  private EnvironmentEntity environment;

  @Column(name = "file", nullable = false)
  private String file;

  @Setter
  @Column(name = "error", nullable = false)
  private String error;

  @Setter
  @Builder.Default
  @Column(name = "error_at", nullable = false)
  private ZonedDateTime at = ZonedDateTime.now();
}
