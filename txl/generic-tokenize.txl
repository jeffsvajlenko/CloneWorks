% Tokenizes code in extraced functions or blocks
% Jeff Svajlenko, June 2015
% Dependent and based on code by: Jim Cordy, May 2010

#pragma -raw -width 32767

define programelement
    	'< [SPOFF] 'source [SP] 'file=[stringlit] [SP] 'startline=[stringlit] [SP] 'endline=[stringlit] '> [SPON] [NL]
|    	'< [SPOFF] '/ 'source '> [SPON] [NL]
|	[token] [NL]
|	[key] [NL]
end define

redefine program
    [repeat programelement]
end define

% Main program

rule main
    match [program]
	P [program]
end rule

