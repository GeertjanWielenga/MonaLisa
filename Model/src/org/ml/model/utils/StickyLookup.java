package org.ml.model.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

public class StickyLookup extends ProxyLookup implements LookupListener {
    private final Lookup.Result result;
    private final InstanceContent ic;

    public StickyLookup(final Lookup lookup, final Class<?> clazz) {
        this(lookup, clazz, new InstanceContent());
    }

    private StickyLookup(final Lookup lookup, final Class<?> clazz, InstanceContent ic) {
        super(Lookups.exclude(lookup, clazz), new AbstractLookup(ic));
        this.ic = ic;
        this.result = lookup.lookupResult(clazz);
        this.ic.set(this.result.allInstances(), null);
        this.result.addLookupListener(this);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (this.result.allInstances().isEmpty()) {
            // Wrapped lookup is empty. We pretend like nothing happened and keep 
            // exposing the same instances as before.
            return;
        } else {
            // Just copy whatever the wrapped instance has
            ic.set(result.allInstances(), null);
        }
    }
}