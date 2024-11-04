package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String[]> {

  @Override
  public String[] convertToDatabaseColumn(List<String> stringList) {
    if (stringList == null || stringList.isEmpty()) {
      return new String[0];
    }
    List<String> sortedList = new ArrayList<>(stringList);
    Collections.sort(sortedList);
    return sortedList.toArray(new String[0]);
  }

  @Override
  public List<String> convertToEntityAttribute(String[] array) {
    if (array == null || array.length == 0) {
      return new ArrayList<>();
    }
    List<String> list = new ArrayList<>(Arrays.asList(array));
    Collections.sort(list);
    return list;
  }
}
