/*
 * � Copyright IBM Corp. 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */
package com.ibm.xsp.extlib.designer.tooling.visualizations.inotes;

import org.w3c.dom.Node;

import com.ibm.designer.domino.constants.XSPAttributeNames;
import com.ibm.designer.domino.constants.XSPTagNames;
import com.ibm.xsp.extlib.designer.tooling.visualizations.AbstractCommonControlVisualizer;
import com.ibm.xsp.registry.FacesRegistry;

/**
 * This class generates the following source
 * 
 *  <?xml version="1.0" encoding="UTF-8"?>
 *  <xp:view xmlns:xp="http://www.ibm.com/xsp/core">
 * 
 *      <xp:label style="border-color:rgb(192,192,192);border-style:solid;border-width:2px;padding:2px;margin-left:2px;margin-top:2px;margin-bottom:2px" value="Notes Calendar Store">
 *      </xp:label>
 * 
 *  </xp:view>
 *
 */
public class NotesCalendarStoreVisualizer extends AbstractCommonControlVisualizer{

    private static final String NOTES_CALENDAR_STORE_STRING = "Notes Calendar Store"; // $NLX-NotesCalendarStoreVisualizer.NotesCalendarStore-1$
    
    /*
     * (non-Javadoc)
     * @see com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory#getXSPMarkupForControl(org.w3c.dom.Node, com.ibm.designer.domino.xsp.api.visual.AbstractVisualizationFactory.IVisualizationCallback, com.ibm.xsp.registry.FacesRegistry)
     */
    @Override
    public String getXSPMarkupForControl(Node nodeToVisualize,  IVisualizationCallback callback, FacesRegistry registry) {

        StringBuilder strBuilder = new StringBuilder();
        
        Tag label = new Tag(XP_PREFIX, XSPTagNames.XSP_TAG_LABEL);
        label.addAttribute(XSPAttributeNames.XSP_ATTR_STYLE, "border-color:rgb(192,192,192);border-style:solid;border-width:2px;padding:2px;margin-left:2px;margin-top:2px;margin-bottom:2px"); // $NON-NLS-1$
        label.addAttribute(XSPAttributeNames.XSP_ATTR_VALUE,NOTES_CALENDAR_STORE_STRING);
        strBuilder.append(label.toString());
        
        strBuilder.append(LINE_DELIMITER);
        return strBuilder.toString();
    }
}