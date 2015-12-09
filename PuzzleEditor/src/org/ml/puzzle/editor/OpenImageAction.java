package org.ml.puzzle.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.ml.puzzle.editor.OpenImageAction"
)
@ActionRegistration(
        displayName = "#CTL_OpenImageAction"
)
@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_OpenImageAction=Open Image")
public final class OpenImageAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        File home = new File(System.getProperty("user.home"));
        File[] filesFromDisk = new FileChooserBuilder("user-dir").
                setTitle("Open File").
                setDefaultWorkingDirectory(home).
                setApproveText("Open").showMultiOpenDialog();
        if (filesFromDisk != null) {
            for (File fileFromDisk : filesFromDisk) {
                PuzzleTopComponent tc = new PuzzleTopComponent(fileFromDisk);
                tc.setDisplayName(fileFromDisk.getName());
                tc.open();
                tc.requestActive();
            }
        }
    }
}
