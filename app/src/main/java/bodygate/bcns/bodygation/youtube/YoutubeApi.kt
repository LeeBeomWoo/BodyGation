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



interface YoutubeApi {
    @GET("search")
    fun searchVideo(@Query("q") query:String,
    @Query("key") key:String,
    @Query("part") part:String,
                    @Query("type") type:String,
                    @Query("regionCode") regionCode:String,
    @Query("maxResults") maxResults:Int):Call<YoutubeResponse>
}