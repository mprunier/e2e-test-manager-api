package fr.plum.e2e.manager.core.domain.port.view;

import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentProjection;
import java.util.List;

public interface ListAllEnvironmentsPort {

  List<EnvironmentProjection> listAll();
}