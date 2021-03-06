/* org.agiso.tempel.Tempel (14-09-2012)
 * 
 * Tempel.java
 * 
 * Copyright 2012 agiso.org
 */
package org.agiso.tempel;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.agiso.tempel.core.ITemplateExecutor;
import org.agiso.tempel.core.ITemplateProvider;
import org.agiso.tempel.core.ITemplateRepository;
import org.agiso.tempel.core.ITemplateVerifier;
import org.agiso.tempel.core.model.Template;
import org.apache.commons.lang.LocaleUtils;

/**
 * 
 * 
 * @author <a href="mailto:kkopacz@agiso.org">Karol Kopacz</a>
 */
public class Tempel {
	private File workDir;
	private File repoDir;

	private ITemplateProvider templateProvider;
	private ITemplateVerifier templateVerifier;
	private ITemplateExecutor templateExecutor;

//	--------------------------------------------------------------------------
	public Tempel(File workDir, File repoDir) {
		this.workDir = workDir;
		this.repoDir = repoDir;
	}

//	--------------------------------------------------------------------------
	public void setTemplateProvider(ITemplateProvider templateProvider) {
		this.templateProvider = templateProvider;
	}

	public void setTemplateVerifier(ITemplateVerifier templateVerifier) {
		this.templateVerifier = templateVerifier;
	}

	public void setTemplateExecutor(ITemplateExecutor templateExecutor) {
		this.templateExecutor = templateExecutor;
	}

//	--------------------------------------------------------------------------
	/**
	 * @param cmd
	 */
	public void startTemplate(String name, Map<String, String> params) throws Exception {
		Map<String, Object> globalProperties = new HashMap<String, Object>();

		// Odczytujemy właściwości systemowe i dodajemy je do globalnej mapy właściwości
		// (będzie ona aktualizowana/uzupełniana/modifikowana w oparciu właściwości odczytane
		// z poszczególnych plików konfigu racyjnych). Oryginalne wartości parametrów syste-
		// mowych zapamiętujemy pod kluczem "SYSTEM":
		Properties systemProperties = System.getProperties();
		for(String key : systemProperties.stringPropertyNames()) {
			globalProperties.put(key.replace('.', '_'), systemProperties.getProperty(key));
		}
		globalProperties.put("SYSTEM", Collections.unmodifiableMap(new HashMap<String, Object>(globalProperties)));

		ITemplateRepository repository = templateProvider.readTemplates(globalProperties);

		Locale date_locale;
		if(globalProperties.containsKey("date_locale")) {
			date_locale = LocaleUtils.toLocale((String)globalProperties.get("date_locale"));
		} else {
			date_locale = Locale.getDefault();
		}

		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();

		if(!globalProperties.containsKey("year")) {
			globalProperties.put("year", calendar.get(Calendar.YEAR));
		}

		if(!globalProperties.containsKey("date_short")) {
			String date_short;
			if(globalProperties.containsKey("date_format_short")) {
				date_short = new SimpleDateFormat((String)globalProperties.get("date_format_short"), date_locale).format(now);
			} else {
				date_short = DateFormat.getDateInstance(DateFormat.SHORT, date_locale).format(new Date());
			}
			globalProperties.put("date_short", date_short);
		}

		if(!globalProperties.containsKey("date_medium")) {
			String date_medium;
			if(globalProperties.containsKey("date_format_medium")) {
				date_medium = new SimpleDateFormat((String)globalProperties.get("date_format_medium"), date_locale).format(now);
			} else {
				date_medium = DateFormat.getDateInstance(DateFormat.MEDIUM, date_locale).format(new Date());
			}
			globalProperties.put("date_medium", date_medium);
		}

		if(!globalProperties.containsKey("date_long")) {
			String date_long;
			if(globalProperties.containsKey("date_format_long")) {
				date_long = new SimpleDateFormat((String)globalProperties.get("date_format_long"), date_locale).format(now);
			} else {
				date_long = DateFormat.getDateInstance(DateFormat.LONG, date_locale).format(new Date());
			}
			globalProperties.put("date_long", date_long);
		}

		if(!globalProperties.containsKey("date_full")) {
			String date_full;
			if(globalProperties.containsKey("date_format_full")) {
				date_full = new SimpleDateFormat((String)globalProperties.get("date_format_full"), date_locale).format(now);
			} else {
				date_full = DateFormat.getDateInstance(DateFormat.FULL, date_locale).format(new Date());
			}
			globalProperties.put("date_full", date_full);
		}

		// Pobieranie definicji szablonu do użycia:
		Template template = repository.get(name);

		// Weryfikowanie definicji szablonu, szablonu nadrzędnego i wszystkich
		// szablonów używanych. Sprawdzanie dostępność klas silników generatorów.
		templateVerifier.verifyTemplate(template, repository);

		// Uruchamianie procesu generacji w oparciu o wskazany szablon:
		templateExecutor.executeTemplate(workDir.getCanonicalPath(), repoDir.getCanonicalPath(),
				template, repository,
				Collections.unmodifiableMap(globalProperties)
		);
	}
}
