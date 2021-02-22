package com.learning.serviceb.graphql.config;

import com.learning.serviceb.data.AddressDataService;
import com.learning.serviceb.data.FriendsDataService;
import com.learning.serviceb.graphql.DgsConstants;
import com.learning.serviceb.graphql.datafetchers.BaseDataFetcher;
import com.learning.serviceb.graphql.datafetchers.FederationDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

@Configuration
public class GraphQLConfig {

    @Autowired
    public FriendsDataService friendsDataService;

    @Autowired
    public AddressDataService addressDataService;

    @Bean("FederationDataFetcher")
    public BaseDataFetcher getFederationDataFetcher() {
        return new FederationDataFetcher
                .Builder()
                .addFederationType(DgsConstants.FRIEND.TYPE_NAME,
                        input -> new FederationDataFetcher
                                .Key(DgsConstants.FRIEND.TYPE_NAME, input.get(DgsConstants.FRIEND.Id)),
                        input -> friendsDataService
                                .getFriendsMap(input
                                        .stream()
                                        .map(FederationDataFetcher.Key::getKey)
                                        .map(String::valueOf).collect(Collectors.toList()))
                                .entrySet()
                                .stream()
                                .collect(Collectors
                                        .toMap(o -> new FederationDataFetcher
                                                        .Key(DgsConstants.FRIEND.TYPE_NAME, o.getKey()),
                                                stringFriendEntry -> stringFriendEntry.getValue())))
                .addFederationType(DgsConstants.ADDRESS.TYPE_NAME,
                        input -> new FederationDataFetcher
                                .Key(DgsConstants.ADDRESS.TYPE_NAME, input.get(DgsConstants.ADDRESS.Id)),
                        input -> addressDataService
                                .getAddressMap(input
                                        .stream()
                                        .map(FederationDataFetcher.Key::getKey)
                                        .map(String::valueOf).collect(Collectors.toList()))
                                .entrySet()
                                .stream()
                                .collect(Collectors
                                        .toMap(o -> new FederationDataFetcher
                                                        .Key(DgsConstants.ADDRESS.TYPE_NAME, o.getKey()),
                                                stringFriendEntry -> stringFriendEntry.getValue())))
                .build();
    }

}
