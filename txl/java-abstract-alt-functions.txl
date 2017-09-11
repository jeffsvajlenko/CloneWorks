% Abstract given nonterminals from potential clones - Java functions version

% Jim Cordy, May 2010

% Using Java grammar
include "java.grm"


redefine if_statement
	...
|	'if
		[statement] 
	[opt else_clause]   [NL]
end redefine

redefine switch_statement
	...
|	'switch [switch_block]   [NL]
end redefine

redefine while_statement
	...
|	'while
		[statement]   [NL]
end redefine

redefine do_statement
	...
|	'do
		[statement]
	'while ';   [NL]
end redefine

redefine for_statement
	...
|	'for
        [statement]                            [NL]
end define

redefine for_in_statement
	...
|	'for
		[statement]   [NL]
end redefine

redefine catch_clause
	...
|    'catch [block]  % July 15
end define

define method_definition
    [method_header]
    '{  [NL][IN] 
	[repeat declaration_or_statement]  [EX]
    '} 
end define

define method_header
	[repeat modifier] [opt generic_parameter] [opt type_specifier] [method_declarator] [opt throws] 
end define

redefine method_header
	...
|	'method_header
end define

define potential_clone
    [method_definition]
end define

% Generic nonterminal abstraction
include "generic-abstract.txl"

