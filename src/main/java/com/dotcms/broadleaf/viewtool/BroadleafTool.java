package com.dotcms.broadleaf.viewtool;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;
import com.dotcms.broadleaf.api.BroadleafAPI;

public class BroadleafTool implements ViewTool {
    private HttpServletRequest request;

    @Override
    public void init(Object initData) {

        ViewContext context = (ViewContext) initData;
        request = context.getRequest();

    }

    /**
     * Caches contact information in the user session
     * 
     * @return
     */
    public Map<String,Object> getProduct(String productId) {
        return new BroadleafAPI().getProduct(productId);
    }


    /**
     * Caches contact information in the user session
     * 
     * @return
     */
    public List<Map<String,Object>> searchProducts(String searchQuery, int limit) {
        return new BroadleafAPI().searchProducts(searchQuery, limit);
    }



}
