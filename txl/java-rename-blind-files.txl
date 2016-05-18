% Using Java grammar
include "java.grm"

define potential_clone
    [package_declaration]
end define

% Generic blind renaming
include "generic-rename-blind.txl"
