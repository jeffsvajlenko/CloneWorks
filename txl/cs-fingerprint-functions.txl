%
include "csharp.grm"
include "bom.grm"

% Temporary handling of designated Linq extensions
redefine invocation_operator
	...
    |	'(( [repeat argument_list_or_key] '))
end redefine

define argument_list_or_key
	[argument_list]
    |	'in
end define

define method_definition
    [method_header]				
    '{  [NL][IN] 
	[opt statement_list]  [EX]
    '}  
end define

define potential_clone
    [method_definition]
end define


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

redefine method_header
	...
|	'method_header
end redefine

rule main
    % Abstract them in each potential clone
    skipping [source_unit]
    replace $ [source_unit]
		BeginXML [xml_source_coordinate]
	 	   PC [potential_clone]
		EndXML [end_xml_source_coordinate]
    by
		BeginXML 
	 	   PC [nif][nelseif][nswitch][nswitchlabel][nwhile][ndo][nfor][nlock][nusing][nheader][ngoto][nret][ngoto][nyield][nexpstmt][nstmtexp][ndeclaration]
		EndXML 
end rule

%[normalizeswitch][normalizewhile][normalizedo][normalizefor][normalizeforin][normalizecatch][normalize_header]

%redefine declaration
%...
%|	[expression_statement]
%end redefine

rule ndeclaration
	replace $ [declaration]
		Dec [declaration]
	by
		'X 'X '= 'X ';
end rule

rule nexpstmt
	replace $ [expression_statement]
		Exp [expression_statement]
	by
		'X '= 'X ';
end rule

rule nstmtexp
	replace $ [statement_expression]
		Exp [statement_expression]
	by
		'X '= 'X
end rule


rule nyield
	replace $ [yield_statement]
		'yield 'return Exp [expression] ';
	by
		'yield 'return 'X ';
end rule

rule ngoto
	replace $ [goto_statement]
		Goto [goto_statement]
	by
		'goto 'X ';
end rule

rule nret
	replace $ [return_statement]
		ReturnStatement [return_statement]
	by
		'return 'X ';
end rule

rule nthrow
	replace $ [throw_statement]
		Throws [throw_statement]
	by
		'throw 'X ';
end rule

rule nheader
	replace $ [method_header]
		header [method_header]
	by
		'method_header
end rule

rule nif
    replace $ [if_statement]
        'if '( Condition [condition] ')
			Nested [nested_statement]
		IfElse [repeat else_if_clause]
		ElseClause [opt else_clause]
    by
        'if '( 'X ')
            Nested 
        IfElse
		ElseClause
end rule

rule nelseif
	replace $ [else_if_clause]
		'else 'if '( Condition [condition] ')
			Nested [nested_statement]
	by
		'else 'if '( 'X ')
			Nested
end rule

rule nswitch
	replace $ [switch_statement]
		'switch '( Expression [expression] ')
			Block [switch_block]
	by
		'switch '( 'X ')
			Block
end rule

rule nswitchlabel
	replace $ [switch_label]
		'case Expression [constant_expression ] ':
	by
		'case 'X ':
end rule

rule nwhile
	replace $ [while_statement]
		'while '( Condition [condition] ')
			Nested [nested_statement]
	by
		'while '( 'X ')
			Nested
end rule

rule ndo
	replace $ [do_statement]
		'do
			Statement [statement]
		'while '( Condition [condition] ') ';
	by
		'do
			Statement
		'while '( 'X ') ';
end rule

rule nfor
	replace $ [for_statement]
		'for '( Init [opt for_initializer] '; Cond [opt for_condition] '; Iter [opt for_iterator] ')
			Nested [nested_statement]
	by
		'for '( '; '; ')
			Nested
end rule


rule nlock
	replace $ [lock_statement]
		'lock '( Expression [expression] ')
			Statement [statement]
	by
		'lock '( 'X ')
			Statement
end rule

rule nusing
	replace $ [using_statement]
		'using '( Resource [resource_acquisition] ')
			Statement [statement]
	by
		'using '( 'X ')
			Statement
end rule






