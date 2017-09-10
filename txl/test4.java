<source file="systems/argouml/src/argouml-app/src/org/argouml/uml/ui/foundation/core/ActionAddOperation.java" startline="76" endline="97">
    public static ActionAddOperation getTargetFollower() {
        if (targetFollower == null) {
            targetFollower  = new ActionAddOperation();
            TargetManager.getInstance().addTargetListener(new TargetListener() {
                public void targetAdded(TargetEvent e) {
                    setTarget();
                }
                public void targetRemoved(TargetEvent e) {
                    setTarget();
                }

                public void targetSet(TargetEvent e) {
                    setTarget();
                }
                private void setTarget() {
                    targetFollower.setEnabled(targetFollower.shouldBeEnabled());
                }
            });
            targetFollower.setEnabled(targetFollower.shouldBeEnabled());
        }
        return targetFollower;
    }
</source>
<source file="systems/argouml/src/argouml-app/src/org/argouml/uml/diagram/ui/ActionAddExtensionPoint.java" startline="107" endline="136">
    public static ActionAddExtensionPoint singleton() {

        // Create the singleton if it does not exist, and then return it

        if (singleton == null) {
            singleton = new ActionAddExtensionPoint();

            // When a new target is selected, we have to check if it 's a use case.
            //Then, the icone "add extension point" have to become enabled.
            TargetManager.getInstance().addTargetListener(new TargetListener() {

                public void targetAdded(TargetEvent e) {
                    setTarget();
                }
                public void targetRemoved(TargetEvent e) {
                    setTarget();
                }

                public void targetSet(TargetEvent e) {
                    setTarget();
                }
                private void setTarget() {
                    singleton.setEnabled(singleton.shouldBeEnabled());
                }
            });
            singleton.setEnabled(singleton.shouldBeEnabled());
        }

        return singleton;
    }
</source>
