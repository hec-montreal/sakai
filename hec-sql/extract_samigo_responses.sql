-- Couldn't find a better way to strip html than multiple regexp_replace
SELECT spasst.ID, spasst.TITLE, suim.eid, spst.title, spst.sequence, 
	spit.INSTRUCTION, 
	REGEXP_REPLACE(REGEXP_REPLACE(regexp_replace(spit.INSTRUCTION, '(<style(.*)<\/style>)', NULL, 1, 0, 'n'), '(<.*?>)', NULL), '^\s*', NULL, 1, 0, 'm') AS INSTRUCTION_SANS_HTML, 
	spitt.TEXT, 
	REGEXP_REPLACE(REGEXP_REPLACE(regexp_replace(spitt.TEXT, '(<style(.*)<\/style>)', NULL, 1, 0, 'n'), '(<.*?>)', NULL), '^\s*', NULL, 1, 0, 'm') AS TEXT_SANS_HTML,
	sigt.answertext, 
	REGEXP_REPLACE(REGEXP_REPLACE(regexp_replace(sigt.answertext, '(<style(.*)<\/style>)', NULL, 1, 0, 'n'), '(<.*?>)', NULL), '^\s*', NULL, 1, 0, 'm') AS ANSWERTEXT_SANS_HTML,
	spat.label, 
	spat.text, 
	REGEXP_REPLACE(REGEXP_REPLACE(regexp_replace(spat.text, '(<style(.*)<\/style>)', NULL, 1, 0, 'n'), '(<.*?>)', NULL), '^\s*', NULL, 1, 0, 'm') AS TEXT2_SANS_HTML,
	spat.iscorrect, sigt.AUTOSCORE AS points, spat.score AS possible_points
FROM SAM_ASSESSMENTGRADING_T sagt, SAM_PUBLISHEDASSESSMENT_T spasst, 
	SAM_ITEMGRADING_T sigt, SAM_PUBLISHEDITEM_T spit, SAM_PUBLISHEDITEMTEXT_T spitt, 
	SAM_PUBLISHEDANSWER_T spat, SAKAI_USER_ID_MAP suim,
	SAM_PUBLISHEDSECTION_T spst, SAM_AUTHZDATA_T sat
WHERE sagt.ASSESSMENTGRADINGID = sigt.ASSESSMENTGRADINGID
AND sigt.PUBLISHEDITEMID = spit.ITEMID
AND sigt.PUBLISHEDITEMTEXTID = spitt.ITEMTEXTID
AND sigt.PUBLISHEDANSWERID = spat.ANSWERID
AND spit.SECTIONID = spst.SECTIONID
AND sagt.AGENTID = suim.USER_ID
AND spasst.id = sagt.PUBLISHEDASSESSMENTID
AND sagt.PUBLISHEDASSESSMENTID = sat.QUALIFIERID AND sat.FUNCTIONID = 'OWN_PUBLISHED_ASSESSMENT' AND sat.AGENTID = 'MATH20605.H2022'
ORDER BY spasst.id, suim.eid, spst."SEQUENCE", spit."SEQUENCE", spitt."SEQUENCE"
