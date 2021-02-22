package com.learning.serviceb.graphql.datafetchers;

import com.learning.serviceb.graphql.DgsConstants;
import com.learning.serviceb.graphql.dataloaders.FriendDataLoader;
import com.learning.serviceb.graphql.types.Friend;
import com.learning.serviceb.security.AuthContext;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsEntityFetcher;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@DgsComponent
public class FriendDatafetcher {

    @DgsEntityFetcher(name = DgsConstants.FRIEND.TYPE_NAME)
    public CompletableFuture<Friend> getFriend(Map<String, Object> values, DgsDataFetchingEnvironment dfe) {
        DataLoader<String, Friend> dataLoader = dfe.getDataLoader(FriendDataLoader.class);
        String id = (String) values.get(DgsConstants.FRIEND.Id);
        log.info("Called Friend EntityFetcher - requestId {} with ids {}", AuthContext.getRequestId(),id);
        return dataLoader.load(id);
    }

}
