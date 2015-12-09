# Mona Lisa

NetBeans Platform application to illustrate usage of Global Lookup as communication mechanism between TopComponents, Nodes, and data.

<ol>
<li>One or more images are added by the user via File | Open Image.</li>
<li>For each image, a new instance of PuzzleTopComponent is created.</li>
<li>The image is sliced into 4 pieces, for each piece an IconNodeWidget is put into an ObjectScene in the PuzzleTopComponent.</li>
<li>During creation of PuzzleTopComponent instances, a PuzzlePiece object is put into the Lookup of the TopComponent, for each piece in the puzzle.</li>
<li>When the Navigator opens (from the Window menu), its ChildFactory listens to the Global Lookup for new PuzzlePiece objects.
<li>When a PuzzleTopComponent instance is selected, its PuzzlePiece objects are automatically put into the Global Lookup, causing the ChildFactory to populate the node hierarchy.
<li>Hovering over an IconNodeWidget results in the ExplorerManager (in the Model module) to be inspected for a matching Node, which is then selected.</li>
<li>When a Node is selected, a Synchronizable capability is put into the Global Lookup, for which the Scene is listening, and when a new Synchronizable is found, the matching IconNodeWidget is found and is given a border to highlight it.

</ol>

Details here:

<ul>
<li><a href="https://blogs.oracle.com/geertjan/entry/mona_lisa_communication_sample_for">https://blogs.oracle.com/geertjan/entry/mona_lisa_communication_sample_for</a></li>
</ul>