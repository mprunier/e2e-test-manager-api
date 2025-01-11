package fr.plum.e2e.manager.core.infrastructure.primary.rest.utils;

import io.quarkus.security.identity.SecurityIdentity;
import java.security.Principal;
import java.util.Optional;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class RestUtils {

  public static String extractUsername(SecurityIdentity identity) {
    return Optional.ofNullable(identity)
        .map(SecurityIdentity::getPrincipal)
        .map(Principal::getName)
        .orElse("Unknown");
  }
}
