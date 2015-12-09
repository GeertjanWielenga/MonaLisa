package org.ml.model.node;

import java.beans.IntrospectionException;
import java.util.List;
import org.ml.model.PuzzlePiece;
import org.ml.model.utils.StickyLookup;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

public class PuzzleChildFactory extends ChildFactory.Detachable<PuzzlePiece> implements LookupListener {

    private Lookup.Result<PuzzlePiece> puzzlePiecesInGlobalLookup;

    @Override
    protected void addNotify() {
        Lookup lookup = new StickyLookup(Utilities.actionsGlobalContext(), PuzzlePiece.class);
        this.puzzlePiecesInGlobalLookup = lookup.lookupResult(PuzzlePiece.class);
        this.puzzlePiecesInGlobalLookup.addLookupListener(this);
    }

    @Override
    protected void removeNotify() {
        this.puzzlePiecesInGlobalLookup.removeLookupListener(this);
    }

    @Override
    protected boolean createKeys(List<PuzzlePiece> list) {
        list.addAll(puzzlePiecesInGlobalLookup.allInstances());
        return true;
    }

    @Override
    protected Node createNodeForKey(PuzzlePiece key) {
        PuzzleNode node = null;
        try {
            node = new PuzzleNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        refresh(true);
    }

}
