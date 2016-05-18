%jeff Svajlenko, June 2013
% Based upon java functions extraction by Jim Cordy, January 2008

include "c.grm"

include "bom.grm"

% start XML
define xml_source_coordinate
	'< [SPOFF] 'source [SP] 'file=[stringlit] [SP] 'startline=[stringlit] [SP] 'endline=[stringlit] '> [SPON] [NL]
end define

% end XML
define end_xml_source_coordinate
	[NL] '< [SPOFF] '/ 'source '> [SPON] [NL]
end define

% program definition that allows coordinates
redefine program
		% Input Form
		[srcfilename]
		[compilation_unit]
	|
		% Output Form
		[not token]
		[opt xml_source_coordinate]
		[compilation_unit]
		[opt end_xml_source_coordinate]
end define

function main
	replace [program]
		FileName [srcfilename]
		File [compilation_unit]
	construct Zero [number]
		0
	construct ZeroString [stringlit]
		_ [quote Zero]
	construct FileNameString [stringlit]
		_ [quote FileName]
	construct XmlHeader [xml_source_coordinate]
		<source file=FileNameString startline=ZeroString endline=ZeroString>
	by
		XmlHeader
		File [removeOptSemis] [removeEmptyStatements]
		</source>
end function

rule removeOptSemis
	replace [opt ';]
		';
	by
		% none
end rule

rule removeEmptyStatements
	replace [repeat declaration_or_statement]
		';
		More [repeat declaration_or_statement]
	by
		More
end rule
