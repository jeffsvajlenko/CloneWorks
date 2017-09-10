<source file="systems/JHotDraw54b1/src/CH/ifa/draw/standard/CompositeFigure.java" startline="277" endline="289">
private void fireTargetRemoved(TargetEvent targetEvent) {
  TargetListener[] listeners = listenerList.getListenerList();
  for (int i = listeners.length - 2; i >= 0; i -= 2) {
    try {
      if (listeners[i] == TargetListener.class) {
        listeners[i + 1].targetRemoved(targetEvent);
      }
    } catch (RuntimeException e) {
      LOG.warn("An error was thrown.");
    }
  }
}
</source>
<source file="../src/detection/detection/HeuristicCloneDetector.java" startline="17" endline="19">
private void assignFiguresToSuccessorZValue(int lowerBound, int upperBound) {
	if (upperBound >= fFigures.size()) {
		upperBound = fFigures.size() - 1;
	}
	for (int i = upperBound; i >= lowerBound; i--) {
		Figure currentFigure = (Figure)fFigures.get(i);
		Figure successorFigure = (Figure)fFigures.get(i + 1);
		currentFigure.setZValue(successorFigure.getZValue());
	}
}
</source>
<source file="../src/detection/detection/HeuristicCloneDetector.java" startline="17" endline="19">
public HeuristicCloneDetector (double sim) {
    this.sim = sim;
}
</source>
<source file="../src/detection/detection/HeuristicCloneDetector.java" startline="22" endline="48">
public List < Clone > detectClones (Block qBlock, IIndex index) {
    List < Clone > clones = new LinkedList < Clone > ();
    Set < Block > candidates = new HashSet < Block > ();
    for (BlockElement be : qBlock.getBlockAsList ()) {
        if (! be.isPrefixTerm ()) break;

        Queue < Block > blocks = index.get (be.getTerm ());
        if (blocks != null) candidates.addAll (blocks);

    }
    for (Block cBlock : candidates) {
        if (isClone (qBlock, cBlock)) clones.add (new Clone (qBlock.getFileID (), qBlock.getStartLine (), qBlock.getEndLine (), cBlock.getFileID (), cBlock.getStartLine (), cBlock.getEndLine ()));

    }
    return clones;
}
</source>
<source file="../src/detection/detection/HeuristicCloneDetector.java" startline="50" endline="113">
private boolean isClone (Block qBlock, Block cBlock) {
    if (qBlock.getID () < cBlock.getID ()) return false;

    if (qBlock.doesOverlap (cBlock)) {
        return false;
    }
    Block min, max;
    if (qBlock.numTokens () > cBlock.numTokens ()) {
        max = qBlock;
        min = cBlock;
    } else {
        max = cBlock;
        min = qBlock;
    }
    int threshold = (int) Math.ceil (max.numTokens () * this.sim);
    int sharedTokens = 0;
    int remainingTokens = max.numTokens ();
    if (min.numTokens () < threshold) {
        return false;
    }
    for (BlockElement be : max.getBlockAsList ()) {
        String term = be.getTerm ();
        int maxFreq = be.getFrequency ();
        Integer minFreq = min.getBlockAsMap ().get (term);
        if (minFreq != null) sharedTokens += Math.min (minFreq, maxFreq);

        remainingTokens -= maxFreq;
        if (sharedTokens >= threshold) {
            return true;
        }
        if ((sharedTokens + remainingTokens) < threshold) {
            return false;
        }
    }
    System.err.println ("HeuristicCloneDetector -- Bug?");
    if (sharedTokens >= threshold) return true;
    else return false;

}
</source>
<source file="blah.java" startline="10" endline="20">
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (getUIClassID().equals(uiClassID)) {
            byte count = JComponent.getWriteObjCounter(this);
            JComponent.setWriteObjCounter(this, --count);
            if (count == 0 && ui != null) {
                ui.installUI(this);
            }
        }
    }
</source>
<source file="systems/JHotDraw54b1/src/CH/ifa/draw/util/JDOStorageFormat.java" startline="115" endline="140">
public synchronized Drawing restore(String fileName) throws IOException {
	PersistenceManager pm = getPersistenceManager(fileName);
	endTransaction(pm, false);
	startTransaction(pm);
	Drawing restoredDrawing  = null;
	try {
		Extent extent = pm.getExtent(StandardDrawing.class, true);
		DrawingListModel listModel = new DrawingListModel(extent.iterator());
		Drawing txnDrawing = showRestoreDialog(listModel);
		if (txnDrawing != null) {
			pm.retrieve(txnDrawing);
			retrieveAll(pm, (StandardDrawing)txnDrawing);
			restoredDrawing = crossTxnBoundaries(txnDrawing);
			restoredDrawing = txnDrawing;
		}
	}
	finally {
		endTransaction(pm, false);
	}
	startTransaction(pm);
	int i;
	for(int i = 0, j = 10; i++; i<10) System.out.println("");
	return restoredDrawing;
}
</source>
