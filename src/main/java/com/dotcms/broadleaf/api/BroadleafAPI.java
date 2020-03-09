package com.dotcms.broadleaf.api;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import com.dotcms.broadleaf.util.BundleConfigProperties;
import com.dotcms.http.CircuitBreakerUrl;
import com.dotcms.http.CircuitBreakerUrl.Method;
import com.dotcms.rest.api.v1.DotObjectMapperProvider;
import com.dotmarketing.exception.DotRuntimeException;
import com.google.common.collect.ImmutableMap;
import io.vavr.control.Try;

public class BroadleafAPI {



    final static String baseUrl = BundleConfigProperties.getProperty("com.dotcms.broadleaf.url");
    final static String userNamePassword =
                    Base64.getEncoder()
                                    .encodeToString((BundleConfigProperties.getProperty("com.dotcms.broadleaf.username") + ":"
                                                    + BundleConfigProperties.getProperty("com.dotcms.broadleaf.password"))
                                                                    .getBytes());

    final static String searchUrl = "/catalog/search?q={searchQuery}&pageSize={limit}&page=1&includePromotionMessages=true&includePriceData=true";


    final static Map<String, String> defaultHeaders =
                    ImmutableMap.of("Authorization", "Basic " + userNamePassword, "Accept", "application/json");


    public Map<String, Object> loadUrl(final String url, final Map<String, String> params){

        return Try.of(()-> DotObjectMapperProvider
                        .getInstance()
                        .getDefaultObjectMapper()
                        .readValue(getUrlToString(url, ImmutableMap.of()), Map.class))
                  .getOrElseThrow(e->new DotRuntimeException(e));
    }

    public String getUrlToString(final String stringUrl)  {
        return getUrlToString(stringUrl, ImmutableMap.of());

    }

    public String getUrlToString(final String stringUrl, final Map<String, String> params)  {

        URL url = Try.of(() -> new URL(baseUrl + stringUrl))
                        .getOrElseThrow(e -> new DotRuntimeException("unable to connect:" + e.getMessage()));


        return Try.of(()->  CircuitBreakerUrl.builder()
                        .setMethod(Method.GET)
                        .setHeaders(defaultHeaders)
                        .setUrl(url.toString())
                        .setParams(params)
                        .setTimeout(5000).build().doString()
                        ).getOrElseThrow(e->new DotRuntimeException(e));

    }

    public String postUrlToString(final String stringUrl, final Map<String, String> params)  {

        URL url = Try.of(() -> new URL(baseUrl + stringUrl))
                        .getOrElseThrow(e -> new DotRuntimeException("unable to connect:" + e.getMessage()));


        return Try.of(()->  CircuitBreakerUrl.builder()
                        .setMethod(Method.POST)
                        .setHeaders(defaultHeaders)
                        .setUrl(url.toString())
                        .setParams(params)
                        .setTimeout(5000).build().doString()
                        ).getOrElseThrow(e->new DotRuntimeException(e));

    }

    private Map<String, Object> loadUrl(final String stringUrl) {
        return loadUrl(stringUrl, ImmutableMap.of());
    }


    public Map<String, Object> getProduct(final String productId) {
        return loadUrl(baseUrl + "/catalog/product/" + productId);


    }



    public List<Map<String, Object>> searchProducts(final String searchQuery, int limit)  {
        limit = limit<1 ? 1 : limit;


        return (List<Map<String, Object>>) loadUrl(
                        searchUrl.replace("{searchQuery}", searchQuery).replace("{limit}", String.valueOf(limit))).computeIfAbsent("products", k-> new ArrayList<>());

    }



}
