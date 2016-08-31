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

package org.onosproject.yangutils.translator.tojava.javamodel;

import java.io.IOException;
import org.onosproject.yangutils.datamodel.RpcNotificationContainer;
import org.onosproject.yangutils.datamodel.YangLeavesHolder;
import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.datamodel.javadatamodel.YangJavaNotification;
import org.onosproject.yangutils.translator.exception.TranslatorException;
import org.onosproject.yangutils.translator.tojava.JavaCodeGenerator;
import org.onosproject.yangutils.translator.tojava.JavaCodeGeneratorInfo;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoContainer;
import org.onosproject.yangutils.translator.tojava.JavaFileInfoTranslator;
import org.onosproject.yangutils.translator.tojava.JavaQualifiedTypeInfoTranslator;
import org.onosproject.yangutils.translator.tojava.TempJavaCodeFragmentFiles;
import org.onosproject.yangutils.translator.tojava.TempJavaCodeFragmentFilesContainer;
import org.onosproject.yangutils.translator.tojava.TempJavaServiceFragmentFiles;
import org.onosproject.yangutils.translator.tojava.utils.JavaExtendsListHolder;
import org.onosproject.yangutils.utils.io.YangPluginConfig;

import static org.onosproject.yangutils.translator.tojava.GeneratedJavaFileType.GENERATE_INTERFACE_WITH_BUILDER;
import static org.onosproject.yangutils.translator.tojava.YangJavaModelUtils.generateCodeOfAugmentableNode;
import static org.onosproject.yangutils.translator.tojava.utils.JavaIdentifierSyntax.getEnumJavaAttribute;
import static org.onosproject.yangutils.utils.UtilConstants.EVENT_LISTENER_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.EVENT_STRING;
import static org.onosproject.yangutils.utils.io.impl.YangIoUtils.getCapitalCase;

/**
 * Represents notification information extended to support java code generation.
 */
public class YangJavaNotificationTranslator
        extends YangJavaNotification
        implements JavaCodeGenerator, JavaCodeGeneratorInfo {

    private static final long serialVersionUID = 806201624L;

    /**
     * File handle to maintain temporary java code fragments as per the code
     * snippet types.
     */
    private transient TempJavaCodeFragmentFiles tempFileHandle;

    /**
     * Creates an instance of java Notification.
     */
    public YangJavaNotificationTranslator() {
        super();
        setJavaFileInfo(new JavaFileInfoTranslator());
        getJavaFileInfo().setGeneratedFileTypes(GENERATE_INTERFACE_WITH_BUILDER);
    }

    /**
     * Returns the generated java file information.
     *
     * @return generated java file information
     */
    @Override
    public JavaFileInfoTranslator getJavaFileInfo() {

        if (javaFileInfo == null) {
            throw new TranslatorException("Missing java info in java datamodel node " +
                    getName() + " in " +
                    getLineNumber() + " at " +
                    getCharPosition()
                    + " in " + getFileName());
        }
        return (JavaFileInfoTranslator) javaFileInfo;
    }

    /**
     * Sets the java file info object.
     *
     * @param javaInfo java file info object
     */
    @Override
    public void setJavaFileInfo(JavaFileInfoTranslator javaInfo) {
        javaFileInfo = javaInfo;
    }

    /**
     * Returns the temporary file handle.
     *
     * @return temporary file handle
     */
    @Override
    public TempJavaCodeFragmentFiles getTempJavaCodeFragmentFiles() {
        return tempFileHandle;
    }

    /**
     * Sets temporary file handle.
     *
     * @param fileHandle temporary file handle
     */
    @Override
    public void setTempJavaCodeFragmentFiles(TempJavaCodeFragmentFiles fileHandle) {
        tempFileHandle = fileHandle;
    }

    /**
     * Prepare the information for java code generation corresponding to YANG
     * notification info.
     *
     * @param yangPlugin YANG plugin config
     * @throws TranslatorException translator operation fail
     */
    @Override
    public void generateCodeEntry(YangPluginConfig yangPlugin) throws TranslatorException {

        /*
         * As part of the notification support the following files needs to be generated.
         * 1) Subject of the notification(event), this is simple interface with builder class.
         * 2) Event class extending "AbstractEvent" and defining event type enum.
         * 3) Event listener interface extending "EventListener".
         *
         * The manager class needs to extend the ListenerRegistry.
         */

        // Generate subject of the notification(event), this is simple interface
        // with builder class.
        try {
            generateCodeOfAugmentableNode(this, yangPlugin);
            addNotificationToExtendsList();
        } catch (IOException e) {
            throw new TranslatorException(
                    "Failed to prepare generate code entry for notification node " +
                            getName() + " in " +
                            getLineNumber() + " at " +
                            getCharPosition()
                            + " in " + getFileName() + " " + e.getLocalizedMessage());
        }
    }

    /*Adds current notification info to the extends list so its parents service*/
    private void addNotificationToExtendsList() {
        YangNode parent = getParent();
        TempJavaServiceFragmentFiles tempJavaServiceFragmentFiles = ((TempJavaCodeFragmentFilesContainer) parent)
                .getTempJavaCodeFragmentFiles()
                .getServiceTempFiles();
        JavaExtendsListHolder holder = tempJavaServiceFragmentFiles.getJavaExtendsListHolder();
        JavaQualifiedTypeInfoTranslator event = new JavaQualifiedTypeInfoTranslator();

        String parentInfo = getCapitalCase(((JavaFileInfoContainer) parent)
                .getJavaFileInfo().getJavaName());
        event.setClassInfo(parentInfo + EVENT_STRING);
        event.setPkgInfo(getJavaFileInfo().getPackage());
        holder.addToExtendsList(event, parent, tempJavaServiceFragmentFiles);

        JavaQualifiedTypeInfoTranslator eventListener = new JavaQualifiedTypeInfoTranslator();

        eventListener.setClassInfo(parentInfo + EVENT_LISTENER_STRING);
        eventListener.setPkgInfo(getJavaFileInfo().getPackage());
        holder.addToExtendsList(eventListener, parent, tempJavaServiceFragmentFiles);

    }

    /**
     * Creates a java file using the YANG notification info.
     */
    @Override
    public void generateCodeExit() throws TranslatorException {
        try {
            getTempJavaCodeFragmentFiles().generateJavaFile(GENERATE_INTERFACE_WITH_BUILDER, this);
        } catch (IOException e) {
            throw new TranslatorException("Failed to generate code for notification node " +
                    getName() + " in " +
                    getLineNumber() + " at " +
                    getCharPosition()
                    + " in " + getFileName() + " " + e.getLocalizedMessage());
        }
    }

    @Override
    public void setNameSpaceAndAddToParentSchemaMap() {
        // Get parent namespace.
        if (this.getParent() != null) {
            String nameSpace = this.getParent().getNameSpace();
            // Set namespace for self node.
            setNameSpace(nameSpace);
            // Process addition of leaf to the child schema map of parent.
            processAdditionOfSchemaNodeToParentMap(getName(), getNameSpace());
            // Obtain the notification name as per enum in notification.
            String enumName = getEnumJavaAttribute(getJavaFileInfo().getJavaName()).toUpperCase();

            ((RpcNotificationContainer) this.getParent()).addToNotificationEnumMap(enumName, this);

        }
        /*
         * Check if node contains leaf/leaf-list, if yes add namespace for leaf
         * and leaf list.
         */
        if (this instanceof YangLeavesHolder) {
            ((YangLeavesHolder) this).setLeafNameSpaceAndAddToParentSchemaMap();
        }
    }
}
