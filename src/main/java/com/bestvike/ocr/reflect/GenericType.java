package com.bestvike.ocr.reflect;

import com.fasterxml.jackson.databind.util.ArrayIterator;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * 泛型类型
 * Created by 许崇雷 on 2017-10-18.
 */
@SuppressWarnings("unused")
public abstract class GenericType<T> implements ParameterizedType {
    private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
    private final Type[] actualTypeArguments;
    private final Class<?> rawType;
    private final Type ownerType;

    public GenericType() {
        ParameterizedType superClass = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type type = superClass.getActualTypeArguments()[0];
        if (type instanceof Class<?>) {
            this.actualTypeArguments = EMPTY_TYPE_ARRAY;
            this.rawType = (Class<?>) type;
            this.ownerType = null;
        } else if (type instanceof ParameterizedTypeImpl) {
            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) type;
            this.actualTypeArguments = parameterizedType.getActualTypeArguments();
            this.rawType = parameterizedType.getRawType();
            this.ownerType = parameterizedType.getOwnerType();
        } else {
            throw new RuntimeException("type must be instance of Class or ParameterizedTypeImpl.");
        }
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.actualTypeArguments;
    }

    @Override
    public Class<?> getRawType() {
        return this.rawType;
    }

    @Override
    public Type getOwnerType() {
        return this.ownerType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof GenericType))
            return false;
        GenericType that = (GenericType) obj;
        return Arrays.equals(this.actualTypeArguments, that.actualTypeArguments)
                && Objects.equals(this.rawType, that.rawType)
                && Objects.equals(this.ownerType, that.ownerType);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.actualTypeArguments == null ? 0 : Arrays.hashCode(this.actualTypeArguments));
        result = prime * result + (this.rawType == null ? 0 : this.rawType.hashCode());
        result = prime * result + (this.ownerType == null ? 0 : this.ownerType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.ownerType != null) {
            if (this.ownerType instanceof Class)
                builder.append(((Class) this.ownerType).getName());
            else
                builder.append(this.ownerType.toString());
            builder.append(".");
            if (this.ownerType instanceof ParameterizedTypeImpl)
                builder.append(this.rawType.getName().replace(((ParameterizedTypeImpl) this.ownerType).getRawType().getName() + "$", ""));
            else
                builder.append(this.rawType.getName());
        } else {
            builder.append(this.rawType.getName());
        }

        if (this.actualTypeArguments != null && this.actualTypeArguments.length > 0) {
            builder.append("<");
            Iterator<Type> iterator = new ArrayIterator<>(this.actualTypeArguments);
            if (iterator.hasNext()) {
                builder.append(iterator.next().getTypeName());
                while (iterator.hasNext()) {
                    builder.append(", ");
                    builder.append(iterator.next().getTypeName());
                }
            }
            builder.append(">");
        }

        return builder.toString();
    }
}
