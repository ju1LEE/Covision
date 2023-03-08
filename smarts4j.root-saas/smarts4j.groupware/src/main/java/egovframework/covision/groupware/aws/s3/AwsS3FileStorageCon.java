package egovframework.covision.groupware.aws.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;

import egovframework.baseframework.util.StringUtil;
import egovframework.coviframework.util.s3.AwsS3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author nkpark
 */
@RestController
//@Api("AWS S3 API V1")
@RequestMapping("/aws")
public class AwsS3FileStorageCon {

    AwsS3 awsS3 = AwsS3.getInstance();

    private Logger LOGGER = LogManager.getLogger(AwsS3FileStorageCon.class);

    @RequestMapping(value = "/s3redirect.do", method = {RequestMethod.GET,RequestMethod.POST})
    public void down(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String key = StringUtil.replaceNull(request.getParameter("key"), "");
        String convKey = new String(key.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        if(!convKey.contains("?")){
            key = convKey;
        }
        String[] arrKey;
        if(key.indexOf("/")>-1){
            arrKey = key.split("/");
        }else{
            arrKey = new String[] {key};
        }
        String fileName = arrKey[arrKey.length-1].replaceAll("\r", "").replaceAll("\n", ""); //CLRF 대응;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        response.setHeader("Content-Description", "JSP Generated Data");
        response.setHeader("Content-Disposition", "attachment;fileName=\""+fileName+"\";");
        response.setContentType("application/octet-stream;charset=utf-8");
        try {
            LOGGER.info("[AWS S3]#####s3redirect-try-key:"+key);
            if(awsS3.exist(key)) {
                byte[] bytes = awsS3.down(key);
                LOGGER.debug("[AWS S3]#####s3redirect-length:"+bytes.length);
                if (bytes.length > 0) {
                    baos.write(bytes);
                    response.getOutputStream().write(baos.toByteArray());
                    response.getOutputStream().flush();
                } else {
                    response.reset();
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
        	LOGGER.error(e.getLocalizedMessage(), e);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
        	LOGGER.error(e.getLocalizedMessage(), e);
        } catch (IOException e){
        	LOGGER.error(e.getLocalizedMessage(), e);
        } finally {
        	if (baos != null) { try{ baos.flush(); baos.close(); } catch (IOException ioe) { LOGGER.error(ioe.getLocalizedMessage(), ioe); } }
        }
    } 
}
