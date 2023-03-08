/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.activiti.service.activiti;

import com.activiti.domain.ServerConfig;
import com.activiti.repository.ServerConfigRepository;
import com.activiti.web.rest.dto.ServerConfigRepresentation;

import egovframework.covision.coviflow.util.CommentedProperties;
import egovframework.covision.coviflow.util.CommonPropertiesUtil;
import egovframework.covision.coviflow.util.CoviFlowPropHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author jbarrez
 * @author yvoswillens
 */
@Service
public class ServerConfigService extends AbstractEncryptingService {

    private static final String REST_APP_NAME = "rest.app.name";
    private static final String REST_APP_DESCRIPTION = "rest.app.description";
    private static final String REST_APP_HOST = "rest.app.host";
    private static final String REST_APP_PORT = "rest.app.port";
    private static final String REST_APP_CONTEXT_ROOT = "rest.app.contextroot";
    private static final String REST_APP_REST_ROOT = "rest.app.restroot";
    private static final String REST_APP_USER = "rest.app.user";
    private static final String REST_APP_PASSWORD = "rest.app.password";

    @Autowired
    protected Environment environment;

	@Autowired
	protected ServerConfigRepository serverConfigRepository;
	
	String FILE_NAME = "activiti-admin";
	
	//@Transactional
	public void createDefaultServerConfig() {
/*
        ServerConfig serverConfig = getDefaultServerConfig();

        serverConfig.setUserName(environment.getRequiredProperty(REST_APP_USER));
        serverConfig.setPassword(encrypt(CoviFlowPropHelper.getInstace().getDecryptedProperty(environment.getRequiredProperty(REST_APP_PASSWORD))));

		// save(serverConfig, true);
*/
	}

	
	/**
	 * @deprecated use properties instead database.
	 * @param id
	 * @return
	 */
    @Transactional
    @Deprecated
    public ServerConfig findOne(Long id) {
        return serverConfigRepository.findOne(id);
    }

    //@Transactional
    public List<ServerConfigRepresentation> findAll() {
        //return createServerConfigRepresentation(serverConfigRepository.findAll());
    	
    	List<ServerConfig> list = new ArrayList<ServerConfig>();
    	list.add(getServerConfig());
    	
    	return createServerConfigRepresentation(list);
    }

    @Transactional
    public void save(ServerConfig serverConfig, boolean encryptPassword) {
    	//Mod. save as properties.
        if (encryptPassword) {
            //serverConfig.setPassword(encrypt(serverConfig.getPassword()));
        	//String restEncrypted = encrypt(serverConfig.getPassword());
        	String PBEEncrypted = CoviFlowPropHelper.getInstace().getEncryptedValue(serverConfig.getPassword());
        	getProp().setProperty(REST_APP_PASSWORD, PBEEncrypted);
        }else {
        	//REST Encrypt(or Plain) to PBE Encrypt
        	String plain = decrypt(serverConfig.getPassword());
        	getProp().setProperty(REST_APP_PASSWORD, CoviFlowPropHelper.getInstace().getEncryptedValue(plain));	
        }
        //serverConfigRepository.save(serverConfig);
        
        getProp().setProperty(REST_APP_USER, serverConfig.getUserName());
        getProp().setProperty(REST_APP_NAME, serverConfig.getName());
        getProp().setProperty(REST_APP_DESCRIPTION, serverConfig.getDescription());
        getProp().setProperty(REST_APP_HOST, serverConfig.getServerAddress());
        getProp().setProperty(REST_APP_PORT, String.valueOf(serverConfig.getPort()));
        getProp().setProperty(REST_APP_CONTEXT_ROOT, serverConfig.getContextRoot());
        getProp().setProperty(REST_APP_REST_ROOT, serverConfig.getRestRoot());
        // save(change properties file with comment.)
        CommonPropertiesUtil.store(getProp(), FILE_NAME, "");
    }
    
    public String getServerConfigDecryptedPassword(ServerConfig serverConfig) {
        return decrypt(serverConfig.getPassword());
    }

    protected List<ServerConfigRepresentation> createServerConfigRepresentation(List<ServerConfig> serverConfigs) {
        List<ServerConfigRepresentation> serversRepresentations = new ArrayList<ServerConfigRepresentation>();
        for (ServerConfig serverConfig: serverConfigs) {
            serversRepresentations.add(createServerConfigRepresentation(serverConfig));
        }
        return serversRepresentations;
    }
    
    protected ServerConfigRepresentation createServerConfigRepresentation(ServerConfig serverConfig) {
        ServerConfigRepresentation serverRepresentation = new ServerConfigRepresentation();
        serverRepresentation.setId(serverConfig.getId());
        serverRepresentation.setName(serverConfig.getName());
        serverRepresentation.setDescription(serverConfig.getDescription());
        serverRepresentation.setServerAddress(serverConfig.getServerAddress());
        serverRepresentation.setServerPort(serverConfig.getPort());
        serverRepresentation.setContextRoot(serverConfig.getContextRoot());
        serverRepresentation.setRestRoot(serverConfig.getRestRoot());
        serverRepresentation.setUserName(serverConfig.getUserName());
        return serverRepresentation;
    }

    public ServerConfig getDefaultServerConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setName(getProp().getProperty(REST_APP_NAME, ""));
        serverConfig.setDescription(getProp().getProperty(REST_APP_DESCRIPTION, ""));
        serverConfig.setServerAddress(getProp().getProperty(REST_APP_HOST, ""));
        serverConfig.setPort(Integer.parseInt(getProp().getProperty(REST_APP_PORT, "")));
        serverConfig.setContextRoot(getProp().getProperty(REST_APP_CONTEXT_ROOT, ""));
        serverConfig.setRestRoot(getProp().getProperty(REST_APP_REST_ROOT, ""));

        return serverConfig;

    }
    
    public ServerConfig getServerConfig() {
    	//return configuration from properties.
    	ServerConfig serverConfig = getDefaultServerConfig();
        serverConfig.setUserName(getProp().getProperty(REST_APP_USER, ""));
        serverConfig.setId(1L);
        
        // PBE Encrypt to REST encrypt
        serverConfig.setPassword(encrypt(CoviFlowPropHelper.getInstace().getDecryptedProperty(getProp().getProperty(REST_APP_PASSWORD, ""))));
        return serverConfig; 
    }
    
    // runtime.
    private Properties getProp() {
    	return CommonPropertiesUtil.getProperties(FILE_NAME); 
    }
}
