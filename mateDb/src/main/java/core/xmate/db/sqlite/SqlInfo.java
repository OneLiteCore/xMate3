/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package core.xmate.db.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import core.xmate.util.KeyValue;
import core.xmate.db.converter.ColumnConverter;
import core.xmate.db.converter.ColumnConverterFactory;
import core.xmate.db.table.ColumnUtils;

import java.util.ArrayList;
import java.util.List;

public final class SqlInfo {

	private String sql;
	private List<KeyValue> bindArgs;

	public SqlInfo () {
	}

	public SqlInfo (String sql) {
		this.sql = sql;
	}

	public String getSql () {
		return sql;
	}

	public SqlInfo setSql (String sql) {
		this.sql = sql;
		return this;
	}

	public SqlInfo addBindArg (KeyValue kv) {
		if (bindArgs == null) {
			bindArgs = new ArrayList<KeyValue>();
		}
		bindArgs.add(kv);
		return this;
	}

	public SqlInfo addBindArgs (List<KeyValue> bindArgs) {
		if (this.bindArgs == null) {
			this.bindArgs = bindArgs;
		} else {
			this.bindArgs.addAll(bindArgs);
		}
		return this;
	}

	/**
	 * 添加占位符对应的参数。
	 * 原本xUtils设计的是使用{@link KeyValue}对象来添加参数的，但是如你所见，
	 * 这里其实并没有用到{@link KeyValue#key}，也就是说本质上其实就是添加Object。
	 * 这里为了兼容旧有代码添加了该方法。
	 *
	 * @param values
	 * @return
	 */
	public SqlInfo addBindArgs (Object... values) {
		if (this.bindArgs == null) {
			this.bindArgs = new ArrayList<>(values.length);
		}
		for (Object v : values) {
			this.bindArgs.add(new KeyValue(null, v));
		}
		return this;
	}

	/**
	 * 清空数据。
	 *
	 * @return
	 */
	public SqlInfo clear () {
		sql = null;
		clearBindArgs();
		return this;
	}

	/**
	 * 清空已绑定的参数。
	 *
	 * @return
	 */
	public SqlInfo clearBindArgs () {
		if (bindArgs != null) {
			bindArgs.clear();
		}
		return this;
	}

	public SQLiteStatement buildStatement (SQLiteDatabase database) {
		SQLiteStatement result = database.compileStatement(sql);
		if (bindArgs != null) {
			for (int i = 1; i < bindArgs.size() + 1; i++) {
				KeyValue kv = bindArgs.get(i - 1);
				Object value = ColumnUtils.convert2DbValueIfNeeded(kv.value);
				if (value == null) {
					result.bindNull(i);
				} else {
					ColumnConverter converter = ColumnConverterFactory.getColumnConverter(value.getClass());
					ColumnDbType type = converter.getColumnDbType();
					switch (type) {
						case INTEGER:
							result.bindLong(i, ((Number) value).longValue());
							break;
						case REAL:
							result.bindDouble(i, ((Number) value).doubleValue());
							break;
						case TEXT:
							result.bindString(i, value.toString());
							break;
						case BLOB:
							result.bindBlob(i, (byte[]) value);
							break;
						default:
							result.bindNull(i);
							break;
					} // end switch
				}
			}
		}
		return result;
	}

	public Object[] getBindArgs () {
		Object[] result = null;
		if (bindArgs != null) {
			result = new Object[bindArgs.size()];
			for (int i = 0; i < bindArgs.size(); i++) {
				result[i] = ColumnUtils.convert2DbValueIfNeeded(bindArgs.get(i).value);
			}
		}
		return result;
	}

	public String[] getBindArgsAsStrArray () {
		String[] result = null;
		if (bindArgs != null) {
			result = new String[bindArgs.size()];
			for (int i = 0; i < bindArgs.size(); i++) {
				Object value = ColumnUtils.convert2DbValueIfNeeded(bindArgs.get(i).value);
				result[i] = value == null ? null : value.toString();
			}
		}
		return result;
	}
}
