package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testresult;

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
@Table(name = "test_result_video")
public class JpaTestVideoEntity extends PanacheEntityBase {

  @Id private UUID id;

  @Column(name = "test_result_id", nullable = false)
  protected UUID testResultId;

  @Column(name = "video", nullable = false)
  private byte[] video;
}
