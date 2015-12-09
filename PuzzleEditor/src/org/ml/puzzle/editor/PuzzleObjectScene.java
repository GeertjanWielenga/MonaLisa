package org.ml.puzzle.editor;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import javax.swing.ImageIcon;
import org.ml.model.PuzzlePiece;
import org.ml.model.node.PuzzlePieceSelectionMediator;
import org.ml.model.capabilities.Synchronizable;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.MoveProvider;
import org.netbeans.api.visual.action.TwoStateHoverProvider;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.model.ObjectScene;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.api.visual.widget.general.IconNodeWidget;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

class PuzzleObjectScene extends ObjectScene implements LookupListener {

    int ROWS = 2;
    int COLUMNS = 2;

    int width;
    int height;

    String name;

    int correctPuzzlePieces = 0;

    private Lookup.Result<Synchronizable> synchronizablesInGlobalLookup;

    public PuzzleObjectScene(
            TopComponent tc,
            InstanceContent sceneInstanceContent,
            ImageIcon mona,
            File file) {

        LayerWidget layerWidget = new LayerWidget(this);

        Image source = mona.getImage();
        width = mona.getIconWidth();
        height = mona.getIconHeight();

        name = file.getName();

        synchronizablesInGlobalLookup = Utilities.actionsGlobalContext().lookupResult(Synchronizable.class);
        synchronizablesInGlobalLookup.addLookupListener(WeakListeners.create(LookupListener.class, this, synchronizablesInGlobalLookup));

        Random generator = new Random();

        for (int i = 0; i < ROWS; i++) {

            for (int j = 0; j < COLUMNS; j++) {

                Image image = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(source.getSource(),
                                new CropImageFilter(j * width / COLUMNS, i * height / ROWS,
                                        (width / COLUMNS), height / ROWS)));

                PuzzleWidget puzzleWidget = new PuzzleWidget(this, image,
                        generator.nextInt(600), generator.nextInt(300), tc);

                layerWidget.addChild(puzzleWidget);

                PuzzlePiece puzzle = new PuzzlePiece(name, j, i);

                puzzleWidget.setLabel(name + ": " + puzzle.getRow() + "/" + puzzle.getColumn());


                addObject(puzzle, puzzleWidget);

                sceneInstanceContent.add(puzzle);

            }

        }

        addChild(layerWidget);

    }

    WidgetAction puzzleMoveAction;

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends Synchronizable> allSynchronizables = synchronizablesInGlobalLookup.allInstances();
        for (Synchronizable s : allSynchronizables) {
            PuzzlePiece puzzlePiece = s.synchronize();
            int row = puzzlePiece.getRow();
            int col = puzzlePiece.getColumn();
            String msg = name + ": " + row + "/" + col;
            Set<?> objects = this.getObjects();
            for (Object object : objects) {
                PuzzlePiece puzzle = (PuzzlePiece) object;
                int rowInScene = puzzle.getRow();
                int colInScene = puzzle.getColumn();
                String textInScene = name+": " + rowInScene + "/" + colInScene;
                if (textInScene.equals(msg)) {
                    StatusDisplayer.getDefault().setStatusText(textInScene);
                    Widget w = this.findWidget(puzzle);
                    this.setFocusedWidget(w);
                }
            }
        }

    }

    class PuzzleWidget extends IconNodeWidget {

        private WidgetSavable mySavable;

        private Lookup lookup;
        private TopComponent tc;
        private InstanceContent savableWidgetContent;

        public PuzzleWidget(final ObjectScene scene, Image image, int x, int y, TopComponent tc) {
            super(scene);
            setPreferredLocation(new Point(x, y));
            setImage(image);
            this.tc = tc;
            savableWidgetContent = new InstanceContent();
            puzzleMoveAction = ActionFactory.createMoveAction(
                    ActionFactory.createSnapToGridMoveStrategy(10, 10),
                    new PuzzleMoveProvider());
            getActions().addAction(puzzleMoveAction);
            WidgetAction hoverAction = ActionFactory.createHoverAction(new TwoStateHoverProvider() {
                @Override
                public void unsetHovering(Widget w) {
                    w.setBorder(BorderFactory.createEmptyBorder());
                }

                @Override
                public void setHovering(Widget w) {
                    w.setBorder(BorderFactory.createLineBorder(8, Color.BLACK));
                    ExplorerManager em = PuzzlePieceSelectionMediator.getExplorerManager();
                    Node[] nodes = em.getRootContext().getChildren().getNodes();
                    for (Node node : nodes) {
                        if (getLabelWidget().getLabel().equals(node.getDisplayName())) {
                            try {
                                em.setSelectedNodes(new Node[]{node});
                            } catch (PropertyVetoException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                    scene.setFocusedWidget(w);
                }
            });
            getActions().addAction(hoverAction);
            scene.getActions().addAction(hoverAction);
        }

        private void modify() {
            if (getLookup().lookup(WidgetSavable.class) == null) {
                savableWidgetContent.add(mySavable = new WidgetSavable());
            }
        }

        @Override
        public Lookup getLookup() {
            if (lookup == null) {
                lookup = new AbstractLookup(savableWidgetContent);
            }
            return lookup;
        }

        private class WidgetSavable extends AbstractSavable {

            public WidgetSavable() {
                register();
            }

            TopComponent tc() {
                return tc;
            }

            @Override
            protected String findDisplayName() {
                return getLabelWidget().getLabel();
            }

            @Override
            protected void handleSave() throws IOException {
                savableWidgetContent.remove(mySavable);
                unregister();
            }

            @Override
            public boolean equals(Object obj) {
//                if (obj instanceof MySavable) {
//                    MySavable m = (MySavable) obj;
//                    return tc() == m.tc();
//                }
                return false;
            }

            @Override
            public int hashCode() {
                return tc().hashCode();
            }
        }

        private class PuzzleMoveProvider implements MoveProvider {

            @Override
            public void movementStarted(Widget widget) {
            }

            @Override
            public void movementFinished(Widget widget) {
                PuzzlePiece puzzleObject = (PuzzlePiece) findObject(widget);
                Point newLoc = widget.getPreferredLocation();
                int newRow = newLoc.y / (height / ROWS);
                int newCol = newLoc.x / (width / COLUMNS);
                if (puzzleObject.getColumn() == newCol && puzzleObject.getRow() == newRow) {
                    correctPuzzlePieces++;
                    if (correctPuzzlePieces == ROWS * COLUMNS) {
                        StatusDisplayer.getDefault().setStatusText("Game Over!");
                    } else {
                        StatusDisplayer.getDefault().setStatusText("Correct! Total: " + correctPuzzlePieces);
                    }
                    //Prevent the piece from being moved after we know it is correct:
//                    widget.getActions().removeAction(puzzleMoveAction);
                } else {
                    StatusDisplayer.getDefault().setStatusText("---");
                }
//                setLabel("Position: " + newLoc.x + "/" + newLoc.y);
                modify();
            }

            @Override
            public Point getOriginalLocation(Widget widget) {
                return widget.getPreferredLocation();
            }

            @Override
            public void setNewLocation(Widget widget, Point location) {
                widget.setPreferredLocation(location);
            }

        }

    }

}
