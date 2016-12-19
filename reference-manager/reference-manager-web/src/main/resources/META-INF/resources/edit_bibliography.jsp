<%--
    edit_bibliography.jsp: default view of the bibliography manaager portlet.
    
    Created:    2016-11-30 00:18 by Christian Berndt
    Modified:   2016-12-20 00:17 by Christian Berndt
    Version:    1.0.5
--%>

<%@ include file="/init.jsp"%>

<%
    String redirect = ParamUtil.getString(request, "redirect");
    String tabNames = "settings"; 
    String tabs1 = ParamUtil.getString(request, "tabs1", "settings");
    
    Bibliography bibliography = (Bibliography) request.getAttribute(BibliographyWebKeys.BIBLIOGRAPHY);

    portletURL.setParameter("mvcPath", "/edit_bibliography.jsp");

    boolean hasUpdatePermission = true;

    if (bibliography != null) {
        BibliographyPermission.contains(permissionChecker, bibliography, BibliographyActionKeys.UPDATE);
        tabNames = "browse,import-export,settings";
        portletURL.setParameter("bibliographyId", String.valueOf(bibliography.getBibliographyId()));
        tabs1 = ParamUtil.getString(request, "tabs1", "browse");
        AssetEntryServiceUtil.incrementViewCounter(Bibliography.class.getName(), bibliography.getBibliographyId());
    }
%>

<c:choose>
    <c:when test="<%=Validator.isNull(bibliography)%>">
        <div class="bibliography-head">
            <h2>
                <liferay-ui:message key="create-a-new-bibliography" />
            </h2>
            
            <c:choose>
                <c:when test="<%= themeDisplay.isSignedIn() %>">
                    <p>
                        <liferay-ui:message key="you-can-import-your-references-from-a-file-or-pick-references-already-available-on-bibshare" />
                    </p>                    
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">
                        <liferay-ui:message key="you-must-sign-in-order-to-create-a-new-bibliography"/>
                        
                        <strong>
                            <aui:a href="<%= themeDisplay.getURLSignIn() %>" label="sign-in">
                                <liferay-ui:icon iconCssClass="icon-signin"/>
                            </aui:a>
                        </strong> 
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>
    <c:otherwise>
        <div class="bibliography-head">
            <h2><%=bibliography.getTitle()%></h2>
            <div class="compiled-by">
                <liferay-ui:message key="compiled-by-x" arguments="<%= new String[] {bibliography.getUserName()} %>"/>
            </div>
            <a class="add-reference btn btn-success" href="#"><liferay-ui:message key="add-reference"/></a>
        </div>
    </c:otherwise>
</c:choose>

<liferay-ui:tabs names="<%= tabNames %>" param="tabs1"
    url="<%=portletURL.toString()%>" />
    
<c:choose>

    <c:when test='<%= tabs1.equals("import-export") %>'>
        <liferay-util:include page="/import_bibliography.jsp" servletContext="<%= application %>" />
    </c:when>
    
   <c:when test='<%= tabs1.equals("settings") %>'>
       <liferay-util:include page="/bibliography_settings.jsp" servletContext="<%= application %>" />
   </c:when>        

    <c:otherwise> 
       <liferay-util:include page="/bibliography_entries.jsp" servletContext="<%= application %>" />     
    </c:otherwise>
</c:choose>
