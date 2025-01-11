package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchSuiteConfigurationSortField {
  TITLE("title"),
  FILE("file"),
  LAST_PLAYED_AT("lastPlayedAt");

  private final String field;
}
