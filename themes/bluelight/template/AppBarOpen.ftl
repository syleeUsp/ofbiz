<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<#if (requestAttributes.externalLoginKey)??>
    <#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey!>
</#if>
<#if (externalLoginKey)??>
    <#assign externalKeyParam = "?externalLoginKey=" + requestAttributes.externalLoginKey!>
</#if>
<#assign ofbizServerName = application.getAttribute("_serverId")?default("default-server")>
<#assign contextPath = request.getContextPath()>
<#assign displayApps = Static["org.apache.ofbiz.webapp.control.LoginWorker"].getAppBarWebInfos(security, userLogin, ofbizServerName, "main")>
<#assign displaySecondaryApps = Static["org.apache.ofbiz.webapp.control.LoginWorker"].getAppBarWebInfos(security, userLogin, ofbizServerName, "secondary")>

<#assign appModelMenu = Static["org.apache.ofbiz.widget.model.MenuFactory"].getMenuFromLocation(applicationMenuLocation,applicationMenuName)>
<#if appModelMenu.getModelMenuItemByName(headerItem)??>
    <#if headerItem!="main">
        <#assign show_last_menu = true>
    </#if>
</#if>

<div class="tabbar">
    <div class="breadcrumbs<#if show_last_menu??> menu_selected</#if>">
        <div class="breadcrumbs-start">
            <div id="main-navigation">
                <h2>${uiLabelMap.CommonApplications}</h2>
                <ul>
                    <li>
                        <ul>
                        <!-- CUSTOMIZING MENU 2019.07.22 START -->
                        <#if uspErpTF?has_content>
                            <#if uspErpTF == "true">
                                <#list mainMenuList as display>
                                    <#assign selected = false>
                                    <#assign thisURL = "/uspErp/control/" + display.target>
                                    <#if appMenuNm == display.appMenuNm>
                                        <#assign selected = true>
                                    </#if>
                                    <li<#if selected> class="selected"</#if>>
                                        <a href="${thisURL + externalKeyParam}"<#if uiLabelMap??> title="${uiLabelMap[display.title]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a>
                                    </li>
                                </#list>
                            </#if>
                        <#else>
                        <!-- CUSTOMIZING MENU 2019.07.22 END -->
                            <li>
                                <ul class="primary">
                                <#-- Primary Applications -->
                                <#list displayApps as display>
                                    <#assign thisApp = display.getContextRoot()>
                                    <#assign selected = false>
                                    <#if thisApp == contextPath || contextPath + "/" == thisApp>
                                        <#assign selected = true>
                                    </#if>
                                    <#assign thisApp = StringUtil.wrapString(thisApp)>
                                    <#assign thisURL = thisApp>
                                    <#if thisApp != "/">
                                        <#assign thisURL = thisURL + "/control/main">
                                    </#if>
                                    <#if layoutSettings.suppressTab?? && display.name == layoutSettings.suppressTab>
                                        <!-- do not display this component-->
                                    <#else>
                                        <li <#if selected>class="selected"</#if>><a href="${thisURL + externalKeyParam}" <#if uiLabelMap??> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a></li>
                                    </#if>
                                </#list>
                                </ul>
                            </li>
                            <li>
                                <ul class="secondary">
                                <#-- Secondary Applications -->
                                <#list displaySecondaryApps as display>
                                    <#assign thisApp = display.getContextRoot()>
                                    <#assign selected = false>
                                    <#if thisApp == contextPath || contextPath + "/" == thisApp>
                                        <#assign selected = true>
                                    </#if>
                                    <#assign thisApp = StringUtil.wrapString(thisApp)>
                                    <#assign thisURL = thisApp>
                                    <#if thisApp != "/">
                                        <#assign thisURL = thisURL + "/control/main">
                                    </#if>
                                    <#if layoutSettings.suppressTab?? && display.name == layoutSettings.suppressTab>
                                        <!-- do not display this component-->
                                    <#else>
                                        <li <#if selected>class="selected"</#if>>
                                            <a href="${thisURL + externalKeyParam}" <#if uiLabelMap??> title="${uiLabelMap[display.description]}">${uiLabelMap[display.title]}<#else> title="${display.description}">${display.title}</#if></a>
                                        </li>
                                    </#if>
                                </#list>
                                </ul>
                            </li>
                        </#if>
                        </ul>
                    </li>
                </ul>
            </div>