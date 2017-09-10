public class MyClass {
    public void setShowBidirectionalArrows(final boolean showem) {
        if (showBidirectionalArrows != null
                && showBidirectionalArrows == showem) {
            return;
        }

        Memento memento = new Memento() {
            public void redo() {
                showBidirectionalArrows = showem;
            }

            public void undo() {
                showBidirectionalArrows = !showem;
            }
        };
        doUndoable(memento);

    }
}
