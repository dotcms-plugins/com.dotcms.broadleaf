
package com.dotcms.broadleaf.servlet;


import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import com.dotcms.broadleaf.api.BroadleafAPI;
import com.dotcms.broadleaf.util.BundleConfigProperties;
import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotmarketing.util.json.JSONException;
import com.dotmarketing.util.json.JSONObject;

public class BroadleafInterceptor implements WebInterceptor{


    private final static String broadLeafPostingUrl = "/broadapi";
    private final static String[] filters = {broadLeafPostingUrl};
    private final BroadleafAPI broadleafAPI = new BroadleafAPI();
    
    
    private final String corsHeader = BundleConfigProperties.getProperty("com.dotcms.broadleaf.cors.allow.origin","*");
    
    @Override
    public final String[] getFilters() {
        return filters;
    }
    
    @Override
    public String getName() {
        return this.getClass().getName();
    }


    @Override
    public Result intercept(final HttpServletRequest request,
                            final HttpServletResponse response) throws IOException {
        
        
        System.out.println("we got a broadleaf request");
        
        

        final String uri = request.getRequestURI().replaceAll("/api/v1", "") + "?" + request.getQueryString();
        final String broadUrl = uri.replaceAll(broadLeafPostingUrl, "");
        System.err.println("broadLeafPostingUrl:" + broadUrl);



        if ("/test".equals(broadUrl)) {
            response.getWriter().write("broadapi");
            return Result.SKIP_NO_CHAIN;
        }
        
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", corsHeader);

        if ("GET".equals(request.getMethod())) {

            IOUtils.write(broadleafAPI.getUrlToString(broadUrl), response.getOutputStream());
            response.flushBuffer();

        } else if ("POST".equals(request.getMethod())) {

            Map<String, String> params = new HashMap<>();
            Enumeration<String> iter = request.getParameterNames();
            while (iter.hasMoreElements()) {
                String key = iter.nextElement();
                params.put(key, request.getParameter(key));
            }
            response.setContentType("application/json");
            IOUtils.write(broadleafAPI.postUrlToString(broadUrl, params), response.getOutputStream());
            response.flushBuffer();

        }



        return Result.SKIP_NO_CHAIN;




    }

    private JSONObject decodeRequest(HttpServletRequest request) throws IOException, JSONException {
        String x = IOUtils.toString(request.getReader());


        return new JSONObject(x);
    }

    public void destroy() {
        System.out.println("HubspotFilter Filter Destroyed");
    }



}
