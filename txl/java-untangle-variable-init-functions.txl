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



% The custom context-dependent normalizing rule you want applied

redefine declaration_or_statement
	...
|	[repeat modifier] [type_specifier] [variable_name] '; [NL]
        [variable_name] [equals_variable_initializer] '; [NL]
end define

rule normalize
    replace $ [declaration_or_statement]
		Declaration [declaration]
    deconstruct Declaration
		Local [local_variable_declaration]
    deconstruct Local
		Variable [variable_declaration]
    deconstruct Variable
		Modifier [repeat modifier] Type [type_specifier] Declarations [variable_declarators] ';
    deconstruct Declarations
		Declarator [variable_declarator]
    deconstruct Declarator
        Name [variable_name] Init [equals_variable_initializer]
	deconstruct Init
		'= VarInit [variable_initializer]
	deconstruct VarInit
		Expression [expression]
    by
		Modifier Type Name ';
		Name Init ';
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
	    PC [normalize]
	EndXML 
end rule
