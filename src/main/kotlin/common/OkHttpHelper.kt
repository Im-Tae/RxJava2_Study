package common

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class OkHttpHelper {
    companion object {
        private val client : OkHttpClient = OkHttpClient()

        fun get(url: String): String {
            val request: Request = Request.Builder()
                .url(url)
                .build()

            val response: Response = client.newCall(request).execute()
            return response.body().string()
        }
    }
}