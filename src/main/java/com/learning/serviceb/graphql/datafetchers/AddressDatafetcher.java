package com.learning.serviceb.graphql.datafetchers;

import com.learning.serviceb.graphql.DgsConstants;
import com.learning.serviceb.graphql.dataloaders.AddressDataLoader;
import com.learning.serviceb.graphql.types.Address;
import com.learning.serviceb.graphql.types.Friend;
import com.learning.serviceb.security.AuthContext;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

 @Slf4j
@DgsComponent
public class AddressDatafetcher {

    @DgsEntityFetcher(name = DgsConstants.ADDRESS.TYPE_NAME)
    public CompletableFuture<Address> getAddress(Map<String, Object> values, DgsDataFetchingEnvironment dfe) {
        DataLoader<String, Address> dataLoader = dfe.getDataLoader(AddressDataLoader.class);
        String id = (String) values.get(DgsConstants.ADDRESS.Id);
        log.info("Called Address EntityFetcher - requestId {} with ids {}", AuthContext.getRequestId(),id);
        return dataLoader.load(id);
    }

    @DgsData(
            parentType = DgsConstants.FRIEND.TYPE_NAME, field = DgsConstants.FRIEND.Address)
    public CompletableFuture<Address> getAddress(DgsDataFetchingEnvironment dfe) {
        DataLoader<String, Address> dataLoader = dfe.getDataLoader(AddressDataLoader.class);
        Friend friend = dfe.getSource();
        log.info("Called Address datafetcher  - requestId {} with ids {}", AuthContext.getRequestId(),friend);
        return dataLoader.load(friend.getAddress().getId());
    }
}
