% Example custom context-dependent normalization: 
% abstract only if-condition expressions in Java 
% Jim Cordy, May 2010

% Using C# grammar
include "csharp.grm"
include "bom.grm"

% Redefinition for potential clones

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


% Main program

rule main
    % Abstract them in each potential clone
    skipping [source_unit]
    replace $ [source_unit]
        BeginXML [xml_source_coordinate]
            PC [potential_clone]
        EndXML [end_xml_source_coordinate]
    by
        BeginXML
            PC
        EndXML
end rule
