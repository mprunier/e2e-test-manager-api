package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.locker;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(JpaLockManagerId.class)
@Table(name = "lock_manager")
public class JpaLockManagerEntity extends PanacheEntityBase {
  @Id
  @Column(name = "resource_type", nullable = false)
  private String resourceType;

  @Id
  @Column(name = "resource_id", nullable = false)
  private String resourceId;
}
