package bodygate.bcns.bodygation.youtube

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit



/**
 * Created by LeeBeomWoo on 2018-03-26.
 */
object APIService {
    val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    val youtubeApi = createService(YoutubeApi::class.java)
    private fun <LPL> createService(cls: Class<LPL>): LPL {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build()
        return retrofit.create(cls)
    }
}