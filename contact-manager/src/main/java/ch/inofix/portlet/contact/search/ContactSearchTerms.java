package ch.inofix.portlet.contact.search;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.dao.search.DAOParamUtil;

/**
 * 
 * @author Christian Berndt
 * @created 2015-05-24 22:14
 * @modified 2015-05-27 11:45
 * @version 1.0.3
 *
 */
public class ContactSearchTerms extends ContactDisplayTerms {

	public ContactSearchTerms(PortletRequest portletRequest) {
		super(portletRequest);

		company = DAOParamUtil.getString(portletRequest, COMPANY);
		contactId = DAOParamUtil.getString(portletRequest, CONTACT_ID);
		createDate = DAOParamUtil.getString(portletRequest, CREATE_DATE);
		email = DAOParamUtil.getString(portletRequest, EMAIL);
		fax = DAOParamUtil.getString(portletRequest, FAX);
		fullName = DAOParamUtil.getString(portletRequest, FULL_NAME);
		// TODO: set default impp
		modifiedDate = DAOParamUtil.getString(portletRequest, MODIFIED_DATE);
		name = DAOParamUtil.getString(portletRequest, NAME);
		phone = DAOParamUtil.getString(portletRequest, PHONE);
		userName = DAOParamUtil.getString(portletRequest, USER_NAME);
	}

}
