package bodygate.bcns.bodygation.youtube

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import com.google.api.services.youtube.model.ChannelListResponse
import retrofit2.Call
import retrofit2.Callback
import okhttp3.ResponseBody
import retrofit2.http.Headers


interface YoutubeApi {
   // @Headers("Content-Type: application/json;charset=utf-8")
    @GET("search")
    fun searchVideo(@Query("part") part:String,
                    @Query("maxResults") maxResults:Int,
                    @Query("q") query:String,
                    @Query("regionCode") regionCode:String,
                    @Query("type") type:String,
                    @Query("key") key:String
    ):Call<YoutubeResponse>
    companion object Factory {
        val BASE_URL = "https://www.googleapis.com/youtube/v3/"
        fun create(): YoutubeApi {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(YoutubeApi::class.java)
        }
    }
}