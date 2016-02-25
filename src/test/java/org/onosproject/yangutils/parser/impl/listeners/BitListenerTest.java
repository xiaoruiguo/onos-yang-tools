/*
 * Copyright 2014-2016 Open Networking Laboratory
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

package org.onosproject.yangutils.parser.impl.listeners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import org.onosproject.yangutils.datamodel.YangBit;
import org.onosproject.yangutils.datamodel.YangBits;
import org.onosproject.yangutils.datamodel.YangDataTypes;
import org.onosproject.yangutils.datamodel.YangLeaf;
import org.onosproject.yangutils.datamodel.YangModule;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.YangNodeType;
import org.onosproject.yangutils.parser.exceptions.ParserException;
import org.onosproject.yangutils.parser.impl.YangUtilsParserManager;

import java.io.IOException;
import java.util.ListIterator;
import java.util.Set;

/**
 * Test cases for bit listener.
 */
public class BitListenerTest {

    private final YangUtilsParserManager manager = new YangUtilsParserManager();

    /**
     * Checks bit statement without position.
     */
    @Test
    public void processBitTypeStatement() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/BitTypeStatement.yang");

        // Check whether the data model tree returned is of type module.
        assertThat((node instanceof YangModule), is(true));

        // Check whether the node type is set properly to module.
        assertThat(node.getNodeType(), is(YangNodeType.MODULE_NODE));

        // Check whether the module name is set correctly.
        YangModule yangNode = (YangModule) node;
        assertThat(yangNode.getName(), is("Test"));

        ListIterator<YangLeaf> leafIterator = yangNode.getListOfLeaf().listIterator();
        YangLeaf leafInfo = leafIterator.next();

        assertThat(leafInfo.getLeafName(), is("mybits"));
        assertThat(leafInfo.getDataType().getDataTypeName(), is("bits"));
        assertThat(leafInfo.getDataType().getDataType(), is(YangDataTypes.BITS));
        assertThat(((YangBits) leafInfo.getDataType().getDataTypeExtendedInfo()).getBitsName(),
                is("mybits"));

        Set<YangBit> bitSet = ((YangBits) leafInfo.getDataType().getDataTypeExtendedInfo()).getBitSet();
        for (YangBit tmp : bitSet) {
            if (tmp.getBitName().equals("disable-nagle")) {
                assertThat(tmp.getPosition(), is(0));
            } else if (tmp.getBitName().equals("auto-sense-speed")) {
                assertThat(tmp.getPosition(), is(1));
            } else if (tmp.getBitName().equals("Ten-Mb-only")) {
                assertThat(tmp.getPosition(), is(2));
            }
        }
    }

    /**
     * Checks if enum with same name is not allowed.
     */
    @Test(expected = ParserException.class)
    public void processBitWithDuplicateName() throws IOException, ParserException {

        YangNode node = manager.getDataModel("src/test/resources/BitWithDuplicateName.yang");
    }
}
