package fr.plum.e2e.OLD.model.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
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
@Table(name = "old_file_group")
public class FileGroupEntity extends PanacheEntityBase {

  @Id
  @Setter
  @Column(name = "file", nullable = false)
  private String file;

  @Setter
  @Column(name = "group_name", nullable = false)
  private String group;

  @ManyToOne
  @JoinColumn(
      name = "environment_id",
      foreignKey = @ForeignKey(name = "fk__file_group__environment_id"),
      nullable = false)
  private EnvironmentEntity environment;
}
