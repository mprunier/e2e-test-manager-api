package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.PaginatedProjection;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public record PaginatedResponse<T>(
    @NotNull List<T> content,
    @NotNull Integer currentPage,
    @NotNull Integer totalPages,
    @NotNull Integer size,
    @NotNull Long totalElements) {

  public static <T, D> PaginatedResponse<T> fromDomain(
      PaginatedProjection<D> domain, Function<D, T> mapper) {
    List<T> mappedContent =
        domain.getContent() != null
            ? domain.getContent().stream().map(mapper).toList()
            : new ArrayList<>();

    return new PaginatedResponse<>(
        mappedContent,
        domain.getCurrentPage(),
        domain.getTotalPages(),
        domain.getSize(),
        domain.getTotalElements());
  }
}
