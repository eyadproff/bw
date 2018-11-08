package sa.gov.nic.bio.bw.core.utils;

import java.lang.StackWalker.Option;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Java9Scanner {
	
	public static final class CallerResolver extends SecurityManager {
		/** Get classes in the call stack. */
		@Override
		protected Class<?>[] getClassContext() {
			return super.getClassContext();
		}
	}
	
	/** Recursively find the topological sort order of ancestral layers. */
	public static void findLayerOrder(ModuleLayer layer,
	                                   Set<ModuleLayer> visited, Deque<ModuleLayer> layersOut) {
		if (visited.add(layer)) {
			List<ModuleLayer> parents = layer.parents();
			for (int i = 0; i < parents.size(); i++) {
				findLayerOrder(parents.get(i), visited, layersOut);
			}
			layersOut.push(layer);
		}
	}
	
	/** Get ModuleReferences from a Class reference. */
	public static List<Entry<ModuleReference, ModuleLayer>> findModuleRefs(
			Class<?>[] callStack) {
		Deque<ModuleLayer> layerOrder = new ArrayDeque<>();
		Set<ModuleLayer> visited = new HashSet<>();
		for (int i = 0; i < callStack.length; i++) {
			ModuleLayer layer = callStack[i].getModule().getLayer();
			findLayerOrder(layer, visited, layerOrder);
		}
		Set<ModuleReference> addedModules = new HashSet<>();
		List<Entry<ModuleReference, ModuleLayer>> moduleRefs = new ArrayList<>();
		for (ModuleLayer layer : layerOrder) {
			Set<ResolvedModule> modulesInLayerSet = layer.configuration()
					.modules();
			final List<Entry<ModuleReference, ModuleLayer>> modulesInLayer =
					new ArrayList<>();
			for (ResolvedModule module : modulesInLayerSet) {
				modulesInLayer
						.add(new SimpleEntry<>(module.reference(), layer));
			}
			// Sort modules in layer by name for consistency
			Collections.sort(modulesInLayer,
			                 (e1, e2) -> e1.getKey().descriptor().name()
					                 .compareTo(e2.getKey().descriptor().name()));
			// To be safe, dedup ModuleReferences, in case a module occurs in multiple
			// layers and reuses its ModuleReference (no idea if this can happen)
			for (Entry<ModuleReference, ModuleLayer> m : modulesInLayer) {
				if (addedModules.add(m.getKey())) {
					moduleRefs.add(m);
				}
			}
		}
		return moduleRefs;
	}
	
	/** Get the classes in the call stack. */
	public static Class<?>[] getCallStack() {
		// Try StackWalker (JDK 9+)
		PrivilegedAction<Class<?>[]> stackWalkerAction = new PrivilegedAction<Class<?>[]>() {
			@Override
			public Class<?>[] run() {
				List<Class<?>> stackFrameClasses = new ArrayList<>();
				StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
						.forEach(sf -> stackFrameClasses
								.add(sf.getDeclaringClass()));
				return stackFrameClasses.toArray(new Class<?>[0]);
			}
		};
		try {
			// Try with doPrivileged()
			return AccessController
					.doPrivileged(stackWalkerAction);
		} catch (Exception e) {
		}
		try {
			// Try without doPrivileged()
			return stackWalkerAction.run();
		} catch (Exception e) {
		}
		
		// Try SecurityManager
		PrivilegedAction<Class<?>[]> callerResolverAction = new PrivilegedAction<Class<?>[]>() {
			@Override
			public Class<?>[] run() {
				return new CallerResolver().getClassContext();
			}
		};
		try {
			// Try with doPrivileged()
			return AccessController
					.doPrivileged(callerResolverAction);
		} catch (Exception e) {
		}
		try {
			// Try without doPrivileged()
			return callerResolverAction.run();
		} catch (Exception e) {
		}
		
		// As a fallback, use getStackTrace() to try to get the call stack
		try {
			throw new Exception();
		} catch (final Exception e) {
			final List<Class<?>> classes = new ArrayList<>();
			for (final StackTraceElement elt : e.getStackTrace()) {
				try {
					classes.add(Class.forName(elt.getClassName()));
				} catch (final Throwable e2) {
					// Ignore
				}
			}
			if (classes.size() > 0) {
				return classes.toArray(new Class<?>[0]);
			} else {
				// Last-ditch effort -- include just this class in the call stack
				return new Class<?>[] { Java9Scanner.class };
			}
		}
	}
	
	/**
	 * Return true if the given module name is a system module. There can be
	 * system modules in layers above the boot layer.
	 */
	public static boolean isSystemModule(
			final ModuleReference moduleReference) {
		URI location = moduleReference.location().orElse(null);
		if (location == null) {
			return true;
		}
		final String scheme = location.getScheme();
		return scheme != null && scheme.equalsIgnoreCase("jrt");
	}
}
