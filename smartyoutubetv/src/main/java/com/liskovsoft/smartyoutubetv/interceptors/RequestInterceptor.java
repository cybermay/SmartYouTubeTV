package com.liskovsoft.smartyoutubetv.interceptors;

import android.text.TextUtils;
import android.webkit.WebResourceResponse;
import com.liskovsoft.smartyoutubetv.misc.MyUrlEncodedQueryString;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class RequestInterceptor {
    private OkHttpClient mClient;

    public abstract boolean test(String url);
    public abstract WebResourceResponse intercept(String url);


    protected Response doOkHttpRequest(String url) {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }

        Request okHttpRequest = new Request.Builder()
                .url(url)
                .build();

        Response okHttpResponse = null;
        try {
            okHttpResponse = mClient.newCall(okHttpRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return okHttpResponse;
    }

    protected String getMimeType(MediaType contentType) {
        String type = contentType.type();
        String subtype = contentType.subtype();
        return String.format("%s/%s", type, subtype);
    }

    protected String getCharset(MediaType contentType) {
        if (contentType.charset() == null) {
            return null;
        }
        return contentType.charset().name();
    }

    protected WebResourceResponse createResponse(MediaType mediaType, InputStream is) {
        WebResourceResponse resourceResponse = new WebResourceResponse(
                getMimeType(mediaType),
                getCharset(mediaType),
                is
        );
        return resourceResponse;
    }

    protected Map<String, String> toRegularMap(Map<String, List<String>> multimap) {
        Map<String, String> resultMap = new HashMap<>();
        for (Entry<String, List<String>> entry : multimap.entrySet()) {
            resultMap.put(entry.getKey(), TextUtils.join(",", entry.getValue()));
        }
        return resultMap;
    }

    protected WebResourceResponse createEmptyResponse() {
        return createResponse(MediaType.parse("video/mp4"), new ByteArrayInputStream("".getBytes(Charset.forName("UTF8"))));
    }
}
