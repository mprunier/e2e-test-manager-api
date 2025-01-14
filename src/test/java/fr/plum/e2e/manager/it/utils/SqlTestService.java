package fr.plum.e2e.manager.it.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

@ApplicationScoped
public class SqlTestService {

  @Inject EntityManager entityManager;

  @Transactional
  public void executeSqlScript(String scriptPath) {
    try {
      String sqlScript =
          new BufferedReader(
                  new InputStreamReader(
                      Objects.requireNonNull(
                          Thread.currentThread()
                              .getContextClassLoader()
                              .getResourceAsStream(scriptPath),
                          "SQL file not found: " + scriptPath)))
              .lines()
              .collect(Collectors.joining("\n"));

      entityManager.createNativeQuery(sqlScript).executeUpdate();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
