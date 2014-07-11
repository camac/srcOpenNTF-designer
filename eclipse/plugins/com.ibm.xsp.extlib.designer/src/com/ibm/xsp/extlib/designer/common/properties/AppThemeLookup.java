/*
 * © Copyright IBM Corp. 2011
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
package com.ibm.xsp.extlib.designer.common.properties;

import java.util.ArrayList;

import com.ibm.commons.iloader.node.lookups.api.AbstractLookup;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.LookupListener;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.ide.resources.extensions.DesignerProject;
import com.ibm.designer.domino.ide.resources.extensions.util.DesignerDELookup;

/**
 * @author mleland
 *
 */
public class AppThemeLookup extends AbstractLookup {
    
    private static final String SERVER_DEFAULT = "Server default";  // $NLX-AppThemeLookup.Serverdefault-1$
    private static final String APP_DEFAULT = "Application default"; // $NLX-AppThemeLookup.Applicationdefault-1$
    
    public final static String[] themeStarterCodes = {"", "webstandard", "oneui", "oneuiv2", "oneuiv2.1", "oneuiv3.0.2"}; // $NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$ $NON-NLS-4$ $NON-NLS-5$
    public final static String[] sysThemeStarterLabels = {SERVER_DEFAULT, "webstandard", "OneUI", "OneUI V2", "OneUI V2.1", "OneUI V3.0.2"}; // $NLX-AppThemeLookup.webstandard_sys-1$ $NLX-AppThemeLookup.OneUI.1_sys-2$ $NLX-AppThemeLookup.OneUIV2_sys-3$ $NLX-AppThemeLookup.OneUIV21_sys.1-4$ $NLX-AppThemeLookup.OneUIV302_sys-5$
    public final static String[] appThemeStarterLabels = {APP_DEFAULT, "webstandard", "OneUI", "OneUI V2", "OneUI V2.1", "OneUI V3.0.2"}; // $NLX-AppThemeLookup.webstandard_app-1$ $NLX-AppThemeLookup.OneUI_app-2$ $NLX-AppThemeLookup.OneUIV2_app-3$ $NLX-AppThemeLookup.OneUIV21_app-4$ $NLX-AppThemeLookup.OneUIV302_app-5$
    
    private String[] startCodes;
    private String[] startLabels;
    private DesignerDELookup themeLookup = null;
    private LookupListener ourListener = new LookupListener() {
        public void lookupChanged(ILookup lookup) {
            update();
        }
    };

    public AppThemeLookup(DesignerProject prj, String[] codes, String[] labels) {
        this.startCodes = codes;
        this.startLabels = labels;
        themeLookup = DesignerDELookup.getThemesLookup(prj);
        themeLookup.addLookupListener(ourListener);
    }
    
    // be sure to call this when the user of this lookup goes away
    public void dispose() {
        themeLookup.removeLookupListener(ourListener);
    }
    
    public int size() {
        return (startCodes.length + themeLookup.size());
    }
    
    public String getCode(int index) {
        if(index >= size()) {
            return ""; //$NON-NLS-1$
        }
        else if (index < startCodes.length) {
            return startCodes[index];
        }
        index -= startCodes.length;
        // don't want the slash in this context
        String themeLabel = themeLookup.getLabel(index);
        return themeLabel.concat(".theme"); // need to store with extension $NON-NLS-1$
    }
    
    public String getLabel(int index) {
        if(index >= size()) {
            return ""; //$NON-NLS-1$
        }
        else if (index < startCodes.length) {
            return startLabels[index];
        }
        index -= startCodes.length;
        return themeLookup.getLabel(index);
    }
    
    public void update() {
        notifyLookupChanged();
    }

    public boolean equals(Object obj) {
        if(obj instanceof AppThemeLookup) {
            if(((AppThemeLookup)obj).size() != this.size()) {
                return false;
            }
            int size = size();
            for(int i = 0; i < size; i++) {
                if(!StringUtil.equals(getCode(i), ((AppThemeLookup)obj).getCode(i))) {
                   return false; 
                }
                if(!StringUtil.equals(getLabel(i), ((AppThemeLookup)obj).getLabel(i))) {
                    return false; 
                 }
            }
            return true;
        }
        return super.equals(obj);
    }
    
    // GarMaj - Function added for GGRD9AZKLA and GGRD9BFN7G
    public void removeDuplicateStartLabels() {
        ArrayList<String> newLabels = new ArrayList<String>();
        ArrayList<String> newCodes = new ArrayList<String>();
        
        for(int i=0; i < startLabels.length; i++) {
            int j;

            // Search for each label in the Themes
            for(j=0; j < themeLookup.size(); j++) {
                // We're going to assume underscore == whitespace for the comparison
                String startLabel = startLabels[i].trim().replace(' ', '_');
                String themeLabel = themeLookup.getLabel(j).trim().replace(' ', '_');
                if(startLabel.equalsIgnoreCase(themeLabel)) {
                    // Found label match in Themes
                    break;
                }
            }
            
            if(j == themeLookup.size()) {
                // Label is unique - Add to the new lists
                newLabels.add(startLabels[i]);
                newCodes.add(startCodes[i]);
            }
        }
        
        startLabels = newLabels.toArray(new String[newLabels.size()]);
        startCodes = newCodes.toArray(new String[newCodes.size()]);
    }    
}