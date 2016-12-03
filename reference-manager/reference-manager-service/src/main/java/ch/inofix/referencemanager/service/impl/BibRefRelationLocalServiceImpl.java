/**
 * Copyright (c) 2000-present Inofix GmbH, Luzern. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package ch.inofix.referencemanager.service.impl;

import java.util.List;

import com.liferay.portal.kernel.exception.PortalException;

import aQute.bnd.annotation.ProviderType;
import ch.inofix.referencemanager.model.BibRefRelation;
import ch.inofix.referencemanager.service.base.BibRefRelationLocalServiceBaseImpl;

/**
 * The implementation of the bib ref relation local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are
 * added, rerun ServiceBuilder to copy their definitions into the
 * {@link ch.inofix.referencemanager.service.BibRefRelationLocalService}
 * interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security
 * checks based on the propagated JAAS credentials because this service can only
 * be accessed from within the same VM.
 * </p>
 *
 * @author Christian Berndt
 * @created 2016-12-03 15:33
 * @modified 2016-12-03 15:33
 * @version 1.0.0
 * @see BibRefRelationLocalServiceBaseImpl
 * @see ch.inofix.referencemanager.service.BibRefRelationLocalServiceUtil
 */
@ProviderType
public class BibRefRelationLocalServiceImpl extends BibRefRelationLocalServiceBaseImpl {
    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Always use {@link
     * ch.inofix.referencemanager.service.BibRefRelationLocalServiceUtil} to
     * access the bib ref relation local service.
     */

    public List<BibRefRelation> getBibRefRelationsByBibliographyUuid(String bibliographyUuid) throws PortalException {

        return bibRefRelationPersistence.findByBG_BU(bibliographyUuid);

    }

    public List<BibRefRelation> getBibRefRelationsByReferenceUuid(String referenceUuid) throws PortalException {

        return bibRefRelationPersistence.findByRG_RU(referenceUuid);

    }

}