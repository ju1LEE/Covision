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
package com.activiti.conf;

import org.springframework.context.annotation.*;

@Configuration
@PropertySources({
	@PropertySource(value = "file://${DEPLOY_PATH}/covi_property/activiti-admin.properties"),
	@PropertySource(value = "classpath:/property/activiti-admin.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "classpath:activiti-admin.properties", ignoreResourceNotFound = true),
	@PropertySource(value = "file:activiti-admin.properties", ignoreResourceNotFound = true)
})

@ComponentScan(basePackages = {
        "com.activiti.service",
        "com.activiti.security"})

@Import(value = {
        SecurityConfiguration.class,
        AsyncConfiguration.class,
        DatabaseConfiguration.class,
        JacksonConfiguration.class})

@ImportResource({"classpath:/property/activiti-custom-context.xml"})
public class ApplicationConfiguration {

}
