package org.gortz.greeniot.smartcityiot2.dto.connection;

import org.springframework.http.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * For containing API request information
 */

@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class RestApiCall {
    /**
     * Topic
     *
     * @return topic
     */
    String topic;

    /**
     * Method
     *
     * @return API request method
     */
    HttpMethod method;
}
