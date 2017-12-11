/*
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.yang.compiler.datamodel.utils.builtindatatype;

import org.onosproject.yang.compiler.datamodel.YangDerivedInfo;
import org.onosproject.yang.compiler.datamodel.YangIdentity;
import org.onosproject.yang.compiler.datamodel.YangIdentityRef;
import org.onosproject.yang.compiler.datamodel.YangLeafRef;
import org.onosproject.yang.compiler.datamodel.YangType;
import org.onosproject.yang.compiler.datamodel.YangUnion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

public final class LeafContextUtil {

    // Object provider constant error string.
    private static final String E_DATATYPE = "Data type not supported.";
    private static final String T = "true";
    private static final String F = "false";

    /**
     * Restricts creation of object providers instance.
     */
    private LeafContextUtil() {
    }

    /**
     * Returns object from string value.
     *
     * @param typeInfo refers to YANG type information
     * @param v        v argument is leaf value used to set the value in method
     * @param dataType yang data type
     * @return values object of corresponding given data type
     * @throws IllegalArgumentException if input is not valid
     */
    public static Object getObject(YangType typeInfo, String v,
                                   YangDataTypes dataType)
            throws IllegalArgumentException {
        YangDataTypes type;
        if (dataType != null) {
            type = dataType;
        } else {
            type = typeInfo.getDataType();
        }
        switch (type) {
            case INT8:
                return Byte.parseByte(v);
            case UINT8:
            case INT16:
                return Short.parseShort(v);
            case UINT16:
            case INT32:
                return Integer.parseInt(v);
            case UINT32:
            case INT64:
                return Long.parseLong(v);
            case UINT64:
                return new BigInteger(v);
            case EMPTY:
                if (v == null || v.equals("")) {
                    return null;
                } else if (v.equals(T) || v.equals(F)) {
                    return Boolean.parseBoolean(v);
                }
            case BOOLEAN:
                if (v.equals(T) || v.equals(F)) {
                    return Boolean.parseBoolean(v);
                }
                throw new IllegalArgumentException(E_DATATYPE);
            case BINARY:
            case BITS:
            case IDENTITYREF:
            case ENUMERATION:
            case STRING:
            case INSTANCE_IDENTIFIER:
                return v;
            case DECIMAL64:
                return new BigDecimal(v);
            case LEAFREF:
                YangType refType = ((YangLeafRef) typeInfo
                        .getDataTypeExtendedInfo()).getEffectiveDataType();
                return getObject(refType, v, refType.getDataType());
            case DERIVED:
                // referred typedef's list of type will always has only one type
                YangType rt = ((YangDerivedInfo) typeInfo
                        .getDataTypeExtendedInfo()).getReferredTypeDef()
                        .getTypeList().get(0);
                return getObject(rt, v, rt.getDataType());
            case UNION:
                return parseUnionTypeInfo(typeInfo, v);
            default:
                throw new IllegalArgumentException(E_DATATYPE);
        }
    }

    /**
     * Returns value namespace from string value.
     *
     * @param typeInfo refers to YANG type information
     * @param v        v argument is leaf value used to set the value in method
     * @param dataType yang data type
     * @return value namespace of corresponding given data type and value
     * @throws IllegalArgumentException if input is not valid
     */
    public static String getValueNamespace(YangType typeInfo, String v,
                                           YangDataTypes dataType)
            throws IllegalArgumentException {
        YangDataTypes type;
        if (dataType != null) {
            type = dataType;
        } else {
            type = typeInfo.getDataType();
        }
        switch (type) {
            case INT8:
            case UINT8:
            case INT16:
            case UINT16:
            case INT32:
            case UINT32:
            case INT64:
            case UINT64:
            case EMPTY:
            case BOOLEAN:
            case BINARY:
            case BITS:
            case ENUMERATION:
            case STRING:
            case DECIMAL64:
            case INSTANCE_IDENTIFIER:
                return null;
            case IDENTITYREF:
                YangIdentity refId = ((YangIdentityRef) typeInfo
                        .getDataTypeExtendedInfo()).getReferredIdentity();
                return getReferIdNamespace(refId, v);
            case LEAFREF:
                YangType refType = ((YangLeafRef) typeInfo
                        .getDataTypeExtendedInfo()).getEffectiveDataType();
                return getValueNamespace(refType, v, refType.getDataType());
            case DERIVED:
                // referred typedef's list of type will always has only one type
                YangType rt = ((YangDerivedInfo) typeInfo
                        .getDataTypeExtendedInfo()).getReferredTypeDef()
                        .getTypeList().get(0);
                return getValueNamespace(rt, v, rt.getDataType());
            case UNION:
                return getUnionValNamespace(typeInfo, v);
            default:
                throw new IllegalArgumentException(E_DATATYPE);
        }
    }

    private static String getReferIdNamespace(YangIdentity refId, String v) {
        String baseIdentity = refId.getYangSchemaNodeIdentifier().getName();
        if (v.equals(baseIdentity)) {
            return refId.getYangSchemaNodeIdentifier().getNameSpace()
                    .getModuleNamespace();
        }
        for (YangIdentity i : refId.getExtendList()) {
            String refIdentity = i.getYangSchemaNodeIdentifier().getName();
            if (v.equals(refIdentity)) {
                return i.getYangSchemaNodeIdentifier().getNameSpace()
                        .getModuleNamespace();
            }
        }
        throw new IllegalArgumentException("Invalid value of data");
    }

    private static String getUnionValNamespace(YangType type, String leafValue) {
        Iterator<YangType<?>> it = ((YangUnion) type.getDataTypeExtendedInfo())
                .getTypeList().listIterator();
        while (it.hasNext()) {
            YangType t = it.next();
            try {
                getObject(t, leafValue, t.getDataType());
                return getValueNamespace(t, leafValue, t.getDataType());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Invalid value of data");
    }

    /**
     * Returns the object for given data type with respective value.
     *
     * @param type      data type of value
     * @param leafValue value
     * @return object of data type containing the value
     */
    private static Object parseUnionTypeInfo(YangType type, String leafValue) {
        Iterator<YangType<?>> it = ((YangUnion) type.getDataTypeExtendedInfo())
                .getTypeList().listIterator();
        while (it.hasNext()) {
            YangType t = it.next();
            try {
                return getObject(t, leafValue, t.getDataType());
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        throw new IllegalArgumentException("Invalid value of data");
    }
}