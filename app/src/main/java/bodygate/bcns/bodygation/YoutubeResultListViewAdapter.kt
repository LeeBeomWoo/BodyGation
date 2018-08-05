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
import com.squareup.picasso.Picasso

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
        val i = mValues.size
        mValues.addAll(bdItems1)
        this.notifyItemInserted(i)
    }
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.mItem = mValues[position]
        holder.tvTitle.text = mValues[position].snippet!!.title
        holder.tvDetaile.text = mValues[position].snippet!!.description
        holder.ivYtLogo.setVisibility(View.VISIBLE)
        //holder.ivYtLogo.setBackgroundColor(blackColor)
        Picasso.get().load(mValues[position].snippet!!.thumbnails.default.url.toString()).centerInside().fit().into(holder.ytThubnailView)
        holder.ivYtLogo.setOnClickListener{itemClick(mValues[position].id!!.videoId!!)}
        holder.ytThubnailView.setOnClickListener{itemClick(mValues[position].id!!.videoId!!)}
    }
    override fun getItemCount(): Int {
            return mValues.size
    }
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val ytThubnailView: ImageView
        val ivYtLogo: ImageView
        val tvTitle: TextView
        val tvDetaile: TextView
        var mItem:SearchResult? = null

        init {
            ytThubnailView = itemView.findViewById<View>(R.id.yt_thumbnail) as ImageView
            ivYtLogo = itemView.findViewById<View>(R.id.iv_yt_logo) as ImageView
            tvTitle = itemView.findViewById<View>(R.id.tv_title) as TextView
            tvDetaile= itemView.findViewById<View>(R.id.tv_detail) as TextView

        }
    }
}
