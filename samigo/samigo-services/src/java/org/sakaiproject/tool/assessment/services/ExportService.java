package org.sakaiproject.tool.assessment.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

//import org.sakaiproject.tool.assessment.contentpackaging.ManifestGenerator;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExportService {

    public void extractAssessment(String assessmentId) {
        log.error("exort " + assessmentId);
        return;
    }
    /*
		//update random question pools (if any) before exporting
		AssessmentService assessmentService = new AssessmentService();
        AssessmentFacade a = assessmentService.getAssessment(assessmentId);
		int success = assessmentService.updateAllRandomPoolQuestions(a);
		if(success == AssessmentService.UPDATE_SUCCESS){
            //res.setContentType("application/x-zip-compressed");

            //todo clean title
            String zipFilename = a.getTitle() + ".zip";

            OutputStream outputStream = null;
            //BufferedInputStream bufInputStream = null;
            ZipOutputStream zos = null;
            ZipEntry ze = null;

            try {
                byte[] b = null;
                
                //TODO set outputstream
                //outputStream = res.getOutputStream();
                zos = new ZipOutputStream(outputStream);

                // QTI file
                ze = new ZipEntry("exportAssessment.xml");
                zos.putNextEntry(ze);

                /*
                XMLController xmlController = (XMLController) ContextUtil
                .lookupBeanFromExternalServlet("xmlController", req,
                        res);

                xmlController.setId(assessmentId);
                xmlController.setQtiVersion(1);
                xmlController.displayAssessmentXml();
                String qtiString = xmlController.getXmlBean().getXml();
                        */ /*
                String qtiString = null;
                
                log.debug("qtiString = " + qtiString);
                b = qtiString.getBytes();
                zos.write(b, 0, b.length);
                zos.closeEntry();

                // imsmanifest.xml
                ze = new ZipEntry("imsmanifest.xml");
                zos.putNextEntry(ze);
                //ManifestGenerator manifestGenerator = new ManifestGenerator(
                //        assessmentId);
                String manString = null; //manifestGenerator.getManifest();
                log.debug("manString = " + manString);
                b = manString.getBytes();
                zos.write(b, 0, b.length);
                zos.closeEntry();

                // Attachments
                HashMap contentMap = null; //manifestGenerator.getContentMap();

                String filename = null;
                for (Iterator it = contentMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    filename = (String)  entry.getKey();
                    ze = new ZipEntry(filename.substring(1));
                    zos.putNextEntry(ze);
                    b = (byte[]) entry.getValue();
                    zos.write(b, 0, b.length);
                    zos.closeEntry();
                }

            } catch (IOException e) {
                log.error(e.getMessage());
                throw e;
            } finally {
                if (zos != null) {
                    try {
                        zos.closeEntry();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                    try {
                        zos.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }

                }
            }

		}else{
			if(success == AssessmentService.UPDATE_ERROR_DRAW_SIZE_TOO_LARGE) {
                log.error("update_pool_error_size_too_large");
				//String err=ContextUtil.getLocalizedString("org.sakaiproject.tool.assessment.bundle.AuthorMessages","update_pool_error_size_too_large");
				//req.setAttribute("error", err);
			}else{
                log.error("update_pool_error_unknown");
				//String err=ContextUtil.getLocalizedString("org.sakaiproject.tool.assessment.bundle.AuthorMessages","update_pool_error_unknown");
				//req.setAttribute("error", err);
			}

            return;
		}
    }
*/
}
