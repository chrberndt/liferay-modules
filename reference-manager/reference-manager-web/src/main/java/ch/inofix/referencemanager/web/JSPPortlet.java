/**
 * Copyright 2016-present Inofix GmbH, Luzern
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.inofix.referencemanager.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.fileupload.FileUploadBase;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXParser;

import org.osgi.service.component.annotations.Component;
//import org.osgi.service.component.annotations.Reference;

import ch.inofix.referencemanager.model.Reference;
import ch.inofix.referencemanager.service.ReferenceLocalService;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.FileSizeException;

@Component(immediate = true, property = {
    "com.liferay.portlet.display-category=category.osgi",
    "com.liferay.portlet.instanceable=true",
    "javax.portlet.security-role-ref=power-user,user",
    "javax.portlet.init-param.template-path=/",
    "javax.portlet.init-param.view-template=/view.jsp",
    "javax.portlet.resource-bundle=content.Language"
}, service = Portlet.class)
/**
 * @author Christian Berndt
 * @version 0.1.1
 */
public class JSPPortlet extends MVCPortlet {

    private static Log _log = LogFactoryUtil.getLog(JSPPortlet.class.getName());

    @Override
    public void processAction(
        ActionRequest actionRequest, ActionResponse actionResponse)
        throws IOException, PortletException {

        try {
            String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
            String tabs1 = ParamUtil.getString(actionRequest, "tabs1");

            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                updateReference(actionRequest);
            }
            else if (cmd.equals("importBibtexFile")) {
                importBibtexFile(actionRequest);
            }
            else if (cmd.equals(Constants.DELETE)) {
                deleteReference(actionRequest);
            }

            if (Validator.isNotNull(cmd)) {
                if (SessionErrors.isEmpty(actionRequest)) {
                    SessionMessages.add(actionRequest, "requestProcessed");
                }

                actionResponse.setRenderParameter("tabs1", tabs1);

            }
        }
        catch (Exception e) {
            throw new PortletException(e);
        }
    }

    @Override
    public void render(RenderRequest request, RenderResponse response)
        throws IOException, PortletException {

        // set service bean
        request.setAttribute(
            "referenceLocalService", getReferenceLocalService());

        super.render(request, response);
    }

    protected void deleteReference(ActionRequest actionRequest)
        throws Exception {

        long referenceId = ParamUtil.getLong(actionRequest, "referenceId");

        getReferenceLocalService().deleteReference(referenceId);
    }

    protected void importBibtexFile(ActionRequest actionRequest)
        throws Exception {

        _log.info("Executing importBibtexFile().");

        UploadPortletRequest uploadPortletRequest =
            PortalUtil.getUploadPortletRequest(actionRequest);

        String sourceFileName = uploadPortletRequest.getFileName("file");

        StringBundler sb = new StringBundler(5);

        String extension = FileUtil.getExtension(sourceFileName);

        if (Validator.isNotNull(extension)) {
            sb.append(StringPool.PERIOD);
            sb.append(extension);
        }

        InputStream inputStream = null;

        try {

            inputStream = uploadPortletRequest.getFileAsStream("file");

            BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            BibTeXParser bibtexParser = new BibTeXParser();

            BibTeXDatabase database = bibtexParser.parse(bufferedReader);

            String contentType = uploadPortletRequest.getContentType("file");

            _log.info("sourceFileName = " + sourceFileName);
            _log.info("extension = " + extension);
            _log.info("contentType = " + contentType);

            _log.info("database.getEntries().size() = " +
                database.getEntries().size());

        }
        catch (Exception e) {
            UploadException uploadException =
                (UploadException) actionRequest.getAttribute(WebKeys.UPLOAD_EXCEPTION);

            if ((uploadException != null) &&
                (uploadException.getCause() instanceof FileUploadBase.IOFileUploadException)) {

                // Cancelled a temporary upload

            }
            else if ((uploadException != null) &&

            uploadException.isExceededSizeLimit()) {

                throw new FileSizeException(uploadException.getCause());
            }
            else {
                throw e;
            }
        }
        finally {
            StreamUtil.cleanUp(inputStream);
        }

    }

    protected void updateReference(ActionRequest actionRequest)
        throws Exception {

        long referenceId = ParamUtil.getLong(actionRequest, "referenceId");

        String bibtex = ParamUtil.getString(actionRequest, "bibtex");

        if (referenceId <= 0) {
            Reference reference = getReferenceLocalService().createReference(0);

            reference.setBibtex(bibtex);

            reference.isNew();
            getReferenceLocalService().addReferenceWithoutId(reference);
        }
        else {
            Reference reference =
                getReferenceLocalService().fetchReference(referenceId);
            reference.setReferenceId(referenceId);
            reference.setBibtex(bibtex);

            getReferenceLocalService().updateReference(reference);
        }
    }

    public ReferenceLocalService getReferenceLocalService() {

        return _referenceLocalService;
    }

    @org.osgi.service.component.annotations.Reference
    public void setReferenceLocalService(
        ReferenceLocalService referenceLocalService) {

        this._referenceLocalService = referenceLocalService;
    }

    private ReferenceLocalService _referenceLocalService;

}
