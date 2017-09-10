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
|	[variable_name] [equals_variable_initializer] '; [NL]
end define

rule filter_literalinit
	import Nothing [any]
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
		Literal [literal]
    by
		Nothing
end rule

rule filter_identifierinit
	import Nothing [any]
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
		Identifier [id]
    by
		Nothing
end rule


rule filter_complex
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
		Name Init ';
end rule

rule main
    % Make a global nothing
    construct Empty [empty]
    deconstruct * [any] Empty
	Nothing [any]
    export Nothing

    % Abstract them in each potential clone
    skipping [source_unit]
    replace $ [source_unit]
	BeginXML [xml_source_coordinate]
	    PC [potential_clone]
	EndXML [end_xml_source_coordinate]
    by
	BeginXML 
	    PC [filter_literalinit][filter_identifierinit][filter_complex]
	EndXML 
end rule
