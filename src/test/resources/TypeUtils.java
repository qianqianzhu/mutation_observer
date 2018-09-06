/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

/**
 * <p> Utility methods focusing on type inspection, particularly with regard to
 * generics. </p>
 *
 * @since 3.0
 */
public class TypeUtils {

    /**
     * {@link WildcardType} builder.
     * @since 3.2
     */
    public static class WildcardTypeBuilder implements Builder<WildcardType> {
        /**
         * Constructor
         */
        private WildcardTypeBuilder() {
        }

        private Type[] upperBounds;
        private Type[] lowerBounds;

        /**
         * Specify upper bounds of the wildcard type to build.
         * @param bounds to set
         * @return {@code this}
         */
        public WildcardTypeBuilder withUpperBounds(final Type... bounds) {
            this.upperBounds = bounds;
            return this;
        }

        /**
         * Specify lower bounds of the wildcard type to build.
         * @param bounds to set
         * @return {@code this}
         */
        public WildcardTypeBuilder withLowerBounds(final Type... bounds) {
            this.lowerBounds = bounds;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WildcardType build() {
            return new WildcardTypeImpl(upperBounds, lowerBounds);
        }
    }

        /**
     * GenericArrayType implementation class.
     * @since 3.2
     */
    private static final class GenericArrayTypeImpl implements GenericArrayType {
        private final Type componentType;

        /**
         * Constructor
         * @param componentType of this array type
         */
        private GenericArrayTypeImpl(final Type componentType) {
            this.componentType = componentType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return TypeUtils.toString(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            return obj == this || obj instanceof GenericArrayType && TypeUtils.equals(this, (GenericArrayType) obj);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = 67 << 4;
            result |= componentType.hashCode();
            return result;
        }
    }

}
