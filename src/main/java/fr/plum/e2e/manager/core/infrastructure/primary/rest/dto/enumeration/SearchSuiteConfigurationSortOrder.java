package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchSuiteConfigurationSortOrder {
  ASC("asc"),
  DESC("desc");

  private final String order;
}
