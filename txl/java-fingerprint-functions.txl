% Example custom context-dependent normalization: 
% abstract only if-condition expressions in Java 
% Jim Cordy, May 2010

% Using Java grammar
include "java.grm"

define xml_source_coordinate
    '< [SPOFF] 'source [SP] 'file=[stringlit] [SP] 'startline=[stringlit] [SP] 'endline=[stringlit] '> [SPON] [NL]
end define

define end_xml_source_coordinate
    [NL] '< [SPOFF] '/ 'source '> [SPON] [NL]
end define

define source_unit  
    [xml_source_coordinate]
        [potential_clone]
    [end_xml_source_coordinate]
end define

redefine program
    [repeat source_unit]
end redefine

define method_definition
    [method_header]
    '{  [NL][IN] 
	[repeat declaration_or_statement]  [EX]
    '} 
end define

define method_header
	[repeat modifier] [opt generic_parameter] [opt type_specifier] [method_declarator] [opt throws]
end define

define potential_clone
    [method_definition]
end define

% Generic normalization

rule main
    % Abstract them in each potential clone
    skipping [source_unit]
    replace $ [source_unit]
		BeginXML [xml_source_coordinate]
	 	   PC [potential_clone]
		EndXML [end_xml_source_coordinate]
    by
		BeginXML 
	 	   PC [normalizeif][normalizeswitch][normalizewhile][normalizedo][normalizefor][normalizeforin][normalizecatch][normalizeheader]
		EndXML 
end rule

% The custom context-dependent normalizing rule you want applied

redefine method_header
	...
|	'method_header
end redefine

rule normalizeheader
	replace $ [method_header]
		A [repeat modifier] B [opt generic_parameter] C [opt type_specifier] D [method_declarator] E [opt throws]
	by
		'method_header
end rule

redefine if_statement
	...
|	'if
		[statement] 
	[opt else_clause]   [NL]
end redefine

rule normalizeif
    replace $ [if_statement]
        'if '( Condition [expression] ')     
            ThenPart [statement]
        ElsePart [opt else_clause]      
    by
        'if  
            ThenPart 
        ElsePart 
end rule

redefine switch_statement
	...
|	'switch [switch_block]   [NL]
end redefine

rule normalizeswitch
    replace $ [switch_statement]
        'switch '( Condition [expression] ') SwitchBlock [switch_block]
    by
        'switch SwitchBlock
end rule

redefine while_statement
	...
|	'while
		[statement]   [NL]
end redefine

rule normalizewhile
	replace $ [while_statement]
		'while '( Condition [condition] ') 
			Statement [statement]
	by
		'while
			Statement
end rule

redefine do_statement
	...
|	'do
		[statement]
	'while ';   [NL]
end redefine

rule normalizedo
	replace $ [do_statement]
		'do
			Statement [statement]
		'while '( Condition [condition] ') ';
	by
		'do
			Statement
		'while ';
end rule

redefine for_statement
	...
|	'for
        [statement]                            [NL]
end define

rule normalizefor
	replace $ [for_statement]
    	'for '( Init [for_init] Expression [for_expression] Update [for_update] ')
        	Statement [statement]
	by
		'for
			Statement
end rule

redefine for_in_statement
	...
|	'for
		[statement]   [NL]
end redefine

rule normalizeforin
	replace $ [for_in_statement]
		'for '( Init [for_in_init] : Expression [expression] ')
			Statement [statement]
	by
		'for
			Statement
end rule

redefine catch_clause
	...
|    'catch [block]  % July 15
end define

rule normalizecatch
	replace $ [catch_clause]
		    'catch '( A [repeat modifier] B [type_specifier] C [variable_name] ') D [block]  % July 15
	by
		'catch D
end rule

rule abstract AbstractedNTstring [stringlit]
    construct AbstractedNT [id]
        _ [unquote AbstractedNTstring]
    % Replace all the given NTs by their nonterminal name
    deconstruct * [any] AbstractedNT
        AbstractedNTname [any]
    replace $ [any]
        Any [any]
    where
        Any [istype AbstractedNT]
    by
        AbstractedNTname
end rule
