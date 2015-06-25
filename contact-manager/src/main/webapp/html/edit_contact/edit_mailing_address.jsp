<%--
    edit_mailing_address.jsp: Edit the contact's mailing addresses. 
    
    Created:    2015-05-11 18:30 by Christian Berndt
    Modified:   2015-06-25 14:43 by Christian Berndt
    Version:    1.0.8
--%>

<%@ include file="/html/edit_contact/init.jsp"%>

<%-- Import required classes --%>

<%@page import="ch.inofix.portlet.contact.dto.AddressDTO"%>

<%@page import="ezvcard.parameter.AddressType"%>

<%
	// TODO: make the list of address-types configurable
	String[] addressTypes = new String[] { "other",
			// AddressType.DOM.getValue(),       // Not longer supported in v.4.0
			AddressType.HOME.getValue(),
			// AddressType.INTL.getValue(),      // Not longer supported in v.4.0
			// AddressType.PARCEL.getValue(),    // Not longer supported in v.4.0
			// AddressType.POSTAL.getValue(),    // Not longer supported in v.4.0
			AddressType.WORK.getValue() };
%>


<aui:fieldset id="address" cssClass="address">
<%
    for (AddressDTO address : contact_.getAddresses()) {
%>
    <aui:container>

        <aui:row>
            <div class="lfr-form-row">
 
 
			    <legend class="fieldset-legend">
			    
                    <span class="sort-handle"></span>
			    
			        <span class="legend"><liferay-ui:message
			                key="<%=address.getType()%>" /></span>
			    </legend>           
            
                <div class="row-fields">
                                                    
                    <aui:col span="6">
                        <aui:input name="address.streetAddress" type="textarea"
                            label="street" cssClass="address"
                            value="<%=address.getStreetAddress()%>"
                            helpMessage="address.street-address-help"
                            disabled="<%=!hasUpdatePermission%>" />
                        <aui:input name="address.poBox" value="<%=address.getPoBox()%>"
                            label="po-box" helpMessage="address.po-box-help"
                            disabled="<%=!hasUpdatePermission%>" />
                        <aui:select name="address.type" label="type"
                            helpMessage="address.type-help"
                            disabled="<%=!hasUpdatePermission%>">
                            <%
                                for (String type : addressTypes) {
                            %>
                            <aui:option value="<%=type%>" label="<%=type%>"
                                selected="<%=type.equalsIgnoreCase(address
                                                .getType())%>" />
                            <%
                                }
                            %>
                        </aui:select>
                    </aui:col>
                    <aui:col span="6">
                        <aui:input name="address.locality" label="city"
                            value="<%=address.getLocality()%>"
                            helpMessage="address.locality-help"
                            disabled="<%=!hasUpdatePermission%>" />
                        <aui:input name="address.postalCode" label="postal-code"
                            value="<%=address.getPostalCode()%>"
                            helpMessage="address.postal-code-help"
                            disabled="<%=!hasUpdatePermission%>" />
                        <aui:input name="address.region" label="region"
                            value="<%=address.getRegion()%>"
                            helpMessage="address.region-help"
                            disabled="<%=!hasUpdatePermission%>" />
                        <aui:input name="address.country" label="country"
                            value="<%=address.getCountry()%>"
                            helpMessage="address.country-help"
                            disabled="<%=!hasUpdatePermission%>" />
                    </aui:col>
                </div>
            </div>
        </aui:row>

    </aui:container>
<%
    }
%>
</aui:fieldset>


<%-- Configure auto-fields --%>
<aui:script use="liferay-auto-fields">

    var addressAutoFields = new Liferay.AutoFields({
        contentBox : 'fieldset#<portlet:namespace />address',
        namespace : '<portlet:namespace />',
        sortable : true,
        sortableHandle: '.sort-handle',
        on : {
            'clone' : function(event) {
                restoreOriginalNames(event);
            }
        }
    }).render();
    
</aui:script>
