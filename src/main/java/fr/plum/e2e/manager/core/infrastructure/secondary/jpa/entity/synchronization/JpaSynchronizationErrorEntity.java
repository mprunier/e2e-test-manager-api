package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.synchronization;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "synchronization_error")
@IdClass(JpaSynchronizationErrorId.class)
public class JpaSynchronizationErrorEntity extends PanacheEntityBase {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "synchronization_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk__synchronization_error__synchronization_id"))
  private JpaSynchronizationEntity synchronization;

  @Id
  @Column(name = "file", nullable = false)
  private String file;

  @Id
  @Column(name = "error", nullable = false)
  private String error;

  @Id
  @Builder.Default
  @Column(name = "error_at", nullable = false)
  private ZonedDateTime at = ZonedDateTime.now();
}
