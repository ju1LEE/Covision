package org.activiti.rest.conf;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		response.setHeader("Access-Control-Allow-Origin", "*");
		if (request.getHeader("Access-Control-Request-Method") != null
				&& "OPTIONS".equals(request.getMethod())) {
			// CORS "pre-flight" request
			response.setHeader("Access-Control-Allow-Credentials", "true");
			response.setHeader("Access-Control-Allow-Methods",
					"GET, POST, PUT, DELETE");
			response.setHeader("Access-Control-Allow-Headers",
					"X-ACCESS_TOKEN,Origin,X-Requested-With,Origin,Content-Type,Accept,Authorization");
			response.setHeader("Access-Control-Max-Age", "3600");
		}

		filterChain.doFilter(request, response);
	}

}
