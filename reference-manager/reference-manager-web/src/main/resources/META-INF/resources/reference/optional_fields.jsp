<%--
    reference/optional_fields.jsp: the optional-fields tab of the reference editor.
    
    Created:    2016-12-25 19:24 by Christian Berndt
    Modified:   2016-12-25 20:32 by Christian Berndt
    Version:    1.0.1
--%>

<%@ include file="/init.jsp"%>

<%
    Reference reference = (Reference) request.getAttribute("reference");
    JSONObject entryFields = (JSONObject) request.getAttribute("reference.entryFields");
    boolean hasUpdatePermission = (Boolean) request.getAttribute("reference.hasUpdatePermission");
%>
    
<aui:fieldset cssClass="optional-fields">

<%
    JSONArray optionalFields = entryFields.getJSONArray("optional");  

    for (int i=0; i<optionalFields.length(); i++) {
        JSONObject field = optionalFields.getJSONObject(i);
        String name = field.getString("name");
        String helpKey = field.getString("help");
        if (Validator.isNull(helpKey)) {
            helpKey = name + "-help"; 
        }                    
        String value = reference.getFields().get(name); 
%>
    <aui:input name="name" type="hidden" value="<%= name %>"/>           
    <aui:input disabled="<%= !hasUpdatePermission %>" helpMessage="<%= helpKey %>" label="<%= name %>" name="value" value="<%= value %>"/>
<%
    }
%>

</aui:fieldset>