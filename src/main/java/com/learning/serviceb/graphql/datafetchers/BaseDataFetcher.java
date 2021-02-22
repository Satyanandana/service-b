package com.learning.serviceb.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.jetbrains.annotations.NotNull;

public class BaseDataFetcher implements DataFetcher {

    private final DataFetcher dataFetcher;

    public BaseDataFetcher(@NotNull DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        return this.dataFetcher.get(environment);
    }
}
