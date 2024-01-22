package fr.njj.galaxion.endtoendtesting.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class StringMapConverter implements AttributeConverter<Map<String, String>, String> {

  private static final String ENTRY_SPLIT_CHAR = ";";
  private static final String KEY_VALUE_SPLIT_CHAR = "=";

  @Override
  public String convertToDatabaseColumn(Map<String, String> map) {
    if (map == null) {
      return "";
    }

    return map.entrySet().stream()
        .map(entry -> entry.getKey() + KEY_VALUE_SPLIT_CHAR + entry.getValue())
        .collect(Collectors.joining(ENTRY_SPLIT_CHAR));
  }

  @Override
  public Map<String, String> convertToEntityAttribute(String string) {
    if (string == null || string.trim().isEmpty()) {
      return new HashMap<>();
    }

    return Arrays.stream(string.split(ENTRY_SPLIT_CHAR))
        .map(entry -> entry.split(KEY_VALUE_SPLIT_CHAR))
        .collect(Collectors.toMap(entry -> entry[0], entry -> entry.length > 1 ? entry[1] : ""));
  }
}
