package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "old_test_screenshot")
public class JpaTestScreenshotEntity extends PanacheEntityBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "test_id",
      foreignKey = @ForeignKey(name = "fk__test_screenshot__test_id"),
      nullable = false)
  protected JpaTestResultEntity test;

  @Column(name = "filename", nullable = false)
  private String filename;

  @Column(name = "screenshot", nullable = false)
  private byte[] screenshot;
}
