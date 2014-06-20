package nl.viking.db.hibernate.strategy

import nl.viking.Conf
import org.hibernate.cfg.ImprovedNamingStrategy

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 7/15/13
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
class VikingNamingStrategy extends ImprovedNamingStrategy {
	private static final long serialVersionUID = 1L;

	@Override
	public String classToTableName(final String className) {
		return this.addPrefix(super.classToTableName(className));
	}

	@Override
	public String collectionTableName(final String ownerEntity,
									  final String ownerEntityTable, final String associatedEntity,
									  final String associatedEntityTable, final String propertyName) {
		return this.addPrefix(super.collectionTableName(ownerEntity,
				ownerEntityTable, associatedEntity, associatedEntityTable,
				propertyName));
	}

	@Override
	public String logicalCollectionTableName(final String tableName,
											 final String ownerEntityTable, final String associatedEntityTable,
											 final String propertyName) {
		return this.addPrefix(super.logicalCollectionTableName(tableName,
				ownerEntityTable, associatedEntityTable, propertyName));
	}

	private String addPrefix(final String composedTableName) {
		String prefix = Conf.properties.hibernate.prefix != null ? Conf.properties.hibernate.prefix : "vk_"
		return prefix + composedTableName;
	}
}
