package fr.njj.galaxion.endtoendtesting.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class DayOfWeekConverter implements AttributeConverter<List<DayOfWeek>, String> {

  private static final String SPLIT_CHAR = ";";

  @Override
  public String convertToDatabaseColumn(List<DayOfWeek> dayOfWeekSet) {
    if (dayOfWeekSet == null || dayOfWeekSet.isEmpty()) {
      return "";
    }
    return dayOfWeekSet.stream().map(DayOfWeek::name).collect(Collectors.joining(SPLIT_CHAR));
  }

  @Override
  public List<DayOfWeek> convertToEntityAttribute(String string) {
    if (string == null || string.isEmpty()) {
      return new ArrayList<>();
    }
    return Arrays.stream(string.split(SPLIT_CHAR)).map(DayOfWeek::valueOf).toList();
  }
}
