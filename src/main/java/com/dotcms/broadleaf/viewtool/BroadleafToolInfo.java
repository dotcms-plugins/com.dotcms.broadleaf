package com.dotcms.broadleaf.viewtool;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

public class BroadleafToolInfo extends ServletToolInfo {

    @Override
    public String getKey () {
        return "broadleaf";
    }

    @Override
    public String getScope () {
        return ViewContext.REQUEST;
    }

    @Override
    public String getClassname () {
        return BroadleafTool.class.getName();
    }

    @Override
    public Object getInstance ( Object initData ) {

        BroadleafTool viewTool = new BroadleafTool();
        viewTool.init( initData );

        setScope( ViewContext.REQUEST );

        return viewTool;
    }

}