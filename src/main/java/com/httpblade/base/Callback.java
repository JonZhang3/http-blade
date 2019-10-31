package com.httpblade.base;

import java.io.IOException;

/**
 *
 * @author Jon
 * @since 1.0.0
 */
public interface Callback {

    void error(Exception e);

    void success(Response response) throws IOException;

}
