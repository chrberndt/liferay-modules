package ch.inofix.portlet.newsletter.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.inofix.portlet.newsletter.model.Subscriber;
import ch.inofix.portlet.newsletter.security.permission.ActionKeys;
import ch.inofix.portlet.newsletter.service.SubscriberLocalServiceUtil;
import ch.inofix.portlet.newsletter.service.base.SubscriberServiceBaseImpl;
import ch.inofix.portlet.newsletter.service.permission.SubscriberPermission;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.SimpleFacet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.FormattedName;
import ezvcard.property.Gender;
import ezvcard.property.Member;
import ezvcard.property.StructuredName;
import ezvcard.property.Uid;

/**
 * The implementation of the subscriber remote service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.portlet.newsletter.service.SubscriberService} interface.
 *
 * <p>
 * This is a remote service. Methods of this service are expected to have
 * security checks based on the propagated JAAS credentials because this service
 * can be accessed remotely.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-10-09 21:10
 * @modified 2016-10-21 20:00
 * @version 1.0.5
 * @see ch.inofix.portlet.newsletter.service.base.SubscriberServiceBaseImpl
 * @see ch.inofix.portlet.newsletter.service.SubscriberServiceUtil
 */
public class SubscriberServiceImpl extends SubscriberServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     * 
     * Never reference this interface directly. Always use {@link
     * ch.inofix.portlet.newsletter.service.SubscriberServiceUtil} to access the
     * subscriber remote service.
     */

    @Override
    public Hits search(long groupId, SearchContext searchContext, int start,
            int end) throws PortalException, SystemException {

        return subscriberLocalService
                .search(groupId, searchContext, start, end);
    }

    @Override
    public Subscriber createSubscriber() throws PortalException,
            SystemException {

        // Create an empty subscriber - no permission check required
        return SubscriberLocalServiceUtil.createSubscriber(0);

    }

    private Subscriber documentToSubscriber(Document document)
            throws PortalException, SystemException {

        Subscriber subscriber = createSubscriber();

        VCard vCard = Ezvcard.parse(document.get(Field.CONTENT)).first();

        String firstname = null;
        String genderStr = null;
        String lastname = null;
        String name = null;
        String vCardUID = null;

        FormattedName formattedName = vCard.getFormattedName();
        if (formattedName != null) {
            name = formattedName.getValue();
        }
        
        Gender gender = vCard.getGender(); 
        if (gender != null) {
            genderStr = gender.getGender();
        }

        StructuredName structuredName = vCard.getStructuredName();
        if (structuredName != null) {
            firstname = structuredName.getGiven();
            lastname = structuredName.getFamily();
        }
        
        Uid uid = vCard.getUid(); 
        if (uid != null) {
            vCardUID = uid.getValue();
        }

        subscriber.setCreateDate(new Date(GetterUtil.getLong(document
                .get("createDate_sortable"))));
        subscriber.setEmail(document.get("email"));
        subscriber.setFirstname(firstname);
        subscriber.setGender(genderStr);
        subscriber.setLastname(lastname);
        // TODO: set middlename (from additionalNames)
        subscriber.setModifiedDate(new Date(GetterUtil.getLong(document
                .get("modified_sortable"))));
        subscriber.setName(name);
        // TODO: set title (from prefixes)
        subscriber.setVCardUID(vCardUID);

        return subscriber;

    }

    private Document getDocument(long groupId, SearchContext searchContext,
            String vCardUID) throws PortalException, SystemException {

        searchContext.setAttribute("vCardUID", vCardUID);
        Facet simpleFacet = new SimpleFacet(searchContext);
        simpleFacet.setFieldName("vCardUID");
        searchContext.addFacet(simpleFacet);

        Hits hits = search(groupId, searchContext, 0, 1);

        if (hits.getLength() > 0) {
            return hits.doc(0);
        } else {
            return null;
        }

    }

    @Override
    public Subscriber getSubscriber(long subscriberId) throws PortalException,
            SystemException {

        SubscriberPermission.check(getPermissionChecker(), subscriberId,
                ActionKeys.VIEW);

        return SubscriberLocalServiceUtil.getSubscriber(subscriberId);

    }

    public Subscriber getSubscriber(long groupId, SearchContext searchContext,
            String vCardUID) throws PortalException, SystemException {

        Document document = getDocument(groupId, searchContext, vCardUID);

        Subscriber subscriber = documentToSubscriber(document);

        return subscriber;

    }

    @Override
    public List<Subscriber> getSubscribersFromVCardGroup(long companyId,
            long groupId, String vCardGroupId, int start, int end)
            throws PortalException, SystemException {

        SearchContext searchContext = new SearchContext();

        searchContext.setCompanyId(companyId);
        searchContext.setEnd(end);
        searchContext.setEntryClassNames(SearchEngineUtil.getEntryClassNames());

        Document group = getDocument(groupId, searchContext, vCardGroupId);
        List<Subscriber> subscribers = new ArrayList<Subscriber>();

        if (group != null) {

            String content = group.get(Field.CONTENT);

            VCard vCard = Ezvcard.parse(content).first();

            if (vCard != null) {
                List<Member> members = vCard.getMembers();

                for (Member member : members) {

                    String uri = member.getUri();
                    String[] tokens = uri.split(":");
                    String uuid = null;
                    if (tokens.length > 2) {
                        uuid = tokens[2];
                    }

                    if (uuid != null) {

                        Document document = getDocument(groupId, searchContext,
                                uuid);
                        if (document != null) {

                            // Only add members with email-address

                            String email = document.get("email");
                            if (Validator.isNotNull(email)) {

                                Subscriber subscriber = documentToSubscriber(document);

                                subscribers.add(subscriber);
                            }
                        }
                    }
                }
            }
        }

        if (end > subscribers.size()) {
            end = subscribers.size();
        }

        return subscribers.subList(start, end);
    }

    @Override
    public int getSubscribersFromVCardGroupCount(long companyId, long groupId,
            String vCardGroupId) throws PortalException, SystemException {

        List<Subscriber> subscribers = getSubscribersFromVCardGroup(companyId,
                groupId, vCardGroupId, 0, Integer.MAX_VALUE);

        return subscribers.size();

    }

    private static Log _log = LogFactoryUtil.getLog(SubscriberServiceImpl.class
            .getName());
}
