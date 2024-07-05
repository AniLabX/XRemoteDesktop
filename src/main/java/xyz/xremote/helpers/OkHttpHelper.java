package xyz.xremote.helpers;

import xyz.xremote.Constants;
import xyz.xremote.utils.Utils;
import io.reactivex.Single;
import okhttp3.*;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class OkHttpHelper {

    public static void downloadAsync(String url, File outputFile, OnDownloadCallback callback) {
        new Thread(() -> download(url, outputFile, callback)).start();
    }

    public static void download(String url, File outputFile, OnDownloadCallback callback) {
        InputStream is = null;
        BufferedInputStream input = null;
        OutputStream output = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = getOkhttpBuilderInstance().build()
                    .newCall(request)
                    .execute();
            ResponseBody responseBody = response.body();
            long contentLength = responseBody.contentLength();

            is = responseBody.byteStream();
            input = new BufferedInputStream(is);
            output = new FileOutputStream(outputFile);

            byte[] data = new byte[1024];

            int count;
            long read = 0;
            while ((count = input.read(data)) != -1) {
                read += count;
                output.write(data, 0, count);
                callback.onProgress(read, contentLength);
            }

            callback.onFinish(outputFile);
            output.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            Utils.closeQuietly(is);
            Utils.closeQuietly(output);
            Utils.closeQuietly(input);
        }
    }

    public static Single<String> GET(String url) {
        return Single.create(subscriber -> {
            Response response = null;
            try {
                response = getOkhttpBuilderInstance()
                        .build()
                        .newCall(
                                new Request.Builder()
                                        .url(url)
                                        .header("User-Agent", Constants.USER_AGENT)
                                        .build()
                        )
                        .execute();

                subscriber.onSuccess(response.body().string());
            } catch (Exception ex) {
                subscriber.onError(ex);
            } finally {
                Utils.closeQuietly(response);
            }
        });
    }

    private static OkHttpClient.Builder getOkhttpBuilderInstance() {
        return new OkHttpClient.Builder()
                .connectionSpecs(Arrays.asList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .allEnabledTlsVersions()
                        .allEnabledCipherSuites()
                        .build(), ConnectionSpec.CLEARTEXT))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2));
    }

    public interface OnDownloadCallback {
        void onProgress(long count, long total);

        void onFinish(File file);
    }
}