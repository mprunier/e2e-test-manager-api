package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "test_result_screenshot")
public class JpaTestScreenshotEntity extends PanacheEntityBase {

  @Id private UUID id;

  @Column(name = "test_result_id", nullable = false)
  protected UUID testResultId;

  @Column(name = "filename", nullable = false)
  private String filename;

  @Column(name = "screenshot", nullable = false)
  private byte[] screenshot;
}
