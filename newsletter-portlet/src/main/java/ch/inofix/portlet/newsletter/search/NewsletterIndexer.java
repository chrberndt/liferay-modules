package ch.inofix.portlet.newsletter.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.portlet.PortletURL;

import ch.inofix.portlet.newsletter.model.Newsletter;
import ch.inofix.portlet.newsletter.service.NewsletterLocalServiceUtil;
import ch.inofix.portlet.newsletter.service.persistence.NewsletterActionableDynamicQuery;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngineUtil;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.util.GetterUtil;

/**
 * @author Christian Berndt
 * @created 2016-10-15 23:14
 * @modified 2016-10-15 23:14
 * @version 1.0.0
 */
public class NewsletterIndexer extends BaseIndexer {

    public NewsletterIndexer() {
        setPermissionAware(true);
    }

    @Override
    public String[] getClassNames() {
        return CLASS_NAMES;
    }

    @Override
    public String getPortletId() {
        return PORTLET_ID;
    }

    @Override
    protected void doDelete(Object obj) throws Exception {

        Newsletter newsletter = (Newsletter) obj;

        deleteDocument(newsletter.getCompanyId(), newsletter.getNewsletterId());

    }

    @Override
    protected Document doGetDocument(Object obj) throws Exception {

        Newsletter newsletter = (Newsletter) obj;

        Document document = getBaseModelDocument(PORTLET_ID, newsletter);
        document.addText(Field.TITLE, newsletter.getTitle());

        return document;
    }

    @Override
    protected Summary doGetSummary(Document document, Locale locale,
            String snippet, PortletURL portletURL) throws Exception {

        Summary summary = createSummary(document);

        summary.setMaxContentLength(200);

        return summary;
    }

    @Override
    protected void doReindex(Object obj) throws Exception {

        Newsletter newsletter = (Newsletter) obj;

        Document document = getDocument(newsletter);

        SearchEngineUtil.updateDocument(getSearchEngineId(),
                newsletter.getCompanyId(), document);
    }

    @Override
    protected void doReindex(String[] ids) throws Exception {

        long companyId = GetterUtil.getLong(ids[0]);

        reindexEntries(companyId);
    }

    @Override
    protected void doReindex(String className, long classPK) throws Exception {

        Newsletter newsletter = NewsletterLocalServiceUtil
                .getNewsletter(classPK);

        doReindex(newsletter);
    }

    @Override
    protected String getPortletId(SearchContext searchContext) {
        return PORTLET_ID;
    }

    protected void reindexEntries(long companyId) throws PortalException,
            SystemException {

        final Collection<Document> documents = new ArrayList<Document>();

        ActionableDynamicQuery actionableDynamicQuery = new NewsletterActionableDynamicQuery() {

            @Override
            protected void addCriteria(DynamicQuery dynamicQuery) {

            }

            @Override
            protected void performAction(Object object) throws PortalException {

                Newsletter newsletter = (Newsletter) object;

                Document document = getDocument(newsletter);

                documents.add(document);

            }

        };

        // TODO: Check Liferay-Indexer for a more recent implementation
        actionableDynamicQuery.setCompanyId(companyId);

        actionableDynamicQuery.performActions();

        SearchEngineUtil.updateDocuments(getSearchEngineId(), companyId,
                documents);

    }

    private static final Log _log = LogFactoryUtil
            .getLog(NewsletterIndexer.class.getName());

    public static final String[] CLASS_NAMES = { Newsletter.class.getName() };

    public static final String PORTLET_ID = "newsletter";

}