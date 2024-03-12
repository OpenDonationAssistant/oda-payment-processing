package io.github.opendonationassistant.reel;

import java.util.List;
import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReelRepository extends CrudRepository<ReelData, String> {
  List<ReelData> getByRecipientId(String recipientId);
  Optional<ReelData> getByWidgetConfigId(String widgetConfigId);
}
