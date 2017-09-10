<source file="systems/argouml/src/argouml-app/src/org/argouml/uml/diagram/activity/ui/ActivityDiagramRenderer.java" startline="90" endline="109">
    public FigNode getFigNodeFor(GraphModel gm, Layer lay, Object node,
            Map styleAttributes) {

        FigNode figNode = null;
        // Although not generally true for GEF, for Argo we know that the layer
        // is a LayerPerspective which knows the associated diagram
        Diagram diag = ((LayerPerspective) lay).getDiagram(); 
        if (diag instanceof UMLDiagram
            && ((UMLDiagram) diag).doesAccept(node)) {
            figNode = (FigNode) ((UMLDiagram) diag).drop(node, null);
        } else {
            figNode =  super.getFigNodeFor(gm, lay, node, styleAttributes);
            if (figNode == null) {
                return null;
            }
        }

        lay.add(figNode);
        return figNode;
    }
</source>
<source file="systems/argouml/src/argouml-app/src/org/argouml/uml/diagram/deployment/ui/DeploymentDiagramRenderer.java" startline="85" endline="104">
    public FigNode getFigNodeFor(
            GraphModel gm,
            Layer lay,
            Object node,
            Map styleAttributes) {

        FigNode figNode = null;

        // Although not generally true for GEF, for Argo we know that the layer
        // is a LayerPerspective which knows the associated diagram
        Diagram diag = ((LayerPerspective) lay).getDiagram(); 
        if (((UMLDiagram) diag).doesAccept(node)) {
            figNode = (FigNode) ((UMLDiagram) diag).drop(node, null);
        } else {
            LOG.debug("TODO: DeploymentDiagramRenderer getFigNodeFor");
            return null;
        }
        lay.add(figNode);
        return figNode;
    }
</source>
<source file="systems/JHotDraw54b1/src/CH/ifa/draw/standard/CompositeFigure.java" startline="277" endline="289">
	private void assignFiguresToPredecessorZValue(int lowerBound, int upperBound) {
		int x = 0;
		int y = lowerBound;
		int z = 1+2;
	}
</source>
