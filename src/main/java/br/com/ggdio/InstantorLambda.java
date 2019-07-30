/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package br.com.ggdio;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.instantor.api.InstantorParams;

/**
 * 
 * AWS Lambda Function for Instantor Callback
 * 
 * @author Guilherme Dio
 *
 * @since 1.0
 */
public class InstantorLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
		
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    
	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		LambdaLogger logger = context.getLogger();
		
		Map<String, String> parameters;
		String body;
		
		try {
			// Decode 'x-www-form-urlencoded' payload
			body = URLDecoder.decode(input.getBody(), StandardCharsets.UTF_8.name());
			
		} catch (UnsupportedEncodingException e) {
			String msg = "ERROR: [phase=HTTP_BODY_DECODING,cause=" + e.getCause() + ", message=" + e.getMessage() + ", stackTrace=" + getStackTrace(e);
			logger.log(msg);
			
			// BAD_REQUEST
			return buildResponse(400, msg);
			
		}
		
		logger.log("BODY_ENCODED:" + input.getBody());
		logger.log("BODY_DECODED:" + body);
		
		if(body != null && !body.isEmpty()) {
			parameters = new HashMap<String, String>();
			
			String[] params = body.split("&");
			for (String paramLine : params) {
				String[] kv = paramLine.split("=");
				parameters.put(kv[0], kv[1]);
				
			}
			
		} else {
			// BAD_REQUEST
			return buildResponse(400, "ERROR: Request Body is Empty !");
			
		}
		
		// Retrieve parameters
		String source = parameters.get(Environment.PARAMETER_SOURCE);
        String msgId = parameters.get(Environment.PARAMETER_MESSAGE_ID);
        String action = parameters.get(Environment.PARAMETER_ACTION);
        String encryption = parameters.get(Environment.PARAMETER_ENCRYPTION);
        String payload = parameters.get(Environment.PARAMETER_PAYLOAD);
        String timestamp = parameters.get(Environment.PARAMETER_TIMESTAMP);
        String hash = parameters.get(Environment.PARAMETER_HASH);
		
		try {
			logger.log("{\"dataType\": \"callbackParameters\", \"data\": "+MAPPER.writeValueAsString(parameters)+"}");
			
		} catch (Exception e1) {
			logger.log("QueryStringParameters: " + parameters);
	        logger.log("source:" + source);
	        logger.log("msgId:" + msgId);
	        logger.log("action:" + action);
	        logger.log("encryption:" + encryption);
	        logger.log("payload:" + payload);
	        logger.log("timestamp:" + timestamp);
	        logger.log("hash:" + hash);
	        
		}
		
		try {
			String data = InstantorParams.loadResponse(source,
					Environment.APPKEY,
	                msgId,
	                action,
	                encryption,
	                payload,
	                timestamp,
	                hash);
			
			logger.log("{\"dataType\": \"callbackPayload\", \"data\": " + data + "}");
			
		} catch(Exception e) {
			String msg = "ERROR: [phase=PAYLOAD_PARSING_BY_INSTANTOR_API,cause=" + e.getCause() + ", message=" + e.getMessage() + ", stackTrace=" + getStackTrace(e);
			logger.log(msg);
			
			// INTERNAL_SERVER_ERROR
			return buildResponse(500, msg);
			
		}
		
		// OK
		return buildResponse(200, "OK: " + msgId);
	}

	/**
	 * Builds the aws lambda response with a status code and a msg
	 * @param status - The HTTP Status COde
	 * @param msg    - The Payload
	 * @return {@link APIGatewayProxyResponseEvent}
	 */
	@SuppressWarnings("serial")
	private APIGatewayProxyResponseEvent buildResponse(int status, String msg) {
		return new APIGatewayProxyResponseEvent()
				.withStatusCode(status)
				.withBody(msg)
				.withIsBase64Encoded(false)
				.withHeaders(new HashMap<String, String>(){{
					put("Content-Type", "text/html;charset=UTF-8");
				}});
	}
	
	/**
	 * Parse {@link Exception} into a stack trace {@link String}
	 * @param e - The Exception
	 * @return {@link String} containing the stack trace
	 */
	private String getStackTrace(Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			return pw.toString();
		} finally {
			try {
				pw.close();
			} catch (Exception ex) {
			}
			try {
				sw.close();
			} catch (IOException ex) {
			}
		}
	}
	
}