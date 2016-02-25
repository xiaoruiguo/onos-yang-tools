/*
 * Copyright 2016 Open Networking Laboratory
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

package org.onosproject.yangutils.datamodel;

import java.util.HashSet;
import java.util.Set;

import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.parser.Parsable;
import org.onosproject.yangutils.parser.ParsableDataType;

/*
 * Reference RFC 6020.
 *
 * The bits built-in type represents a bit set.  That is, a bits value
 * is a set of flags identified by small integer position numbers
 * starting at 0.  Each bit number has an assigned name.
 */

/**
 * Maintains the bits data type information.
 */
public class YangBits implements Parsable {

    // Bits information set.
    private Set<YangBit> bitSet;

    // BITS name.
    private String bitsName;

    /**
     * Create a YANG bits type object.
     */
    public YangBits() {
        setBitSet(new HashSet<YangBit>());
    }

    /**
     * Returns the bit set.
     *
     * @return the bit set
     */
    public Set<YangBit> getBitSet() {
        return bitSet;
    }

    /**
     * Set the bit set.
     *
     * @param bitSet the bit set
     */
    private void setBitSet(Set<YangBit> bitSet) {
        this.bitSet = bitSet;
    }

    /**
     * Add bit info.
     *
     * @param bitInfo the bit information to be added.
     * @throws DataModelException due to violation in data model rules.
     */
    public void addBitInfo(YangBit bitInfo) throws DataModelException {
        if (!getBitSet().add(bitInfo)) {
            throw new DataModelException("YANG Bit already exists");
        }
    }

    /**
     * Returns the type of the data.
     *
     * @return ParsedDataType returns BITS_DATA
     */
    @Override
    public ParsableDataType getParsableDataType() {
        return ParsableDataType.BITS_DATA;
    }

    /**
     * Returns the bits name.
     *
     * @return name of the bits.
     */
    public String getBitsName() {
        return bitsName;
    }

    /**
     * Set bits name.
     *
     * @param bitsName bit name to be set.
     */
    public void setBitsName(String bitsName) {
        this.bitsName = bitsName;
    }

    /**
     * Validate the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules.
     */
    @Override
    public void validateDataOnEntry() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Validate the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules.
     */
    @Override
    public void validateDataOnExit() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }
}
