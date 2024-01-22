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
@Table(name = "environment_variable")
public class EnvironmentVariableEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__environment_variable__environment_id"))
  private EnvironmentEntity environment;

  @Setter
  @Column(name = "name", nullable = false)
  private String name;

  @Setter
  @Column(name = "default_value", nullable = false)
  private String defaultValue;

  @Setter
  @Column(name = "description")
  private String description;

  @Setter
  @Builder.Default
  @Column(name = "is_hidden", nullable = false)
  private Boolean isHidden = false;
}
