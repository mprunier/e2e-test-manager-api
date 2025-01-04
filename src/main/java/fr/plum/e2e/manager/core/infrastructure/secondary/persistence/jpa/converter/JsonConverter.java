package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter
public class JsonConverter<T> implements AttributeConverter<T, String> {
  private final ObjectMapper objectMapper =
      new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

  private final Class<T> typeParameterClass;

  public JsonConverter(Class<T> typeParameterClass) {
    this.typeParameterClass = typeParameterClass;
  }

  @Override
  public String convertToDatabaseColumn(T attribute) {
    try {
      return objectMapper.writeValueAsString(attribute);
    } catch (JsonProcessingException e) {
      log.error("Error converting object to json", e);
      return null;
    }
  }

  @Override
  public T convertToEntityAttribute(String dbData) {
    try {
      return objectMapper.readValue(dbData, typeParameterClass);
    } catch (JsonProcessingException e) {
      log.error("Error converting json to object", e);
      return null;
    }
  }
}
