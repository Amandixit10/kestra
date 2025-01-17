package io.kestra.repository.postgres;

import io.kestra.core.models.executions.LogEntry;
import io.kestra.jdbc.repository.AbstractJdbcLogRepository;
import io.kestra.jdbc.services.JdbcFilterService;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.slf4j.event.Level;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Singleton
@PostgresRepositoryEnabled
public class PostgresLogRepository extends AbstractJdbcLogRepository {
    @Inject
    public PostgresLogRepository(@Named("logs") PostgresRepository<LogEntry> repository,
                                 JdbcFilterService filterService) {
        super(repository, filterService);
    }

    @Override
    protected Condition findCondition(String query) {
        return this.jdbcRepository.fullTextCondition(Collections.singletonList("fulltext"), query);
    }

    @Override
    protected Condition levelsCondition(List<Level> levels) {
        return DSL.condition("level in (" +
            levels
                .stream()
                .map(s -> "'" + s + "'::log_level")
                .collect(Collectors.joining(", ")) +
            ")");
    }
}
