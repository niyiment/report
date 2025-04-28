package com.niyiment.report.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Slf4j
@Service
@RequiredArgsConstructor
public class JdbcQueryExecutorService implements QueryExecutorService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final int PAGE_SIZE = 5000;

    @Override
    public Stream<Map<String, Object>> executeQuery(String baseQuery, Map<String, Object> params) {
        Spliterator<Map<String, Object>> spliterator = new Spliterators.AbstractSpliterator<>(
                Long.MAX_VALUE, Spliterator.ORDERED
        ) {
            int offset = 0;
            boolean finished = false;

            @Override
            public boolean tryAdvance(Consumer<? super Map<String, Object>> action) {
                if (finished) return false;

                List<Map<String, Object>> batch = namedParameterJdbcTemplate.query(baseQuery
                +  " LIMIT :limit OFFSET :offset", new MapSqlParameterSource(params)
                        .addValue("limit", PAGE_SIZE)
                        .addValue("offset", offset),
                        (rs, rowNum) -> {
                            ResultSetMetaData metaData = rs.getMetaData();
                            Map<String, Object> map = new LinkedHashMap<>();
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                map.put(metaData.getColumnLabel(i), rs.getObject(i));
                            }

                            return  map;
                        });

                if (batch.isEmpty()) {
                    finished = true;
                    return false;
                }

                batch.forEach(action);
                offset += PAGE_SIZE;

                return true;
            }
        };

        return StreamSupport.stream(spliterator, false);
    }
}
