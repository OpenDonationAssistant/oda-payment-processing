package io.github.opendonationassistant.reel.repository;

import java.util.Optional;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface ReelLinkRepository extends CrudRepository<ReelLink, String> {
  Optional<ReelLink> findByOriginId(String originId);
}
