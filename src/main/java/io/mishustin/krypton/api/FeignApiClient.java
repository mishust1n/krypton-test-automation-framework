package io.mishustin.krypton.api;

import feign.Feign;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;


import java.io.IOException;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;

public class FeignApiClient {

    public static JsonPlaceholderService getJsonPlaceholderService(String baseUrl) {

        JsonPlaceholderService service = Feign.builder()
                .decoder(new ResponseDecoder())
                .target(JsonPlaceholderService.class, baseUrl);

        return (JsonPlaceholderService) wrap(service, JsonPlaceholderService.class);
    }

    private static Object wrap(Object obj, Class<?> clazz) {
        return Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                new Class[]{clazz},
                new ErrorResponseProxy(obj));
    }

    private static class ResponseDecoder implements Decoder {

        @Override
        public Object decode(Response response, Type type) throws IOException, FeignException {
            Type responseType = ((ParameterizedType) type).getActualTypeArguments()[0];
            String body = null;
            if (response.body() != null) {


                char[] buffer = new char[4096];
                StringBuilder builder = new StringBuilder();
                int numChars;

                while ((numChars = response.body().asReader(StandardCharsets.UTF_8).read(buffer)) >= 0) {
                    builder.append(buffer, 0, numChars);
                }

                body = builder.toString();
            }

            return new ApiResponse<>(responseType, response.status(), body);
        }
    }

    private static class ErrorResponseProxy implements InvocationHandler {

        private final Object target;

        public ErrorResponseProxy(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            try {
                return method.invoke(target, objects);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException instanceof FeignException) {
                    int statusCode = ((FeignException) targetException).status();
                    String body = ((FeignException) targetException).responseBody().toString();
                    return new ApiResponse<>(Object.class, statusCode, body);
                } else {
                    throw e.getCause();
                }
            } catch (Exception ex) {
                throw ex.getCause();
            }
        }
    }
}