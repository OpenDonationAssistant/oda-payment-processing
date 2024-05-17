package io.github.opendonationassistant.goal;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface GoalRepository extends CrudRepository<GoalData, String> {
  List<GoalData> getByRecipientId(String recipientId);
  Optional<GoalData> getById(String id);
}
