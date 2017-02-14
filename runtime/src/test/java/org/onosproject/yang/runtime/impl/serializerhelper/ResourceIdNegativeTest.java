/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.impl.serializerhelper;

import org.junit.Test;
import org.onosproject.yang.model.ResourceId;
import org.onosproject.yang.runtime.impl.TestYangSerializerContext;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.addToResourceId;
import static org.onosproject.yang.runtime.helperutils.SerializerHelper.initializeResourceId;

/**
 * Tests the serializer helper methods.
 */
public class ResourceIdNegativeTest {

    public static final String LNS = "yrt:list";

    TestYangSerializerContext context = new TestYangSerializerContext();

    private static final String E_LEAFLIST =
            "Method is not allowed to pass multiple values for leaf-list.";

    /**
     * Test adding a leaf-list node in resource id with list of values negative
     * scenario.
     */
    @Test
    public void negative1Test() {

        ResourceId.Builder rIdBlr = initializeResourceId(context);
        List<String> valueSet = new LinkedList<>();
        valueSet.add("1");
        valueSet.add("2");
        boolean isExpOccurred = false;
        try {
            addToResourceId(rIdBlr, "leaf1", LNS, valueSet);
        } catch (IllegalArgumentException e) {
            isExpOccurred = true;
            assertEquals(e.getMessage(), E_LEAFLIST);
        }
        assertEquals(isExpOccurred, true);
    }
}
