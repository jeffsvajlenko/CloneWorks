% Consistent renaming - Java Files
% Jeffrey Svajlenko, June, 2013
% Based on java functions version, Jim Cordy, May 2010

% Using Java grammar
include "csharp.grm"

define potential_clone
    [compilation_unit]
end define

% Generic nonterminal filtering
include "generic-filter.txl"
