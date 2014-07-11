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
package com.ibm.xsp.extlib.designer.tooling.palette.applicationlayout;

import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CHILDREN;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_CONFIGURATION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_FOOTER;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_FOOTER_LINKS;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_HREF;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibAttrNames.EXT_LIB_ATTR_LABEL;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_NAMESPACE_URI;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibRegistry.EXT_LIB_TAG_ONEUI_CONFIGURATION;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.EXT_LIB_TAG_APPLICATION_LAYOUT;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.EXT_LIB_TAG_BASIC_CONTAINER_NODE;
import static com.ibm.xsp.extlib.designer.tooling.constants.IExtLibTagNames.EXT_LIB_TAG_BASIC_LEAF_NODE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ibm.commons.iloader.node.DataNode;
import com.ibm.commons.iloader.node.IClassDef;
import com.ibm.commons.iloader.node.ILoader;
import com.ibm.commons.iloader.node.IMember;
import com.ibm.commons.iloader.node.collections.SingleCollection;
import com.ibm.commons.iloader.node.lookups.api.ILookup;
import com.ibm.commons.swt.SWTLayoutUtils;
import com.ibm.commons.swt.data.controls.DCComboBox;
import com.ibm.commons.swt.data.controls.DCUtils;
import com.ibm.commons.swt.data.dialog.LWPDCommonDialog;
import com.ibm.commons.util.StringUtil;
import com.ibm.designer.domino.xsp.api.panels.IPanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.PanelExtraData;
import com.ibm.designer.domino.xsp.api.panels.complex.ComplexPanelComposite;
import com.ibm.designer.domino.xsp.api.panels.complex.IComplexPanel;
import com.ibm.designer.domino.xsp.api.util.XPagesPropertiesViewUtils;
import com.ibm.xsp.extlib.designer.common.properties.ContentFacadeFactory;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties;
import com.ibm.xsp.extlib.designer.common.properties.PreservingProperties.ContentFacade;
import com.ibm.xsp.extlib.designer.tooling.panels.applicationlayout.ConfigurationField;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibRegistryUtil;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingLogger;
import com.ibm.xsp.extlib.designer.tooling.utils.ExtLibToolingUtil;
import com.ibm.xsp.registry.FacesRegistry;

public class ApplicationLayoutDropDialog extends LWPDCommonDialog {
    
    private Composite               mainPanel = null;
    private DCComboBox              combo;
    private ComplexPanelComposite   dynamicComposite;
    private final IPanelExtraData   panelData; 
    private DataNode                dnAppLayout;
    boolean                         propsOpenInEditor = false; // true if an Editor that edits xsp.properties is open 
    
    public ApplicationLayoutDropDialog(Shell shell, PanelExtraData data, boolean isCustomControl) {
        super(shell);
        this.panelData = data;
    }
    
    @Override
    protected boolean needsProgressMonitor() {
        return false;
    }
    

    
    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#getDialogTitle()
     */
    @Override
    protected String getDialogTitle() {
        return "Set Parameters for Application Layout"; // $NLX-ApplicationLayoutDropDialog.Setparametersforapplicationlayout-1$
    }

    /* (non-Javadoc)
     * @see com.ibm.commons.swt.data.dialog.LWPDCommonDialog#fillClientArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void fillClientArea(Composite parent) {
        
        propsOpenInEditor = ExtLibToolingUtil.isPropertiesOpenInEditor(panelData.getDesignerProject());
        
        initData(parent);
        
        if (parent.getLayout() instanceof GridLayout) {
            ((GridLayout) parent.getLayout()).marginWidth = 7;
            ((GridLayout) parent.getLayout()).marginHeight = 0;
        }
    
        mainPanel = new Composite(parent, SWT.NONE);
        GridLayout layout = SWTLayoutUtils.createLayoutDefaultSpacing(1);
        mainPanel.setLayout(layout);
        GridData data = SWTLayoutUtils.createGDFill();
        data.horizontalSpan = 2;
        mainPanel.setLayoutData(data);
    
    
        Composite pickerParent = new Composite(mainPanel, SWT.NONE);
        pickerParent.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        pickerParent.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        new Label(pickerParent, SWT.NONE).setText("&Configuration:"); // $NLX-AddViewControlDialog.Configuration-1$

        combo = new DCComboBox(pickerParent, SWT.DROP_DOWN | SWT.READ_ONLY, "applayout.config.id"); //$NON-NLS-1$
        combo.setLayoutData(SWTLayoutUtils.createGDFillHorizontal());
        combo.setFirstBlankLine(false);
        combo.setEditableLabels(false);

        FacesRegistry registry = panelData.getDesignerProject().getFacesRegistry();
        ConfigurationField configField = new ConfigurationField(dnAppLayout, registry);
        combo.setAttributeName(configField.getName());
        ILookup lookup = configField.getLookup();
        combo.setLookup(lookup);
        
        dynamicComposite = new ComplexPanelComposite(mainPanel, SWT.NONE);
        GridData gd = GridDataFactory.copyData(data);
        int hint = 156;
        
        dynamicComposite.updatePanelData(panelData);

        // check if we should add theme combo
        if (addingThemeControlInfo()) {  
            hint = 181; // make extra room for theme combo
        }
        hint = convertVerticalDLUsToPixels(hint);
        gd.heightHint = hint; // make room for dynamic panels
        
        dynamicComposite.setLayoutData(gd);
        
        combo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                DataNode dn = DCUtils.findDataNode(combo, true); 
                if (dn != null) {
                    IMember config = dn.getMember(EXT_LIB_ATTR_CONFIGURATION);
                    if (config != null) {
                        try {
                            initControlDataNode(dynamicComposite, dnAppLayout.getClassDef());
                        }
                        catch(Exception e) {
                            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, "Error changing configuration of applicationLayout"); // $NLE-ApplicationLayoutDropDialog.Errorchangingconfigurationofappli-1$
                        }
                    }
                }
                updateUI();
            }
        });
        boolean found = false;
        for (int i = 0 ; i < lookup.size(); i++) {
            String label = lookup.getCode(i);
            if (label != null && StringUtil.startsWithIgnoreCase(label, "oneui")){ //$NON-NLS-1$
                combo.select(i);
                found = true;
                break;
            }
        }
        if(!found){
            if(combo.getItemCount() > 0){
                combo.select(0);
            }
        }
        
        updateUI();
        
        setMessage("Select a configuration, and then choose what to include in the layout.", IMessageProvider.INFORMATION); // $NLX-ApplicationLayoutDropDialog.Selectaconfigurationandthenchoose-1$
        
        Composite columns = new Composite(mainPanel, SWT.NONE);
        columns.setLayout(SWTLayoutUtils.createLayoutNoMarginDefaultSpacing(2));
        Label l = new Label(columns, SWT.NONE);
        l.setText(""); //$NON-NLS-1$
        parent.layout(true);
        parent.pack();
    }
    

    private void initData(Composite parent) {
        
        Node appLayoutNode = panelData.getNode();

        ILoader loader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(panelData.getDesignerProject());
        
        DataNode dn = DCUtils.findDataNode(parent, true); 
        IClassDef appLayoutClassDef = ExtLibRegistryUtil.getClassDef(loader, EXT_LIB_NAMESPACE_URI, EXT_LIB_TAG_APPLICATION_LAYOUT);

        if (appLayoutNode != null) {
            dn.setClassDef(appLayoutClassDef);
            dn.setDataProvider(new SingleCollection(appLayoutNode));
        }
       
        dnAppLayout = dn;
    }
    
    
    /**
     * currently always adds the theme controls
     * @return true if the controls were added, false otherwise
     */
    private boolean addingThemeControlInfo() {
        
        // create a PreservingProperties object for the panels to use if they choose
        IFile ifile = panelData.getDesignerProject().getProject().getFile("/WebContent/WEB-INF/xsp.properties"); //$NON-NLS-1$      
        ContentFacade cf = ContentFacadeFactory.instance().getFacadeForObject(ifile);
        PreservingProperties pp = new PreservingProperties(cf, false); // false means we must call pp.save() (on OK)

        java.util.Properties props = pp.getProperties();
        
        String theme = props.getProperty("xsp.theme"); //$NON-NLS-1$

        dynamicComposite.setData("pprops", pp); //$NON-NLS-1$
        dynamicComposite.setData("ppropsopen", Boolean.valueOf(propsOpenInEditor)); //$NON-NLS-1$
        if (null != theme){
            dynamicComposite.setData("ppropstheme", theme); //$NON-NLS-1$
        }
        
        return true;
    }



    
    @Override
    protected boolean performDialogOperation(IProgressMonitor arg0) {
        if (hasFooter()) {
            addFooterLinks();
        }

        Object o = dynamicComposite.getData("pprops"); //$NON-NLS-1$
        if (o instanceof PreservingProperties) {
            PreservingProperties pp = (PreservingProperties)o;
            if (pp.isDirty()) {
                if (propsOpenInEditor) {
                    MessageDialog.openWarning(getShell(), "Domino Designer",  // $NLX-ApplicationLayoutDropDialog.Dominodesigner-1$
                       "The Xsp Properties editor is currently open for editing. Therefore the application theme you specified will not be applied.\n\nPlease choose a theme in the Xsp Properties editor (General tab)."); // $NLX-ApplicationLayoutDropDialog.Youcannotchangetheapplicationtheme-1$
                }
                else {
                    pp.save();
                }
            }
        }

        return true;
    }
    
    private Element getConfigObject() {
        
        IComplexPanel complex = dynamicComposite.getCurrentPanel();
        
        if (complex instanceof Control) {
            DataNode cn = DCUtils.findDataNode((Control)complex, true);
            if (null != cn && cn.getCurrentObject() instanceof Element) {
                Element e = (Element)cn.getCurrentObject();
                return e;
            }
        }
        else {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error("Unable to get Configuration node to add Footer defaults");  // $NLE-ApplicationLayoutDropDialog.UnabletogetConfigurationnodetoadd-1$
        }
        return null;
    }
    
    /**
     * @return
     */
    private boolean hasFooter() {
        
        Element config = getConfigObject();
        if (null == config)
            return false;
        
        String footer = config.getAttribute(EXT_LIB_ATTR_FOOTER);
        
        FacesRegistry registry = panelData.getDesignerProject().getFacesRegistry();
        ExtLibRegistryUtil.Default defFooter = ExtLibRegistryUtil.getDefaultValue(registry, EXT_LIB_TAG_ONEUI_CONFIGURATION, EXT_LIB_ATTR_FOOTER, String.valueOf(true));
        
        return (footer == null ? defFooter.toBoolean() : StringUtil.isTrueValue(footer));
    }
    
    private void addFooterLinks() {
        
        try {
            ILoader loader = XPagesPropertiesViewUtils.getXPagesMultiDomLoader(panelData.getDesignerProject());
            
            for (int i = 1; i < 3; i++) {
                Element config = getConfigObject();
                Map<String, String> props = new HashMap<String, String>();
                props.put(EXT_LIB_ATTR_LABEL, StringUtil.format("Container {0}", i)); // $NLX-ApplicationLayoutDropDialog.Subsection-1$
                Object container = ExtLibRegistryUtil.createCollectionValue(loader, EXT_LIB_TAG_ONEUI_CONFIGURATION, config, EXT_LIB_ATTR_FOOTER_LINKS, EXT_LIB_TAG_BASIC_CONTAINER_NODE, props);
                
                if (container instanceof Element) {
                    for (int j = 1; j < 3; j++) {
                        Map<String, String> leafprops = new HashMap<String, String>();
                        leafprops.put(EXT_LIB_ATTR_LABEL, StringUtil.format("Link {0}", j)); // $NLX-ApplicationLayoutDropDialog.Link-1$
                        leafprops.put(EXT_LIB_ATTR_HREF, "/");
                        ExtLibRegistryUtil.createCollectionValue(loader, EXT_LIB_TAG_BASIC_CONTAINER_NODE, (Element)container, EXT_LIB_ATTR_CHILDREN, EXT_LIB_TAG_BASIC_LEAF_NODE, leafprops);
                    }
                    
                }
            }
        }
        catch(Exception e) {
            ExtLibToolingLogger.EXT_LIB_TOOLING_LOGGER.error(e, e.toString());
        }
    }
    
    /**
     * propagate the parent's data node to the the control.
     * @param control
     */
    private void initControlDataNode(Control control, IClassDef def) {
        if (def != null) {
            DCUtils.initDataBinding(control);
            DataNode newNode = DCUtils.findDataNode(control, true);
            newNode.setClassDef(def);
            newNode.setDataProvider(new SingleCollection(dnAppLayout.getCurrentObject()));
        }
    }
    

    private void updateUI(){
        if (!combo.isDisposed()) {
            String value = combo.getValue();
            if (StringUtil.isNotEmpty(value)) {
                dynamicComposite.updatePanel(EXT_LIB_NAMESPACE_URI, value);
            }else{
                dynamicComposite.updatePanel(null, null);
            }
        }
    }
}