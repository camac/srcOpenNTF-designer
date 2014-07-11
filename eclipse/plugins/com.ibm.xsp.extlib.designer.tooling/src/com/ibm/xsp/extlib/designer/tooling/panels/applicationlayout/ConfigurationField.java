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
package com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout;


import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Image;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.iloader.node.lookups.api.LookupListener;
import com.ibm.designer.domino.xsp.registry.DesignerExtension;
import com.ibm.designer.domino.xsp.registry.DesignerExtensionUtil;
import com.ibm.xsp.extlib.designer.tooling.panels.util.AttributeComputedField;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.registry.FacesDefinition;
import com.ibm.xsp.registry.FacesRegistry;

public class ConfigurationField extends AttributeComputedField {
    
    final ExtensionLookup  _lookup;
    
    public ConfigurationField(DataNode node, FacesRegistry registry) {
        super(EXT_LIB_ATTR_CONFIGURATION, node);

        ExtensionLookup up = null;

        if (registry != null) {
            List<FacesDefinition> list = ExtLibRegistryUtil.getConfigNodes(registry); 
            up = new ExtensionLookup(list);
        }

        _lookup = up;
    }
    
    
    public ILookup getLookup() {
        return _lookup;
    }

    

    /**
     *  provides choices for defined configurations
     */
    static public class ExtensionLookup implements ILookup {
        
        final Map<String, String> map = new TreeMap<String, String>();
        final Map<String, FacesDefinition> mapDefs = new TreeMap<String, FacesDefinition>();
        
        final Object[] codes;
        final Object[] labels;
        
        public FacesDefinition getDefFromCode(String code) {
            return mapDefs.get(code);
        }
        
        public ExtensionLookup(List<FacesDefinition> faceslist) {
            
            for (Iterator<FacesDefinition> it = faceslist.iterator(); it.hasNext();) {
                FacesDefinition def = it.next();
                DesignerExtension ext = DesignerExtensionUtil.getExtension(def);

                if (ext != null) {
                    map.put(def.getTagName(), ext.getDisplayName());
                    mapDefs.put(def.getTagName(), def);
                }
            }
            codes  = map.keySet().toArray();
            labels = map.values().toArray();
        }
        
        
        public int size() {return map.size(); }
       
        public String getCode(int index) {
            if (index >= codes.length) return null;
            return codes[index].toString();
        }
        
        public String getLabel(int index) {
            if (index >= labels.length) return null;
            return labels[index].toString();
        }

        public String getLabelFromCode(String code) {
            if (map.containsKey(code))
                return map.get(code);
            return "";
        }
        
        public Image getImage(int index) { return null;}
        public void addLookupListener( LookupListener listener ) {}
        public void removeLookupListener( LookupListener listener ) {}      
    }

}
