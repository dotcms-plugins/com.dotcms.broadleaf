
# dotCMS Broadleaf Plugin

This is a POC plugin that provides 2 components, 
- A Broadleaf api interceptor that allows Broadleaf APIs to be called through dotCMS (avoiding cors/auth issues)
- A custom field (.vtl) that can be added to a content type that will allow a user to search braodleaf product listing and store the product id.

---
## API Interceptor
The interceptor proxys access to the broadleaf api endponts, which are not browser accessable by default.  In in providing this, it also obfuscates the broadleaf api key from clients.  To access the apis, you use the normal broadleaf api call as in the broadleaf documentation, except that you make the call locally and append `/broadapi` to the call.  The plugin will intercept the calls and proxy (GETs and POSTs) them to broadleafs api.  

So if you want to call : `http://localhost:8080/broadapi/api/v1/catalog/search?q=hot&pageSize=15&page=1`

instead you should call: `http://localhost:8080/broadapi/api/v1/catalog/product/15`

To see the api working and to see the data hubspot has on you, call:

`https://dotcmssite.com/broadapi/test`



**Note** this component exposes the broadleaf API based the api key used in the plugin.  This should either be locked down or should modified for use in production environs. 

---

## Broadleaf Custom Field
Adds a .vtl under DEFAULT_HOST/application/vtl/broadleaf/

## Installation

Before installing the plugin, you need to change a single line of code and set your broadleaf url/api key

https://github.com/dotCMS-plugins/com.dotcms.broadleaf/blob/master/src/main/resources/broadleaf-custom-field.vtl

Then build it
`./gradlew jar`

Then upload the files into your dotCMS installation:



## DISCLAIMER
Plugins are code outside of the dotCMS core code and unless explicitly stated otherwise, are not covered or warrantied  under dotCMS support engagements. However, support and customization for plugins is available through our Enterprise Services department. Contact us for more information.


