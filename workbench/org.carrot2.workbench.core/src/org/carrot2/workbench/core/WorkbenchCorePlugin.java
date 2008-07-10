package org.carrot2.workbench.core;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.workbench.core.preferences.PreferenceConstants;
import org.carrot2.workbench.core.ui.SearchEditor;
import org.carrot2.workbench.core.ui.SearchEditorSections;
import org.carrot2.workbench.core.ui.SearchEditor.SectionReference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class (plug-in's entry point), controls the life-cycle and contains a
 * reference to the Carrot2 {@link Controller}.
 */
public class WorkbenchCorePlugin extends AbstractUIPlugin
{
    /** Plug-in ID. */
    public static final String PLUGIN_ID = "org.carrot2.workbench.core";

    /** Source extension identifier. */
    public static final String SOURCE_EXTENSION_ID = "org.carrot2.core.source";

    /** Algorithm extension identifier. */
    public static final String ALGORITHM_EXTENSION_ID = "org.carrot2.core.algorithm";

    /** The shared instance. */
    private static WorkbenchCorePlugin plugin;

    /**
     * Shared, thread-safe caching controller instance.
     */
    private CachingController controller;

    /**
     * Extension point implementations for {@link DocumentSource}.
     */
    private ExtensionLoader sources;

    /**
     * Extension point implementations for {@link ClusteringAlgorithm}.
     */
    private ExtensionLoader algorithms;

    /*
     * 
     */
    @SuppressWarnings("unchecked")
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;

        controller = new CachingController(DocumentSource.class);
        controller.init(new HashMap<String, Object>());

        sources = new ExtensionLoader(SOURCE_EXTENSION_ID, "source");
        algorithms = new ExtensionLoader(ALGORITHM_EXTENSION_ID, "algorithm");
    }

    /*
     * 
     */
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;

        controller.dispose();
        controller = null;

        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static WorkbenchCorePlugin getDefault()
    {
        return plugin;
    }

    /**
     * Returns an initialized shared controller instance.
     */
    public CachingController getController()
    {
        return controller;
    }
    
    public ExtensionLoader getSources()
    {
        return sources;
    }
    
    public ExtensionLoader getAlgorithms()
    {
        return algorithms;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
    
    /**
     * Restore the state of editor's sections from the most recent global state.
     */
    public void restoreSectionsState(
        EnumMap<SearchEditorSections, SearchEditor.SectionReference> sections)
    {
        final IPreferenceStore store = getPreferenceStore();
        for (Map.Entry<SearchEditorSections, SearchEditor.SectionReference> s 
            : sections.entrySet())
        {
            final SearchEditorSections section = s.getKey();
            final SectionReference ref = s.getValue();

            final String key = PreferenceConstants.getSectionWeightKey(section);
            if (!store.isDefault(key))
            {
                ref.weight = store.getInt(key);
            }
        }
    }

    /**
     * Keep a reference to the most recently updated editor's sections.
     */
    public void storeSectionsState(
        EnumMap<SearchEditorSections, SectionReference> sections)
    {
        final IPreferenceStore store = getPreferenceStore();
        for (Map.Entry<SearchEditorSections, SearchEditor.SectionReference> s 
            : sections.entrySet())
        {
            final SearchEditorSections section = s.getKey();
            final SectionReference ref = s.getValue();

            final String key = PreferenceConstants.getSectionWeightKey(section);
            store.setValue(key, ref.weight);
        }
    }
}
