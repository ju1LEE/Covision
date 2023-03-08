package com.activiti.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.image.exception.ActivitiImageException;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;

import egovframework.covision.coviflow.util.CommonPropertiesUtil;

public class ImageGenerator {

	public static BufferedImage createImage(BpmnModel bpmnModel) {
        return ImageGenerator.createImage(bpmnModel, 1.0);
	}
	
	public static BufferedImage createImage(BpmnModel bpmnModel, double scaleFactor) {
		String activityFontName = CommonPropertiesUtil.getProperties("activiti-app").getProperty("model.diagram.fontname", "Arial");
		String labelFontName = activityFontName;
		String annotationFontName = activityFontName;
		
		DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator(scaleFactor);
        BufferedImage diagramImage = diagramGenerator.generateImage(bpmnModel, "png", Collections.<String>emptyList(), Collections.<String>emptyList(), activityFontName, labelFontName, annotationFontName, null, scaleFactor);

        return diagramImage;
	}
	
	public static byte[] createByteArrayForImage(BufferedImage image, String imageType) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
	    	ImageIO.write(image, imageType, out);
	      
	    } catch (IOException e) {
	      throw new ActivitiImageException("Error while generating byte array for process image", e);
	    } finally {
	    	try {
	    		if (out != null) {
	    			out.close();
	    		}
	    	} catch(IOException ignore) {
	    		// Exception is silently ignored
	    	}
	    }
	    return out.toByteArray();
	}
}
