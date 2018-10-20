package com.bestvike.ocr.reflect;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.util.Assert;

import java.lang.reflect.Type;

/**
 * Created by 许崇雷 on 2017-10-10.
 */
public final class GenericTypeReference<T> extends ParameterizedTypeReference<T> {
    private final Type type;

    public GenericTypeReference(Type type) {
        super();
        Assert.notNull(type, "type can not be null");
        this.type = type;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof org.springframework.core.ParameterizedTypeReference && this.type.equals(((GenericTypeReference) obj).type);
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public String toString() {
        return "GenericTypeReference<" + this.type + ">";
    }
}
