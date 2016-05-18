% Using Java grammar
include "csharp.grm"

define potential_clone
    [compilation_unit]
end define

%blind renaming
include "generic-rename-blind.txl"
