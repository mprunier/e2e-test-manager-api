package fr.plum.e2e.manager.core.domain.model.projection;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginatedProjection<T> {
  List<T> content;
  int currentPage;
  int totalPages;
  int size;
  long totalElements;
}
