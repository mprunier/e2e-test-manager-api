package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.environment;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(JpaEnvironmentVariableId.class)
@Table(name = "environment_variable")
public class JpaEnvironmentVariableEntity extends PanacheEntityBase {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "environment_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk__environment_variable__environment_id"))
  private JpaEnvironmentEntity environment;

  @Id
  @Column(name = "name", nullable = false)
  private String name;

  @Setter
  @Column(name = "default_value", nullable = false)
  private String defaultValue;

  @Setter
  @Column(name = "description")
  private String description;

  @Setter
  @Column(name = "is_hidden", nullable = false)
  private boolean isHidden;
}
