package bodygate.bcns.bodygation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent.getIntent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import bodygate.bcns.bodygation.dummy.DummyContent.DummyItem
import bodygate.bcns.bodygation.dummy.listContent
import bodygate.bcns.bodygation.youtube.YoutubeResponse
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView


/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class YoutubeResultListViewAdapter(private val mValues: List<YoutubeResponse.Items>, private val context:Context) : RecyclerView.Adapter<YoutubeResultListViewAdapter.ViewHolder>() {
    val TAG = "YoutubeListViewAdapter_"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].kind
        holder.mTitleView.text = mValues[position].snippet!!.title
        holder.mVideoView.initialize(context.getString(R.string.API_key), object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider, youTubePlayer: YouTubePlayer, b: Boolean) {
                Log.i(TAG, "onInitializationSuccess")
                if (!b) {
                    val videoId = mValues[position].snippet!!.thumbnails!!.default!!.url
                    youTubePlayer.cueVideo(videoId)
                }
            }
            override fun onInitializationFailure(provider: YouTubePlayer.Provider, youTubeInitializationResult: YouTubeInitializationResult) {
                Log.i(TAG, "onInitializationFailure")
                Toast.makeText(this@YoutubeResultListViewAdapter.context, context.getString(R.string.failed), Toast.LENGTH_LONG).show()
            }
        });
        holder.mView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
            return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mTitleView: TextView
        val mVideoView: YouTubePlayerView
        var mItem: YoutubeResponse.Items? = null

        init {
            mIdView = mView.findViewById<View>(R.id.id) as TextView
            mTitleView = mView.findViewById<View>(R.id.content) as TextView
            mVideoView = mView.findViewById<View>(R.id.listplayer) as YouTubePlayerView
        }
    }
}
