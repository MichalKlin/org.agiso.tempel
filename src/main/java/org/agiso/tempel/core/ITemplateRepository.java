/* org.agiso.tempel.core.ITemplateRepository (29-10-2012)
 * 
 * ITemplateRepository.java
 * 
 * Copyright 2012 agiso.org
 */
package org.agiso.tempel.core;

import org.agiso.tempel.core.model.Repository;
import org.agiso.tempel.core.model.Template;
import org.agiso.tempel.core.model.Template.Scope;

/**
 * 
 * 
 * @author <a href="mailto:kkopacz@agiso.org">Karol Kopacz</a>
 */
public interface ITemplateRepository {
	/**
	 * @param scope
	 * @param repository
	 */
	public void setRepository(Scope scope, Repository repository);

	/**
	 * @param key
	 * @param template
	 */
	public void put(String key, Template template);

	/**
	 * @param key
	 * @return
	 */
	public Template get(String key);

	/**
	 * @param groupId
	 * @param templateId
	 * @param version
	 * @param template
	 */
	public void put(String groupId, String templateId, String version, Template template);

	/**
	 * @param groupId
	 * @param templateId
	 * @param version
	 * @return
	 */
	public Template get(String groupId, String templateId, String version);
}
