/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package br.org.indt.ndg.server.language;

import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.org.indt.ndg.server.pojo.Language;
import br.org.indt.ndg.server.language.LanguageManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public @Stateless
class LanguageManagerBean implements LanguageManager {

	@PersistenceContext(name = "MobisusPersistence")
	private EntityManager manager;

    private static Log log = LogFactory.getLog(LanguageManagerBean.class);

	public LanguageManagerBean() {}

    @Override
    public HashMap<String, String> getLanguageList() {
        Query q = manager.createNamedQuery("languages.getLanguageList");

        HashMap<String, String> list = new HashMap<String, String>();

        List<Language> listOfLanguages = q.getResultList();

        for( Language language : listOfLanguages ) {
            list.put(language.getName(), language.getLocaleString());
        }

		return list;
	}

    @Override
    public String getLanguagePath(String locale) {
        Query q = manager.createNamedQuery("languages.getPath");
        q.setParameter("locale", locale + "%");

        Language language = (Language) q.getSingleResult();

        return language.getPath();
    }

    @Override
    public String getFontPath(String locale) {
        Query q = manager.createNamedQuery("languages.getPath");
        q.setParameter("locale", locale + "%");

        Language language = (Language) q.getSingleResult();

        return language.getFontPath();
    }
}
