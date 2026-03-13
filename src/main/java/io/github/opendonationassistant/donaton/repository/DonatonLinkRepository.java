package io.github.opendonationassistant.donaton.repository;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface DonatonLinkRepository
  extends CrudRepository<DonatonLink, String> {
  Optional<DonatonLink> findByOriginId(String originId);
}
