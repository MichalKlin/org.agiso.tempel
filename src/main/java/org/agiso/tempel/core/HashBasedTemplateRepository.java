/* org.agiso.tempel.core.HashBasedTemplateRepository (29-10-2012)
 * 
 * HashBasedTemplateRepository.java
 * 
 * Copyright 2012 agiso.org
 */
package org.agiso.tempel.core;

import java.util.HashMap;
import java.util.Map;

import org.agiso.tempel.core.model.Repository;
import org.agiso.tempel.core.model.Template;
import org.agiso.tempel.core.model.Template.Scope;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * 
 * 
 * @author <a href="mailto:kkopacz@agiso.org">Karol Kopacz</a>
 */
public class HashBasedTemplateRepository implements ITemplateRepository {
	private Map<Scope, Repository> rMap;

	// key -> template
	private Map<String, Template> kMap;

	// groupId, templateId, version -> Template
	private Table<String, String, Map<String, Template>> gtvTable;

//	--------------------------------------------------------------------------
	public HashBasedTemplateRepository() {
		rMap = new HashMap<Template.Scope, Repository>();
		kMap = new HashMap<String, Template>();
		gtvTable = HashBasedTable.create();
	}

//	--------------------------------------------------------------------------
	@Override
	public void setRepository(Scope scope, Repository repository) {
		// Przeglądamy wszystkie szablony i ustawiamy repozytorium dla tych,
		// które są przyporządkowane do zadanego zasięgu:
		for(Map<String, Template> row : gtvTable.values()) {
			for(Template template: row.values()) {
				if(scope.equals(template.getScope())) {
					template.setRepository(repository);
				}
			}
		}

		// Zapamiętujemy repozytorium dla zakresu:
		rMap.put(scope, repository);
	}

	@Override
	public void put(String key, Template template) {
		if(kMap.containsKey(key)) {
			throw new IllegalStateException("Powtórzony klucz szablonu: " + key);
		}
		kMap.put(key, template);

		template.setRepository(rMap.get(template.getScope()));
	}

	@Override
	public Template get(String key) {
		return kMap.get(key);
	}

	@Override
	public void put(String groupId, String templateId, String version, Template template) {
		Map<String, Template> vMap = gtvTable.get(groupId, templateId);
		if(vMap == null) {
			vMap = new HashMap<String, Template>();
			gtvTable.put(groupId, templateId, vMap);
		} else if(vMap.containsKey(version)) {
			throw new IllegalStateException("Powtórzona definicja szablonu " + groupId + ":" + templateId + ":" + version);
		}
		vMap.put(version, template);

		template.setRepository(rMap.get(template.getScope()));
	}

	@Override
	public Template get(String groupId, String templateId, String version) {
		Map<String, Template> vMap = gtvTable.get(groupId, templateId);
		return vMap == null? null : vMap.get(version);
	}
}
