package com.learning.serviceb.graphql.datafetchers;

import com.apollographql.federation.graphqljava._Entity;
import com.google.common.base.Function;
import graphql.GraphQLException;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DataFetcher implementation that can be used to resolve the Federated graphQL Types.
 *
 * @author satyanandana
 */

@Slf4j
public class FederationDataFetcher implements DataFetcher {

    public static final String TYPENAME = "__typename";
    public static final String DATA_EXTRACT_FUNCTION_IS_NOT_DEFINED_FOR_TYPE =
            "DataExtractFunction is not defined for type: %s";
    public static final String KEY_EXTRACT_FUNCTION_IS_NOT_DEFINED_FOR_TYPE =
            "KeyExtractFunction is not defined for type: %s";
    public static final String UNRESOLVED_KEYS = "Unresolved keys : %s ";
    private final Map<String, Function<Map<String, Object>, Key>> typeKeyExtractorMap;
    private final Map<String, Function<List<Key>, Map<Key, ?>>> typeDataResolverMap;

    private FederationDataFetcher(
            Map<String, Function<Map<String, Object>, Key>> typeKeyExtractorMap,
            Map<String, Function<List<Key>, Map<Key, ?>>> typeDataResolverMap) {
        this.typeKeyExtractorMap = typeKeyExtractorMap;
        this.typeDataResolverMap = typeDataResolverMap;
    }

    /**
     * This Datafetcher will aggregates the keys of configured GraphQL Type
     * using the keyExtractor function defined in
     * {@link Builder#addFederationType(String, Function, Function)}
     * Gets the data for provided keys using the DataExtractor function.And return the data for
     * the provided Keys.
     *
     * @param environment {@link DataFetchingEnvironment}
     * @return Object
     * @throws Exception exception.
     */
    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {

        final List<Map<String, Object>> argument = environment
                .getArgument(_Entity.argumentName);

        Map<String, List<Key>> typeKeyMap = getTypeKeyMap(argument);

        Map<Key, Object> resultMap = getResultMap(typeKeyMap);

        List<Key> unresolvedKeys = new ArrayList<>();

        List<Object> result = getFinalData(typeKeyMap, resultMap, unresolvedKeys);

        if (!unresolvedKeys.isEmpty()) {
            final String message = String
                    .format(UNRESOLVED_KEYS, Arrays.toString(unresolvedKeys.toArray()));
            log.error(message);
            throw new GraphQLException(message);
        }

        return result;
    }

    @NotNull
    private List<Object> getFinalData(Map<String, List<Key>> typeKeyMap,
                                      Map<Key, Object> resultMap,
                                      List<Key> unresolvedKeys) {
        return typeKeyMap
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(key -> {
                    final Object value = resultMap.get(key);
                    if (Objects.isNull(value)) {
                        unresolvedKeys.add(key);
                    }
                    return value;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @NotNull
    private Map<Key, Object> getResultMap(
            Map<String, List<Key>> typeKeyMap) {
        return typeKeyMap
                .entrySet()
                .stream()
                .map(entry -> {
                    return getData(entry);
                })
                .filter(Objects::nonNull)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, List<Key>> getTypeKeyMap(List<Map<String, Object>> argument) {
        return argument
                .stream()
                .map(values -> getKey(values))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Key::getTypeName));
    }

    private Map<Key, ?> getData(Map.Entry<String, List<Key>> entry) {
        final Function<List<Key>, Map<Key, ?>> typeDataResolverFunction = typeDataResolverMap
                .get(entry.getKey());
        if (Objects.nonNull(typeDataResolverFunction)) {
            return typeDataResolverFunction.apply(entry.getValue());
        }
        final String msg =
                String.format(DATA_EXTRACT_FUNCTION_IS_NOT_DEFINED_FOR_TYPE, entry.getKey());
        log.error(msg);
        throw new GraphQLException(msg);
    }

    @Nullable
    private Key getKey(Map<String, Object> values) {
        final String typeName = String.valueOf(values.get(TYPENAME));
        if (Objects.nonNull(typeName)) {

            final Function<Map<String, Object>, Key> keyExtractFunction =
                    typeKeyExtractorMap.get(typeName);
            if (Objects.nonNull(keyExtractFunction)) {
                return keyExtractFunction.apply(values);
            }

            final String msg =
                    String.format(KEY_EXTRACT_FUNCTION_IS_NOT_DEFINED_FOR_TYPE, typeName);
            log.error(msg);
            throw new GraphQLException(msg);
        }
        return null;
    }

    /**
     * This builder provides method {@link #addFederationType(String, Function, Function)}
     * to build a {@link FederationDataFetcher} and return an instance of {@link BaseDataFetcher}.
     */
    public static class Builder {

        private final Map<String, Function<Map<String, Object>, Key>> typeKeyExtractorMap
                = new HashMap<>();
        private final Map<String, Function<List<Key>, Map<Key, ?>>> typeDataResolverMap
                = new HashMap<>();

        public Builder() {
        }

        /**
         * Add handlers to extract key and to get data for a specific GraphQL type.
         *
         * @param typeName      GraphQL Type
         * @param keyExtractor  Lambda is a <pre> {@code Function<Map<String, Object>, Key> } </pre>
         *                      which extract the key from {@link DataFetchingEnvironment}
         * @param dataExtractor Lambda is a <pre> {@code Function<List<Key>, Map<Key, ?>> } </pre>
         *                      which gets the data for the provided <pre>{@code List<Key>} </pre>
         */
        public Builder addFederationType(
                String typeName,
                Function<Map<String, Object>, Key> keyExtractor,
                Function<List<Key>, Map<Key, ?>> dataExtractor) {

            Assert.notNull(typeName, "The graphql type name should not be null.");
            Assert.notNull(keyExtractor, "The keyExtractor function should not be null.");
            Assert.notNull(dataExtractor, "The dataExtractor function should not be null.");

            this.typeKeyExtractorMap.put(typeName, keyExtractor);
            this.typeDataResolverMap.put(typeName, dataExtractor);
            return this;
        }

        /**
         * Add handlers to extract key and to get data for a specific GraphQL type.
         *
         * @param federationType {@link FederationType}
         * @return {@link Builder}
         */
        public Builder addFederationType(FederationType federationType) {
            if (Objects.nonNull(federationType)) {
                addFederationType(federationType.getTypeName(),
                        federationType.getKeyExtractor(),
                        federationType.getDataExtractor());
            }
            return this;
        }

        /**
         * Build a {@link FederationDataFetcher} and return an instance of {@link BaseDataFetcher}.
         *
         * @return {@link BaseDataFetcher}
         */
        public BaseDataFetcher build() {
            return new BaseDataFetcher(new FederationDataFetcher(
                    this.typeKeyExtractorMap,
                    this.typeDataResolverMap));
        }

    }

    @AllArgsConstructor
    @Getter
    public static class FederationType {
        private final String typeName;
        private final Function<Map<String, Object>, FederationDataFetcher.Key> keyExtractor;
        private final Function<List<FederationDataFetcher.Key>,
                Map<FederationDataFetcher.Key, ?>> dataExtractor;
    }

    /**
     * This class holds the key of specific GraphQL type.
     * {@link #typeName} is the GraphQL type name
     * {@link #key} is the object representing the key of the respective GraphQL type.
     * The key can be a string or a custom object with multiple attributes.
     * Make sure to override equals(), hashCode() and toString() in the Custom key.
     */
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class Key {
        private final String typeName;
        private final Object key;
    }
}
