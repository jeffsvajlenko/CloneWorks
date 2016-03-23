% Example using TXL 10.5a source coordinate extensions to extract
% a table of all method definitions with source coordinates

% Jim Cordy, January 2008

% Revised Nov 2012 - remove @Override annotations from clone comparison - JRC
% Revised Aug 2012 - disallow ouput forms in input parse - JRC
% Revised July 2011 - ignore BOM headers in source
% Revised 25.03.11 - match constructors as methods - JRC
% Revised 30.04.08 - unmark embedded functions - JRC

% Using Java 5 grammar
include "Cpp.grm"

% Ignore BOM headers from Windows
include "bom.grm"

% Redefinitions to collect source coordinates for function definitions as parsed input,
% and to allow for XML markup of function definitions as output

% Modified to match constructors as well.  Even though the grammar still
% has constructor_declaration in it, this one will match first. - JRC 25mar11


%define function_definition
%    [NL] [opt decl_specifiers] [function_declarator] [opt ctor_initializer] 
%        [opt exception_specification] [function_body]
%end define

%define function_declarator
 %   % like [declarator], but requires a [declarator_extension]
%        [repeat pointer_operator] [declared_item] [repeat declarator_extension+]
%end define

%define function_body
%        [NL] [compound_statement] [opt ';] [NL] 
%    |   'try [opt ctor_initializer] [NL] [compound_statement] [opt ';] [NL] [handler_list]
%end define

%define compound_statement
%    '{                          [NL][IN]
%        [opt statement_list]    [EX]
%    '}                          [NL]
%end define




redefine function_definition
	% Input form 
	[srcfilename] [srclinenumber] 		% Keep track of starting file and line number
	[NL] [opt decl_specifiers] [function_declarator] [opt ctor_initializer] 
	[opt exception_specification]
	'{ 				[IN][NL]
	    [opt statement_list]	[EX]
	    [srcfilename] [srclinenumber] 	% Keep track of ending file and line number
	'}
    |
	% Output form 
        [not token]                             % disallow output form in input parse
	[opt xml_source_coordinate]
	[NL] [opt decl_specifiers] [function_declarator] [opt ctor_initializer] 
	[opt exception_specification]
	'{ 				[IN][NL]
	    [opt statement_list]	[EX]
	'}
	[opt end_xml_source_coordinate]
    |
	[srcfilename] [srclinenumber]
	[NL] [opt decl_specifiers] [function_declarator] [opt ctor_initializer] 
        [opt exception_specification] [function_body]
	[srcfilename] [srclinenumber]
    |
	[not token]
	[opt xml_source_coordinate]
	[NL] [opt decl_specifiers] [function_declarator] [opt ctor_initializer] 
        [opt exception_specification] [function_body]
	[opt end_xml_source_coordinate]
	
end redefine

define xml_source_coordinate
    '< [SPOFF] 'source [SP] 'file=[stringlit] [SP] 'startline=[stringlit] [SP] 'endline=[stringlit] '> [SPON] [NL]
end define

define end_xml_source_coordinate
    [NL] '< [SPOFF] '/ 'source '> [SPON] [NL]
end define

redefine program
	...
    | 	[repeat function_definition]
end redefine


% Main function - extract and mark up function definitions from parsed input program
function main
    replace [program]
	P [program]
    construct Functions [repeat function_definition]
    	_ [^ P] 			% Extract all functions from program
	  [convertFunctionDefinitions] [convertFunctionDefinitions2]	% Mark up with XML
    by 
    	Functions
end function

rule convertFunctionDefinitions
    % Find each function definition and match its input source coordinates
    replace [function_definition]
	FileName [srcfilename] LineNumber [srclinenumber]
	Decl [opt decl_specifiers] FunctionHeader [function_declarator] CtorInit[opt ctor_initializer] 
	Except [opt exception_specification]
	'{
	    FunctionBody [opt statement_list]
	    EndFileName [srcfilename] EndLineNumber [srclinenumber]
	'}

    % Convert file name and line numbers to strings for XML
    construct FileNameString [stringlit]
	_ [quote FileName] 
    construct LineNumberString [stringlit]
	_ [quote LineNumber]
    construct EndLineNumberString [stringlit]
	_ [quote EndLineNumber]

    % Output is XML form with attributes indicating input source coordinates
    construct XmlHeader [xml_source_coordinate]
	<source file=FileNameString startline=LineNumberString endline=EndLineNumberString>
    by
	XmlHeader
	Decl	
	FunctionHeader 
	CtorInit
	Except
	'{
	    FunctionBody [unmarkEmbeddedFunctionDefinitions]
	'}
	</source>
end rule

rule convertFunctionDefinitions2
    % Find each function definition and match its input source coordinates
    replace [function_definition]
	FileName [srcfilename] LineNumber [srclinenumber]
	Decl [opt decl_specifiers] FunctionHeader [function_declarator] CtorInit [opt ctor_initializer] 
        Except [opt exception_specification] FunctionBody [function_body]
	EndFileName [srcfilename] EndLineNumber [srclinenumber]

    % Convert file name and line numbers to strings for XML
    construct FileNameString [stringlit]
	_ [quote FileName] 
    construct LineNumberString [stringlit]
	_ [quote LineNumber]
    construct EndLineNumberString [stringlit]
	_ [quote EndLineNumber]

    % Output is XML form with attributes indicating input source coordinates
    construct XmlHeader [xml_source_coordinate]
	<source file=FileNameString startline=LineNumberString endline=EndLineNumberString>
    by
	XmlHeader
	Decl	
	FunctionHeader 
	CtorInit
	Except
	FunctionBody [unmarkEmbeddedFunctionDefinitions]
	</source>
end rule

rule unmarkEmbeddedFunctionDefinitions
    replace [function_definition]
	FileName [srcfilename] LineNumber [srclinenumber]
	Decl [opt decl_specifiers] FunctionHeader [function_declarator] CtorInit[opt ctor_initializer] 
	Except [opt exception_specification]
	'{
	    FunctionBody [opt statement_list]
	    EndFileName [srcfilename] EndLineNumber [srclinenumber]
	'}
    by
	Decl		
	FunctionHeader 
	CtorInit
	Except
	'{
	    FunctionBody
	'}
end rule
