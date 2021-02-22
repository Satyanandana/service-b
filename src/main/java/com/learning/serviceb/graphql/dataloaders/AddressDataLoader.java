package com.learning.serviceb.graphql.dataloaders;

import com.learning.serviceb.data.AddressDataService;
import com.learning.serviceb.graphql.DgsConstants;
import com.learning.serviceb.graphql.types.Address;
import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

@DgsDataLoader(name = DgsConstants.ADDRESS.TYPE_NAME)
public class AddressDataLoader implements MappedBatchLoader<String, Address> {

    @Autowired
    private AddressDataService addressDataService;

    @Override
    public CompletionStage<Map<String, Address>> load(Set<String> keys) {
        return CompletableFuture.supplyAsync(() -> addressDataService.getAddressMap(keys));
    }

}
