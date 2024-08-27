package org.development.blogApi.common.utils;

import java.util.Map;

public class FilterResult {
    private String query;
    private Map<String, Object> parameters;

    public FilterResult(String query, Map<String, Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public String getQuery() {
        return query;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
