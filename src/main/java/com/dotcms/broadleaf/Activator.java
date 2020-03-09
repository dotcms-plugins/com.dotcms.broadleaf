/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dotcms.broadleaf;


import com.dotcms.broadleaf.servlet.BroadleafInterceptor;
import com.dotcms.broadleaf.util.BundleConfigProperties;
import com.dotcms.broadleaf.viewtool.BroadleafToolInfo;
import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.filters.AutoLoginFilter;
import com.dotmarketing.loggers.Log4jUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import org.osgi.framework.BundleContext;

public final class Activator extends GenericBundleActivator {

    private LoggerContext pluginLoggerContext;

    final BroadleafInterceptor broadleafInterceptor = new BroadleafInterceptor();

    @Override
    public void start(BundleContext context) throws Exception {
        initializeServices(context);
        // Initializing log4j...
        LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
        // Initialing the log4j context of this plugin based on the dotCMS logger context
        pluginLoggerContext = (LoggerContext) LogManager.getContext(this.getClass().getClassLoader(), false, dotcmsLoggerContext,
                        dotcmsLoggerContext.getConfigLocation());

        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                        FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.addFirst(broadleafInterceptor);


        // Copy the custom field

        copyVtls();
        
        
        
        
        
        
        // Registering the ViewTool service
        registerViewToolService(context, new BroadleafToolInfo());

        Logger.info(this.getClass(), "Starting BroadLeafTool");

    }


    @Override
    public void stop(BundleContext context) throws Exception {
        unregisterViewToolServices();
        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                        FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        delegate.remove(broadleafInterceptor.getName(), true);
        unregisterConditionlets();
        unregisterActionlets();

        // Shutting down log4j in order to avoid memory leaks
        Log4jUtil.shutdown(pluginLoggerContext);
    }
    
    
    private String[] vtlsToCopy = new String[] {"broadleaf-custom-field.vtl"};
    
    private String broadleafFolder = "/application/vtl/broadleaf";
    
    private void copyVtls() throws DotDataException, DotSecurityException, IOException {
        Host host = APILocator.getHostAPI().findDefaultHost(APILocator.systemUser(), true);
        Folder folder = APILocator.getFolderAPI().createFolders(broadleafFolder, host, APILocator.systemUser(), true);

        
        
        
        
        
        
        for(String vtl : vtlsToCopy) {
            if(!APILocator.getIdentifierAPI().find(host, broadleafFolder + "/" + vtl).exists()) {
                
                File temp  = File.createTempFile("test", null);
                temp.renameTo(new File(temp.getParent(), vtl));
                IOUtils.copy(BundleConfigProperties.class.getResourceAsStream("/" + vtl), Files.newOutputStream(temp.toPath()));
                
                Logger.info(this.getClass(), "Copying " + vtl + " to " + broadleafFolder);
                
                FileAsset con = new FileAsset();
                con.setTitle(vtl);
                con.setFolder(folder.getInode());
                con.setHost(host.getIdentifier());
                con.setBinary("fileAsset", temp);
                APILocator.getContentletAPI().checkin(con, null);
                
                
            }
            
            
            
        }
        
        
        
        
        
    }
    
    
    
    
    
    
}
