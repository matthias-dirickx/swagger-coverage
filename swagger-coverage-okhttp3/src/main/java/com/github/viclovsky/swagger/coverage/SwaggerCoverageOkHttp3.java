package com.github.viclovsky.swagger.coverage;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.BODY_PARAM_NAME;
import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;
import static io.swagger.models.Scheme.forValue;
import static java.lang.String.valueOf;

public class SwaggerCoverageOkHttp3 implements Interceptor {

    private CoverageOutputWriter writer;
    public SwaggerCoverageOkHttp3(CoverageOutputWriter writer) {
        this.writer = writer;
    }
    public SwaggerCoverageOkHttp3() {
        this.writer = new FileSystemOutputWriter(Paths.get(OUTPUT_DIRECTORY));
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Operation operation = new Operation();
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        // --> check the implementation - must extract the parameters from the path.
        httpUrl.queryParameterNames()
            .forEach(
                (n) -> operation.addParameter(
                    new PathParameter()
                        .name(n)
                        .example(
                            String.join(
                                ",",
                                httpUrl.queryParameterValues(n))
                        )));
        return null;
    }
}
