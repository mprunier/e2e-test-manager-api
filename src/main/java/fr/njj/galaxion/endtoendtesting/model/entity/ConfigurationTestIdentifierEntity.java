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

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "configuration_test_identifier_table")
public class ConfigurationTestIdentifierEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "configuration_test_id", foreignKey = @ForeignKey(name = "fk__configuration_test_identifier_table__configuration_test_id"), nullable = false)
    private ConfigurationTestEntity configurationTest;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "environment_id", nullable = false)
    private Long environmentId;

}
