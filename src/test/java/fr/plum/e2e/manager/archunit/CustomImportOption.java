package fr.plum.e2e.manager.archunit;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

public class CustomImportOption implements ImportOption {
  @Override
  public boolean includes(Location location) {
    return !location.contains("fr/plum/e2e/OLD");
  }
}
