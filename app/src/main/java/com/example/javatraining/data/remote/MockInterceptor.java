package com.example.javatraining.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class MockInterceptor implements Interceptor {

    private static final String DUMMY_TOKEN = "DUMMY_TOKEN_123";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();
        String authHeader = request.header("Authorization");

        // 1. Intercept Login if email is dummy
        if (url.contains("/mobile/auth/login") && request.method().equals("POST")) {
            String requestBody = getRequestBody(request);
            if (requestBody != null && requestBody.contains("dummy@test.com")) {
                String loginJson = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": \"Login dummy berhasil\",\n" +
                        "  \"data\": {\n" +
                        "    \"token\": \"" + DUMMY_TOKEN + "\",\n" +
                        "    \"employee\": {\n" +
                        "      \"id\": \"dummy-id\",\n" +
                        "      \"employeeId\": \"EMP-DUMMY\",\n" +
                        "      \"name\": \"Sarah Jenkins (Dummy)\",\n" +
                        "      \"email\": \"dummy@test.com\",\n" +
                        "      \"department\": \"Creative Design\",\n" +
                        "      \"position\": \"Lead Designer\",\n" +
                        "      \"photos\": null\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
                return createResponse(request, 200, loginJson);
            }
        }

        // 2. Intercept other endpoints if they use DUMMY_TOKEN
        if (authHeader != null && authHeader.contains(DUMMY_TOKEN)) {

            if (url.contains("/mobile/profile")) {
                String profileJson = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": \"Profil dummy berhasil diambil\",\n" +
                        "  \"data\": {\n" +
                        "    \"id\": \"dummy-id\",\n" +
                        "    \"employeeId\": \"EMP-DUMMY\",\n" +
                        "    \"name\": \"Sarah Jenkins (Dummy)\",\n" +
                        "    \"email\": \"dummy@test.com\",\n" +
                        "    \"department\": \"Creative Design\",\n" +
                        "    \"position\": \"Lead Designer\",\n" +
                        "    \"faceRegistered\": true,\n" +
                        "    \"photos\": null\n" +
                        "  }\n" +
                        "}";
                return createResponse(request, 200, profileJson);
            }

            if (url.contains("/mobile/schedule/today")) {
                String scheduleJson = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": \"Jadwal dummy hari ini\",\n" +
                        "  \"data\": {\n" +
                        "    \"id\": \"sch-dummy\",\n" +
                        "    \"scheduleCode\": \"REGULAR\",\n" +
                        "    \"name\": \"Regular Shift\",\n" +
                        "    \"workDays\": [\"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\"],\n" +
                        "    \"checkInTime\": \"08:00\",\n" +
                        "    \"checkOutTime\": \"17:00\",\n" +
                        "    \"breakStartTime\": \"12:00\",\n" +
                        "    \"breakEndTime\": \"13:00\",\n" +
                        "    \"toleranceMinutes\": 15\n" +
                        "  }\n" +
                        "}";
                return createResponse(request, 200, scheduleJson);
            }

            if (url.contains("/mobile/attendance/history")) {
                // Return fake check-in from today
                String now = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new java.util.Date());
                
                String historyJson = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": \"Riwayat dummy berhasil diambil\",\n" +
                        "  \"data\": [\n" +
                        "    {\n" +
                        "      \"id\": \"event-1\",\n" +
                        "      \"employeeId\": \"dummy-id\",\n" +
                        "      \"cameraId\": \"mobile-app\",\n" +
                        "      \"eventType\": \"CHECK_IN\",\n" +
                        "      \"timestamp\": \"" + now + "\",\n" +
                        "      \"confirmationStatus\": \"CONFIRMED\",\n" +
                        "      \"isLate\": false,\n" +
                        "      \"photoUrl\": null,\n" +
                        "      \"createdAt\": \"" + now + "\",\n" +
                        "      \"updatedAt\": \"" + now + "\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"pagination\": {\n" +
                        "    \"page\": 1,\n" +
                        "    \"limit\": 10,\n" +
                        "    \"total\": 1,\n" +
                        "    \"totalPages\": 1\n" +
                        "  }\n" +
                        "}";
                return createResponse(request, 200, historyJson);
            }

            if (url.contains("/mobile/attendance") && request.method().equals("POST")) {
                String attendanceResp = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": \"Absensi dummy berhasil dicatat\",\n" +
                        "  \"data\": null\n" +
                        "}";
                return createResponse(request, 200, attendanceResp);
            }

            if (url.contains("/mobile/notifications")) {
                String notifJson = "{\n" +
                        "  \"success\": true,\n" +
                        "  \"message\": \"Notifikasi dummy\",\n" +
                        "  \"data\": [],\n" +
                        "  \"pagination\": {\"page\": 1, \"limit\": 10, \"total\": 0, \"totalPages\": 1}\n" +
                        "}";
                return createResponse(request, 200, notifJson);
            }

            if (url.contains("/mobile/device-token")) {
                return createResponse(request, 200, "{\"success\":true,\"message\":\"Token updated\",\"data\":null}");
            }
        }

        // Proceed normally if not intercepted
        return chain.proceed(request);
    }

    private String getRequestBody(Request request) {
        try {
            if (request.body() == null) return null;
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            return buffer.readString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }

    private Response createResponse(Request request, int code, String json) {
        return new Response.Builder()
                .code(code)
                .message("Mock Response")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create(json, MediaType.parse("application/json")))
                .addHeader("content-type", "application/json")
                .build();
    }
}
