package com.ibm.xsp.extlib.designer.common.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;

public class ThemeLookupEntryContributions {

	private static List<ThemeLookupEntry> themes = null;
	
	private static final String ITHEMENAMEPROVIDER_ID = "com.ibm.xsp.extlib.designer.themeLookupEntryProvider";

	private static void clearThemes() {
		themes.clear();
	}
	
	private static void initThemeNames() {

		if (themes == null) {
			themes = new ArrayList<ThemeLookupEntry>(); 
			IExtensionRegistry registry = Platform.getExtensionRegistry();        
			execute(registry);
		}

	}
	
	public static List<ThemeLookupEntry> getThemeLookupEntries() {
		
		initThemeNames();		
		return new ArrayList<ThemeLookupEntry>(themes);		
	}
	
	private static void addTheme(String code, String label) {		
		themes.add(new ThemeLookupEntry(code, label));		
	}		
	
	private static void execute(IExtensionRegistry registry) {

		IConfigurationElement[] config =
				registry.getConfigurationElementsFor(ITHEMENAMEPROVIDER_ID);
		
		try {
		
			ThemeLookupEntryContributions.clearThemes();
			
			for (IConfigurationElement e : config) {
				System.out.println("Evaluating Theme Name Provider Extension");
			
				final Object o =
						e.createExecutableExtension("class");
				if (o instanceof ThemeLookupEntryProvider) {
					executeExtension(o);
				}
			}
		} catch (CoreException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	private static void executeExtension(final Object o) {
		ISafeRunnable runnable = new ISafeRunnable() {
			
			public void run() throws Exception {

				ThemeLookupEntryProvider provider = (ThemeLookupEntryProvider)o;
				
				for (ThemeLookupEntry entry : provider.getThemes()) {
					ThemeLookupEntryContributions.addTheme(entry.getCode(), entry.getLabel());
				}
								
			}
			
			public void handleException(Throwable arg0) {
				System.out.println("Exception in client");
			}
		};
		SafeRunner.run(runnable);
	}
	
}
