package io.github.opendonationassistant.reel;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReelDataRepository extends CrudRepository<ReelData, String> {
  List<ReelData> getByRecipientId(String recipientId);
  Optional<ReelData> getByWidgetConfigId(String widgetConfigId);
}
