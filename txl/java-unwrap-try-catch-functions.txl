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
%include "generic-normalize.txl"


redefine statement
	...
|	[repeat declaration_or_statement]
end redefine

% The custom context-dependent normalizing rule you want applied

rule normalize
    replace $ [statement]
	'try '{ B [repeat declaration_or_statement] '} C [repeat catch_clause]
    by
	B
end rule

rule normalize2
    replace $ [statement]
	'try '{ B [repeat declaration_or_statement] '} C [repeat catch_clause] 'finally '{ D [repeat declaration_or_statement] '}
    construct NewSequence [repeat declaration_or_statement]
	B D
    by
	NewSequence
end rule

rule main
    % Abstract them in each potential clone
    skipping [source_unit]
    replace $ [source_unit]
	BeginXML [xml_source_coordinate]
	    PC [potential_clone]
	EndXML [end_xml_source_coordinate]
    by
	BeginXML 
	    PC [normalize] [normalize2]
	EndXML 
end rule
