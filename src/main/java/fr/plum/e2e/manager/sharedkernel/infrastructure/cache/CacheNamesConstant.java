package fr.plum.e2e.manager.sharedkernel.infrastructure.cache;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheNamesConstant {

  //  JPA Cache
  public static final String CACHE_JPA_ENVIRONMENT_BY_ID = "jpa-environment-by-id";
  public static final String CACHE_JPA_ENVIRONMENTS_BY_PROJECT_BRANCH =
      "jpa-environments-by-project-branch";

  // HTTP Cache
  public static final String CACHE_HTTP_LIST_ALL_ENVIRONMENTS = "http-list-all-environments";
  public static final String CACHE_HTTP_GET_ENVIRONMENT_DETAILS = "http-get-environment-details";
  public static final String CACHE_HTTP_GET_SCHEDULER_DETAILS = "http-get-scheduler-details";

  // Other Cache
  public static final String CACHE_GITLAB_JOB = "gitlab-job";
}
