<%--
    init.jsp: Common imports and initialization code.

    Created:     2016-10-05 15:44 by Christian Berndt
    Modified:    2016-10-08 15:51 by Christian Berndt
    Version:     1.0.3
--%>

<%-- Import required classes --%>
<%@page import="ch.inofix.portlet.newsletter.model.Newsletter"%>
<%@page import="ch.inofix.portlet.newsletter.security.permission.ActionKeys"%>
<%@page import="ch.inofix.portlet.newsletter.service.NewsletterServiceUtil"%>
<%@page import="ch.inofix.portlet.newsletter.service.permission.NewsletterPortletPermission"%>

<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.portal.util.PortalUtil"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<%@page import="javax.portlet.PortletPreferences"%>
<%@page import="javax.portlet.PortletURL"%>

<%-- Import required taglibs --%>

<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/inofix-util" prefix="ifx-util"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet"%>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util"%>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="theme"%>


<%-- Common setup code --%>

<portlet:defineObjects />
<theme:defineObjects />

<%
    String className = portletPreferences.getValue("className", "ch.inofix.portlet.contact.model.Contact");
    String currentURL = PortalUtil.getCurrentURL(request);
    String script = portletPreferences.getValue("script", "");
%>