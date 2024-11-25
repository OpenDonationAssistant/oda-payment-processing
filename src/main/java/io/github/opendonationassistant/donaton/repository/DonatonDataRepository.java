package io.github.opendonationassistant.donaton.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DonatonDataRepository
  extends CrudRepository<DonatonData, String> {
  List<DonatonData> getByRecipientIdOrderByEndDateDesc(String recipientId);
}
