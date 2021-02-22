package com.learning.serviceb.graphql.config;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.federation.DefaultDgsFederationResolver;
import graphql.schema.DataFetcher;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class CustomDgsFederationResolver extends DefaultDgsFederationResolver {

    @Qualifier("FederationDataFetcher")
    @Autowired
    private DataFetcher federationDataFetcher;

    @NotNull
    @Override
    public DataFetcher<Object> entitiesFetcher() {
        return federationDataFetcher;
    }

}
