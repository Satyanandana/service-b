package com.learning.serviceb.data;

import com.learning.serviceb.graphql.types.Address;
import com.learning.serviceb.security.AuthContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddressDataService {

    private final Map<String, Address> addressMap = new HashMap<>();

    public AddressDataService() {
        Address Address1 = new Address("1", "Chandler");
        Address Address2 = new Address("2", "Boston");
        Address Address3 = new Address("3", "Scottsdale");
        Address Address4 = new Address("4", "Tempe");
        Address Address5 = new Address("5", "Mesa");

        addressMap.put("1", Address1);
        addressMap.put("2", Address2);
        addressMap.put("3", Address3);
        addressMap.put("4", Address4);
        addressMap.put("5", Address5);
    }

    public Address getAddressById(String id) {
        return addressMap.get(id);
    }

    public Map<String, Address> getAddressMap(Collection<String> ids) {

        log.info("Called getAddressMap - requestId {} with ids {}", AuthContext.getRequestId(),ids);

        return addressMap
                .entrySet()
                .stream()
                .filter(stringAddressEntry -> ids.contains(stringAddressEntry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
