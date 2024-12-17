package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.PaginatedView;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.function.Function;

public record PaginatedResponse<T>(
    @NotNull List<T> content,
    @NotNull Integer currentPage,
    @NotNull Integer totalPages,
    @NotNull Integer size,
    @NotNull Long totalElements) {

  public static <T, D> PaginatedResponse<T> fromDomain(
      PaginatedView<D> domain, Function<D, T> mapper) {
    List<T> mappedContent =
        domain.getContent() != null ? domain.getContent().stream().map(mapper).toList() : List.of();

    return new PaginatedResponse<>(
        mappedContent,
        domain.getCurrentPage(),
        domain.getTotalPages(),
        domain.getSize(),
        domain.getTotalElements());
  }
}