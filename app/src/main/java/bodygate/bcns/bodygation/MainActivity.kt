@file:Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")

package bodygate.bcns.bodygation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Surface
import bodygate.bcns.bodygation.navigationitem.FollowFragment
import bodygate.bcns.bodygation.navigationitem.YouTubeResult
import cn.gavinliu.android.lib.scale.config.ScaleConfig
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.SearchListResponse
import com.google.api.services.youtube.model.SearchResult
import com.google.common.io.BaseEncoding
import kotlinx.android.synthetic.main.fragment_follow.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.MessageDigest


@Suppress("DUPLICATE_LABEL_IN_WHEN", "CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity(), FollowFragment.OnFollowInteraction, YouTubeResult.OnYoutubeResultInteraction, PlayFragment.OnFragmentInteractionListener {
    override fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
                .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
        Log.i(TAG, "onConfigurationChanged_setCameraDisplayOrientation : " + result.toString())
    }

    override fun OnYoutubeResultInteraction() {
        TODO("not implemented") /* To change body of created functions use File | Settings | File Templates. */
    }

    val LIST_STATE_KEY:String = "recycler_list_state"
    var listState: Parcelable? = null
    val TAG: String = "MainActivity_"
    var page = ""
    var sectionInt = 0
    override var totalpage = 100
    override var visableFragment = ""
    private var doubleBackToExitPressedOnce: Boolean = false
    override val context:Context = this
    override var sendquery:String? = null
    var queryarr:ArrayList<String>? = null
    override var data: MutableList<SearchResult> = arrayListOf()
    var followFragment:Fragment? = null
    var youTubeResult:YouTubeResult? = null
    var playFragment: PlayFragment? = null

    override fun showVideo(s: String) {
        sectionInt = 2
        if(playFragment == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, PlayFragment.newInstance(s), "play")
                    .commit()
        }else{
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, playFragment as PlayFragment, "play")
                    .commit()
        }
    }
    fun addData(response: SearchListResponse) {
        Log.i(TAG, "addData")
        Log.i("test", "second")
        val body = response.items
        Log.i("query", body.toString())
        Log.i("response", response.toString())
        if(response.nextPageToken != null) {
            page = response.nextPageToken
            totalpage = (response.pageInfo.totalResults/5)
            Log.i("data_page", page)
            Log.i("data_page", totalpage.toString())
        }else{
            page = ""
        }
        data = body
        Log.i("data", data.toString())
    }

    override suspend fun getNetxtPage(q: String, api_Key: String, max_result: Int, more:Boolean) = withContext(Dispatchers.IO){
        val youTube = YouTube.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), object: HttpRequestInitializer {
            @Throws(IOException::class)
            override fun initialize(request:HttpRequest) {
                val SHA1 = getSHA1(packageName)
                request.headers.set("X-Android-Package", packageName)
                request.headers.set("X-Android-Cert", SHA1)
            }
        }).setApplicationName(packageName).build()
        val order = "relevance"
        totalpage -= 1
        Log.i("data_page", page)
        Log.i("data_page", totalpage.toString())
        if(totalpage > 1){
            val query = youTube.search().list("id, snippet")
            query.key = api_Key
            query.type = "video"
            if(page != "") {
                query.pageToken = page
            }
            query.fields = "items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/thumbnails/high/width,snippet/thumbnails/high/height), nextPageToken, pageInfo"
            query.q = q
            query.order = order
            query.maxResults = max_result.toLong()
            CoroutineScope(Dispatchers.Default).launch {
                // background thread
                val body = query.execute()
                               if(body.nextPageToken != null && body.nextPageToken != ""){
                                   page = body.nextPageToken
                               }else{
                                   page = ""
                               }
                               data = body.items
            }.join()
        }else{
            data.clear()
        }
    }
    @SuppressLint("PackageManagerGetSignatures")
    private fun getSHA1(packageName:String):String? {
        try
        {
            val signatures = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            for (signature in signatures)
            {
                val md:MessageDigest = MessageDigest.getInstance("SHA-1")
                Log.i(TAG, md.toString())
                md.update(signature.toByteArray())
                return BaseEncoding.base16().encode(md.digest())
            }
        }
        catch (e:PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null
    }
    override suspend fun getDatas(part: String, q: ArrayList<String>, api_Key: String, max_result: Int, more:Boolean) {
        Log.i(TAG, "getDatas")
        val youTube = YouTube.Builder(NetHttpTransport(), JacksonFactory.getDefaultInstance(), object: HttpRequestInitializer {
            @Throws(IOException::class)
            override fun initialize(request:HttpRequest) {
                val SHA1 = getSHA1(packageName)
                request.headers.set("X-Android-Package", packageName)
                request.headers.set("X-Android-Cert", SHA1)
            }
        }).setApplicationName(packageName).build()
        queryarr = q
        val searchType = "video"
        val a = q.toString().replace("[", "")
        val b = a.replace("]", "")
        val order = "relevance"
        val query = youTube.search().list("id, snippet")
        query.key = api_Key
        query.type = "video"
        query.fields = "items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/high/url,snippet/thumbnails/high/width,snippet/thumbnails/high/height), nextPageToken, pageInfo"
        val bReader = BufferedReader(InputStreamReader(b.byteInputStream()))
        sendquery = bReader.readLine()
        query.q = sendquery
        query.maxResults = max_result.toLong()
        query.order = order
        query.type = searchType
        Log.i("test", "first")
        CoroutineScope(Dispatchers.Default).launch {addData( query.execute())}.join()
        Log.i("test", "third")
    }
    override fun OnFollowInteraction(q: ArrayList<String>?, s:Int) {
        Log.i(TAG, "OnFollowInteraction")
        sectionInt = 1
        if(youTubeResult == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, YouTubeResult.newInstance(q!!), "youtube")
                    .commit()
        }else{
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, youTubeResult as YouTubeResult, "youtube")
                    .commit()
        }
    }

    override fun getpage(): String {
        return nextpage()
    }
    fun nextpage():String{
        return page
    }
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScaleConfig.create(this,
                1080, // Design Width
                1920, // Design Height
                (3).toFloat(),    // Design Density
                (3).toFloat(),    // Design FontScale
                ScaleConfig.DIMENS_UNIT_DP)
        setContentView(R.layout.activity_main)
        Log.i(TAG + "_", "onCreate")
        if (savedInstanceState != null) {
            sectionInt = savedInstanceState.getInt("sectionInt")
            if(sectionInt == 1) {
                listState = savedInstanceState.getParcelable(LIST_STATE_KEY)
            }
            queryarr = savedInstanceState.getStringArrayList("queryarr")
            sendquery = savedInstanceState.getString("sendquery")
            Log.i(TAG + "_", "play"+ "\n sendquery :" + sendquery)
        }
        followFragment = supportFragmentManager.findFragmentByTag("follow") as FollowFragment?
        youTubeResult = supportFragmentManager.findFragmentByTag("youtube") as YouTubeResult?
        playFragment = supportFragmentManager.findFragmentByTag("play") as PlayFragment?
        when(sectionInt){
            0->{
                if (followFragment == null) {
                    Log.i(TAG, "mainTabFragment")
                    supportFragmentManager
                            .beginTransaction()
                            .add(R.id.root_layout, FollowFragment.newInstance(), "follow")
                            .commit()
                }else{
                    supportFragmentManager
                            .beginTransaction().replace(R.id.root_layout, followFragment!!).commit()
                }
            }
            1->{
                if (youTubeResult == null) {
                    Log.i(TAG, "mainTabFragment")
                    supportFragmentManager
                            .beginTransaction()
                            .add(R.id.root_layout, YouTubeResult.newInstance(queryarr!!), "youtube")
                            .commit()
                }else{
                    supportFragmentManager
                            .beginTransaction().replace(R.id.root_layout, youTubeResult!!).commit()
                }
            }
            2->{
                if (playFragment == null) {
                    Log.i(TAG, "mainTabFragment")
                    supportFragmentManager
                            .beginTransaction()
                            .add(R.id.root_layout, PlayFragment.newInstance(sendquery!!), "play")
                            .commit()
                }else{
                    supportFragmentManager
                            .beginTransaction().replace(R.id.root_layout, playFragment!!).commit()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("sectionInt", sectionInt)
        //Save the fragment's instance
        if(sectionInt == 1) {
            listState = result_list.layoutManager!!.onSaveInstanceState()
            outState.putParcelable(LIST_STATE_KEY, listState)
        }
            outState.putStringArrayList("queryarr", queryarr)
            outState.putString("sendquery", sendquery)
    }

    override fun onResume() {
        super.onResume()
        if (listState != null) {
            result_list.layoutManager!!.onRestoreInstanceState(listState)
        }
    }

    override fun onBackPressed() {
        when(sectionInt){
            0-> {
                Log.i(TAG, "onBackPressed" + "doubleBackToExitPressedOnce")
                if (doubleBackToExitPressedOnce) {
                    Log.i(TAG, "onBackPressed" + "true")
                    moveTaskToBack(true)
                    android.os.Process.killProcess(android.os.Process.myPid())
                    System.exit(1)
                    return
                } else {
                    Log.i(TAG, "onBackPressed" + "false")
                    val builder = AlertDialog.Builder(this)
                    if (title != null) builder.setTitle(title)
                    builder.setMessage("프로그램을 종료하시겠습니까?")
                    builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            android.os.Process.killProcess(android.os.Process.myPid())
                        }
                    })
                    builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            doubleBackToExitPressedOnce = false
                            dialog!!.dismiss()
                        }
                    }
                    )
                    builder.show()
                    doubleBackToExitPressedOnce = true
                }
            }
            1->{
                Log.i(TAG, "onBackPressed" + " followFragment")
                sectionInt = 0
                doubleBackToExitPressedOnce = false
                if (followFragment == null) {
                    supportFragmentManager
                            .beginTransaction()
                            .add(R.id.root_layout, FollowFragment.newInstance(), "follow")
                            .commit()
                }else{
                    supportFragmentManager
                            .beginTransaction().replace(R.id.root_layout, followFragment!!).commit()
                }
            }
            2->{
                Log.i(TAG, "onBackPressed" + " youTubeResult")
                sectionInt = 1
                doubleBackToExitPressedOnce = false
                if (youTubeResult == null) {
                    supportFragmentManager
                            .beginTransaction()
                            .add(R.id.root_layout, YouTubeResult.newInstance(queryarr!!), "youtube")
                            .commit()
                }else{
                    supportFragmentManager
                            .beginTransaction().replace(R.id.root_layout, youTubeResult!!).commit()
                }
            }
        }
    }

}

