package fr.njj.galaxion.endtoendtesting.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

  private static final String SPLIT_CHAR = ";";

  @Override
  public String convertToDatabaseColumn(List<String> stringList) {
    if (stringList == null || stringList.isEmpty()) {
      return null;
    }
    List<String> sortedList = new ArrayList<>(stringList);
    Collections.sort(sortedList);
    return String.join(SPLIT_CHAR, sortedList);
  }

  @Override
  public List<String> convertToEntityAttribute(String string) {
    if (string == null) {
      return null;
    }
    List<String> list = new ArrayList<>(List.of(string.split(SPLIT_CHAR)));
    Collections.sort(list);
    return list;
  }
}
