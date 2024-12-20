package fr.plum.e2e.manager.core.domain.model.view;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaginatedView<T> {
  List<T> content;
  int currentPage;
  int totalPages;
  int size;
  long totalElements;
}
