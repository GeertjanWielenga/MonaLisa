package org.ml.model.node;

import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.ml.model.PuzzlePiece;
import org.ml.model.capabilities.Synchronizable;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

public class PuzzleNode extends BeanNode {
    
    public PuzzleNode(PuzzlePiece bean) throws IntrospectionException {
        this(bean, new InstanceContent());
    }
    
    private PuzzleNode(final PuzzlePiece bean, InstanceContent ic) throws IntrospectionException {
        super(bean, Children.LEAF, new AbstractLookup(ic));
        ic.add(new Synchronizable() {
            @Override
            public PuzzlePiece synchronize() {
                return bean;
            }
        });
        setDisplayName(bean.getName() + ": " + bean.getRow() + "/" + bean.getColumn());
    }

    @Override
    public Action getPreferredAction() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
    }
    
}
