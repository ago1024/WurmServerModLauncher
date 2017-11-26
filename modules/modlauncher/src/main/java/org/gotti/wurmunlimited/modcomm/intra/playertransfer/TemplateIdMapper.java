package org.gotti.wurmunlimited.modcomm.intra.playertransfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gotti.wurmunlimited.modsupport.IdFactory;
import org.gotti.wurmunlimited.modsupport.IdType;

/**
 * Map local to remote templates
 */
public class TemplateIdMapper {

	// Template map
	private final Map<Integer, Integer> templateMap;

	/**
	 * Create
	 * @param templateMap template map
	 */
	private TemplateIdMapper(Map<Integer, Integer> templateMap) {
		this.templateMap = templateMap;
	}

	/**
	 * Is the template a regular template
	 * @param type Template type
	 * @param templateId Template id
	 * @return true if the template is a stock server template
	 */
	public static boolean isRegularTemplate(IdType type, int templateId) {
		return templateId < type.getLastUsedId();
	}

	/**
	 * Test if the template can transfer. I.e. it is available on the remote server
	 * @param templateId Template id
	 * @return true if the template is available on the remote server
	 */
	public boolean willTemplateLeaveServer(int templateId) {
		return templateMap.get(templateId) != null;
	}

	/**
	 * Get the remote template id
	 * @param localTemplateId Local template id
	 * @return Remote template id
	 */
	public int getRemoteTemplate(int localTemplateId) {
		Integer remoteId = templateMap.get(localTemplateId);
		if (remoteId == null) {
			ModPlayerTransfer.LOG.warning("Trying item transfer with not transferrable item " + localTemplateId);
			return localTemplateId;
		}
		return remoteId;
	}

	/**
	 * Builde.
	 */
	public static class Builder {
		private final Map<String, Integer> localMap = new HashMap<>();
		private final Map<Integer, Integer> templateMap = new HashMap<>();

		/**
		 * Create builder
		 * @param type Template type
		 */
		public Builder(IdType type) {
			for (Entry<String, Integer> entry : IdFactory.getIdsFor(type)) {
				localMap.put(entry.getKey(), entry.getValue());
			}
		}

		/**
		 * Add a template
		 * @param name Template name
		 * @param remoteId Template id
		 */
		public void addRemoteTemplate(String name, int remoteId) {
			Integer localId = localMap.get(name);
			if (localId != null) {
				templateMap.put(localId, remoteId);
			}
		}

		/**
		 * Build the {@link TemplateIdMapper}
		 * @return {@link TemplateIdMapper}
		 */
		public TemplateIdMapper build() {
			return new TemplateIdMapper(templateMap);
		}
	}

}
