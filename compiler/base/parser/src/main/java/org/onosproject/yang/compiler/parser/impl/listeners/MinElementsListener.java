/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yang.compiler.parser.impl.listeners;

import org.onosproject.yang.compiler.datamodel.YangMinElementHolder;
import org.onosproject.yang.compiler.datamodel.YangMinElement;
import org.onosproject.yang.compiler.datamodel.utils.Parsable;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;
import org.onosproject.yang.compiler.parser.impl.TreeWalkListener;

import static org.onosproject.yang.compiler.datamodel.utils.YangConstructType.MIN_ELEMENT_DATA;
import static org.onosproject.yang.compiler.parser.antlrgencode.GeneratedYangParser.MinElementsStatementContext;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorLocation.ENTRY;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorMessageConstruction.constructListenerErrorMessage;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.INVALID_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerErrorType.MISSING_HOLDER;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerUtil.getValidNonNegativeIntegerValue;
import static org.onosproject.yang.compiler.parser.impl.parserutils.ListenerValidation.checkStackIsNotEmpty;

/*
 * Reference: RFC6020 and YANG ANTLR Grammar
 *
 * ABNF grammar as per RFC6020
 *  min-elements-stmt   = min-elements-keyword sep
 *                        min-value-arg-str stmtend
 *  min-value-arg-str   = < a string that matches the rule
 *                          min-value-arg >
 *  min-value-arg       = non-negative-integer-value
 *
 * ANTLR grammar rule
 * minElementsStatement : MIN_ELEMENTS_KEYWORD minValue STMTEND;
 * minValue             : string;
 */

/**
 * Represents listener based call back function corresponding to the "min-elements"
 * rule defined in ANTLR grammar file for corresponding ABNF rule in RFC 6020.
 */
public final class MinElementsListener {

    /**
     * Creates a new min-elements listener.
     */
    private MinElementsListener() {
    }

    /**
     * It is called when parser receives an input matching the grammar
     * rule (min-elements), performs validation and updates the data model
     * tree.
     *
     * @param listener listener's object
     * @param ctx      context object of the grammar rule
     */
    public static void processMinElementsEntry(TreeWalkListener listener,
                                               MinElementsStatementContext ctx) {

        // Check for stack to be non empty.
        checkStackIsNotEmpty(listener, MISSING_HOLDER, MIN_ELEMENT_DATA,
                             ctx.minValue().getText(), ENTRY);

        int minElementValue = getValidNonNegativeIntegerValue(
                ctx.minValue().getText(), MIN_ELEMENT_DATA, ctx);

        YangMinElement minElement = new YangMinElement();

        minElement.setMinElement(minElementValue);
        minElement.setLineNumber(ctx.getStart().getLine());
        minElement.setCharPosition(ctx.getStart().getCharPositionInLine());
        minElement.setFileName(listener.getFileName());

        Parsable tmpData = listener.getParsedDataStack().peek();
        if (tmpData instanceof YangMinElementHolder) {
            YangMinElementHolder holder = ((YangMinElementHolder) tmpData);
            holder.setMinElements(minElement);
        } else {
            throw new ParserException(constructListenerErrorMessage(
                    INVALID_HOLDER, MIN_ELEMENT_DATA, ctx.minValue().getText(),
                    ENTRY));
        }
    }
}