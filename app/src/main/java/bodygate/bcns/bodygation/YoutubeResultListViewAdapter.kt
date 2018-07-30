package bodygate.bcns.bodygation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.google.api.services.youtube.model.SearchResult

class YoutubeResultListViewAdapter(val mValues: MutableList<SearchResult>, val context:Context, val itemClick:(String) -> Unit) : RecyclerView.Adapter<YoutubeResultListViewAdapter.ViewHolder>() {

    private val UNINITIALIZED = 1
    private val INITIALIZING = 2
    private val INITIALIZED = 3
    private val blackColor = Color.parseColor("#FF000000")
    private val transparentColor = Color.parseColor("#00000000")
    var dataitem = SearchResult()
    var datalist:MutableList<SearchResult> = ArrayList()
    val TAG = "YoutubeListViewAdapter_"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    fun setLkItems(bdItems1: List<SearchResult>) {
        mValues.addAll(bdItems1)
        this.notifyItemInserted(mValues.size - 1)
    }
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.mItem = mValues[position]
        holder.tvTitle.text = mValues[position].snippet!!.title
        holder.tvDetaile.text = mValues[position].snippet!!.description
        holder.ivYtLogo.setVisibility(View.VISIBLE)
        holder.ytThubnailView.setTag(R.id.videoid, mValues[position].id!!.videoId)
        holder.ivYtLogo.setBackgroundColor(blackColor)
        val state = holder.ytThubnailView.getTag(R.id.initialize) as Int
        if (state == UNINITIALIZED) {
            holder.initialize()
        } else if (state == INITIALIZED) {
            val loader = holder.ytThubnailView.getTag(R.id.thumbnailloader) as YouTubeThumbnailLoader
            loader.setVideo(mValues[position].id!!.videoId)
        }
        holder.ivYtLogo.setOnClickListener{itemClick(mValues[position].id!!.videoId!!)}
        holder.ytThubnailView.setOnClickListener{itemClick(mValues[position].id!!.videoId!!)}
    }
    override fun getItemCount(): Int {
            return mValues.size
    }
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val ytThubnailView: YouTubeThumbnailView
        val ivYtLogo: ImageView
        val tvTitle: TextView
        val tvDetaile: TextView
        var mItem:SearchResult? = null

        init {
            ytThubnailView = itemView.findViewById<View>(R.id.yt_thumbnail) as YouTubeThumbnailView
            ivYtLogo = itemView.findViewById<View>(R.id.iv_yt_logo) as ImageView
            tvTitle = itemView.findViewById<View>(R.id.tv_title) as TextView
            tvDetaile= itemView.findViewById<View>(R.id.tv_detail) as TextView

            initialize()
        }
        fun initialize(){
            ivYtLogo.setBackgroundColor(blackColor);
            ytThubnailView.setTag(R.id.initialize, INITIALIZING);
            ytThubnailView.setTag(R.id.thumbnailloader, null);
            ytThubnailView.setTag(R.id.videoid, "");

            ytThubnailView.initialize(context.getString(R.string.API_key), object : YouTubeThumbnailView.OnInitializedListener{
                override fun onInitializationSuccess(p0: YouTubeThumbnailView?, p1: YouTubeThumbnailLoader?) {
                    ytThubnailView.setTag(R.id.initialize, INITIALIZED);
                    ytThubnailView.setTag(R.id.thumbnailloader, p1);
                    p1!!.setOnThumbnailLoadedListener(object : YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                        override fun onThumbnailLoaded(p0: YouTubeThumbnailView?, p1: String?) {
                            val currentVideoId = ytThubnailView.getTag (R.id.videoid)
                            if (currentVideoId.equals(p1)) {
                                ivYtLogo.setBackgroundColor(transparentColor);
                            } else {
                                ivYtLogo.setBackgroundColor(blackColor);
                            }
                        }

                        override fun onThumbnailError(p0: YouTubeThumbnailView?, p1: YouTubeThumbnailLoader.ErrorReason?) {
                            ivYtLogo.setBackgroundColor(blackColor);
                        }
                    })
                    val videoId = ytThubnailView.getTag(R.id.videoid) as String
                    if (!videoId.isEmpty()) {
                        p1.setVideo(videoId)
                    }
                }
                override fun onInitializationFailure(p0: YouTubeThumbnailView?, p1: YouTubeInitializationResult?) {
                }
            })
        }
    }
}
