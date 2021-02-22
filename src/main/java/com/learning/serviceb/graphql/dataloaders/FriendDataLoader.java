package com.learning.serviceb.graphql.dataloaders;

import com.learning.serviceb.data.FriendsDataService;
import com.learning.serviceb.graphql.DgsConstants;
import com.learning.serviceb.graphql.types.Friend;
import com.netflix.graphql.dgs.DgsDataLoader;
import org.dataloader.MappedBatchLoader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@DgsDataLoader(name = DgsConstants.FRIEND.TYPE_NAME)
public class FriendDataLoader implements MappedBatchLoader<String, Friend> {

    @Autowired
    private FriendsDataService friendsDataService;


    @Override
    public CompletionStage<Map<String, Friend>> load(Set<String> keys) {
        return CompletableFuture.supplyAsync(() -> friendsDataService.getFriendsMap(keys));
    }

}
