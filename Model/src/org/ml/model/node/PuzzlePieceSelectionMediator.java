package org.ml.model.node;

import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

public class PuzzlePieceSelectionMediator {

    private static ExplorerManager em = null;

    public static ExplorerManager getExplorerManager() {
        if (em == null) {
            em = new ExplorerManager();
            AbstractNode rootNode = new AbstractNode(Children.create(new PuzzleChildFactory(), true));
            em.setRootContext(rootNode);
        }
        return em;
    }

}
