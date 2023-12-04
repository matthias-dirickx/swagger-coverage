package com.github.viclovsky.swagger.coverage;

import okhttp3.Headers;
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
        // Currently seems not feasible in a generic way.

        httpUrl.queryParameterNames()
            .forEach(
                (n) -> operation.addParameter(
                    new PathParameter()
                        .name(n)
                        .example(
                            String.join(
                                ",",
                                httpUrl.queryParameterValues(n)))));

        // Go through values of headers and add all of those as examples
        request.headers().names()
            .forEach(
                headerName -> request.headers().values(headerName)
                    .forEach( headerValue -> operation.addParameter(
                        new HeaderParameter()
                            .name(headerName)
                            .example(headerValue))));

        if (Objects.nonNull(request.body())) {
            operation.addParameter(new BodyParameter().name(BODY_PARAM_NAME));
        }

        final Response response = chain.proceed(request);

        operation.addResponse(valueOf(response.code()), new io.swagger.models.Response());

        Swagger swagger = new Swagger()
            .scheme(forValue(request.url().scheme()))
            .host(request.url().host())
            .consumes(request.body().contentType().toString())
            .produces(response.body().contentType().toString())
            .path(httpUrl.url().toString(), new io.swagger.models.Path().set(request.method().toLowerCase(), operation));

        writer.write(swagger);
        return response;
    }
}
