/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect.identity;

import org.hibernate.MappingException;

public class HANAIdentityColumnSupport extends IdentityColumnSupportImpl {

	@Override
	public boolean supportsIdentityColumns() {
		return true;
	}

	@Override
	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
		return "select current_identity_value() from " + table;
	}
	
	@Override
	public String getIdentityColumnString(int type) {
		// implicitly start with 1 increment by 1
		return "generated by default as identity";
	}
	
}
