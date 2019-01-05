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

package core.xmate.db.table;

import android.database.Cursor;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import core.xmate.db.annotation.Column;
import core.xmate.db.converter.ColumnConverter;
import core.xmate.db.converter.ColumnConverterFactory;
import core.xmate.db.sqlite.ColumnDbType;
import core.xmate.util.LogUtil;

public final class ColumnEntity {

    protected final String name;
    private final String property;
    private final boolean isId;
    private final boolean isAutoId;
    private final boolean directReflectField;

    protected final IValueTransformer transformer;

    protected final Method getMethod;
    protected final Method setMethod;

    protected final Field columnField;
    protected final ColumnConverter columnConverter;

    /* package */ ColumnEntity(Class<?> entityType, Field field, Column column) {
        field.setAccessible(true);

        if (TextUtils.isEmpty(column.name())) {
            throw new IllegalArgumentException("Column name must not be null or empty!");
        }

        this.columnField = field;
        this.name = column.name();
        this.property = column.property();
        this.isId = column.isId();
        this.directReflectField = column.directReflectField();

        Class<?> fieldType = field.getType();
        this.isAutoId = this.isId && column.autoGen() && ColumnUtils.isAutoIdType(fieldType);
        this.columnConverter = ColumnConverterFactory.getColumnConverter(fieldType);

        this.getMethod = !directReflectField ? ColumnUtils.findGetMethod(entityType, field, column.name()) : null;
        if (this.getMethod != null && !this.getMethod.isAccessible()) {
            this.getMethod.setAccessible(true);
        }
        this.setMethod = !directReflectField ? ColumnUtils.findSetMethod(entityType, field, column.name()) : null;
        if (this.setMethod != null && !this.setMethod.isAccessible()) {
            this.setMethod.setAccessible(true);
        }

        Class transClz = column.hook();
        if (transClz != IValueTransformer.SIMPLE.class) {
            if (field.getType() != String.class) {
                throw new IllegalStateException("Only String column could be set a ValueTransformer");
            } else if (isId) {
                throw new IllegalStateException("Id column must not be set a ValueTransformer");
            }

            try {
                transformer = (IValueTransformer) transClz.newInstance();
            } catch (Throwable e) {
                throw new IllegalStateException("Unable to instance IValueTransformer " + transClz);
            }

        } else {
            transformer = null;
        }
    }

    public void setValueFromCursor(Object entity, Cursor cursor, int index) {
        Object value = columnConverter.getFieldValue(cursor, index);
        if (value == null) {
            value = getDefaultValue(this.columnField.getType());
        }

        if (transformer != null) {
            value = transformer.toVal(value.toString());
        }

        if (setMethod != null) {
            try {
                setMethod.invoke(entity, value);
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        } else {
            try {
                this.columnField.set(entity, value);
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        }
    }

    private Object getDefaultValue(Class type) {
        Object result = null;
        if (type.isPrimitive()) {
            if (type == Boolean.class || type == boolean.class) {
                result = false;
            } else if (type == Character.class || type == char.class) {
                result = (char) 0;
            } else if (type == Byte.class || type == byte.class) {
                result = (byte) 0;
            } else if (type == Short.class || type == short.class) {
                result = (short) 0;
            } else if (type == Integer.class || type == int.class) {
                result = 0;
            } else if (type == Long.class || type == long.class) {
                result = 0L;
            } else if (type == Float.class || type == float.class) {
                result = 0F;
            } else if (type == Double.class || type == double.class) {
                result = 0D;
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Object getColumnValue(Object entity) {
        Object fieldValue = getFieldValue(entity);
        if (this.isAutoId && (fieldValue.equals(0L) || fieldValue.equals(0))) {
            return null;
        }
        return columnConverter.fieldValue2DbValue(fieldValue);
    }

    public void setAutoIdValue(Object entity, long value) {
        Object idValue = value;
        if (ColumnUtils.isInteger(columnField.getType())) {
            idValue = (int) value;
        }

        if (setMethod != null) {
            try {
                setMethod.invoke(entity, idValue);
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        } else {
            try {
                this.columnField.set(entity, idValue);
            } catch (Throwable e) {
                LogUtil.e(e.getMessage(), e);
            }
        }
    }

    public Object getFieldValue(Object entity) {
        Object fieldValue = null;
        if (entity != null) {
            if (getMethod != null) {
                try {
                    fieldValue = getMethod.invoke(entity);
                } catch (Throwable e) {
                    LogUtil.e(e.getMessage(), e);
                }
            } else {
                try {
                    fieldValue = this.columnField.get(entity);
                } catch (Throwable e) {
                    LogUtil.e(e.getMessage(), e);
                }
            }
        }
        return fieldValue;
    }

    public String getName() {
        return name;
    }

    public String getProperty() {
        return property;
    }

    public boolean isId() {
        return isId;
    }

    public boolean isAutoId() {
        return isAutoId;
    }

    public Field getColumnField() {
        return columnField;
    }

    public ColumnConverter getColumnConverter() {
        return columnConverter;
    }

    public ColumnDbType getColumnDbType() {
        return columnConverter.getColumnDbType();
    }

    @Override
    public String toString() {
        return name;
    }
}
