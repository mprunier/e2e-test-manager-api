package fr.plum.e2e.manager.core.domain.port.out.query;

import fr.plum.e2e.manager.core.domain.model.view.EnvironmentView;
import java.util.List;

public interface ListAllEnvironmentsPort {

  List<EnvironmentView> listAll();
}
