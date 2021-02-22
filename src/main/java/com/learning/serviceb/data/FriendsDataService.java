package com.learning.serviceb.data;

import com.learning.serviceb.graphql.types.Address;
import com.learning.serviceb.graphql.types.Friend;
import com.learning.serviceb.security.AuthContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FriendsDataService {

    private final Map<String, Friend> friendsMap = new HashMap<>();

    public FriendsDataService() {
        Friend friend1 = new Friend("1", "Satya", new Address("1",null));
        Friend friend2 = new Friend("2", "Srikanth", new Address("2",null));
        Friend friend3 = new Friend("3", "Nandhu", new Address("3",null));
        Friend friend4 = new Friend("4", "Chinni", new Address("4",null));
        Friend friend5 = new Friend("5", "Sindhu", new Address("5",null));

        friendsMap.put("1", friend1);
        friendsMap.put("2", friend2);
        friendsMap.put("3", friend3);
        friendsMap.put("4", friend4);
        friendsMap.put("5", friend5);
    }

    public Friend getFriendById(String id) {
        return friendsMap.get(id);
    }

    public Map<String, Friend> getFriendsMap(Collection<String> ids) {
        log.info("Called getFriendsMap - requestId {} with ids {}", AuthContext.getRequestId(),ids);
        return friendsMap
                .entrySet()
                .stream()
                .filter(stringFriendEntry -> ids.contains(stringFriendEntry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
