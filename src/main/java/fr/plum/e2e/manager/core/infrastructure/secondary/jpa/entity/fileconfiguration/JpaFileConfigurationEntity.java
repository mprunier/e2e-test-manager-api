package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.fileconfiguration;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_configuration")
@IdClass(JpaFileConfigurationId.class)
public class JpaFileConfigurationEntity extends PanacheEntityBase {

  @Id
  @Column(name = "file_name")
  private String fileName;

  @Id
  @Column(name = "environment_id")
  private UUID environmentId;

  @Column(name = "group_name")
  private String groupName;

  @OneToMany(mappedBy = "fileConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JpaSuiteConfigurationEntity> suiteConfigurations;
}
