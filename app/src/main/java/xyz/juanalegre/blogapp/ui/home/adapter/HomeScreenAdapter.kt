package xyz.juanalegre.blogapp.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xyz.juanalegre.blogapp.R
import xyz.juanalegre.blogapp.core.BaseViewHolder
import xyz.juanalegre.blogapp.core.TimeUtils
import xyz.juanalegre.blogapp.data.model.Post
import xyz.juanalegre.blogapp.databinding.PostItemViewBinding

class HomeScreenAdapter(private val postList: List<Post>, private val onPostClickListener: OnPostClickListener): RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var postClickListener: OnPostClickListener? = null

    init {
        postClickListener = onPostClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = PostItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeScreenViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder){
            is HomeScreenViewHolder -> {
                holder.bind(postList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private inner class HomeScreenViewHolder(
        val binding: PostItemViewBinding,
        val context: Context
        ): BaseViewHolder<Post>(binding.root) {
        override fun bind(item: Post) {
            Glide.with(context).load(item.post_image).centerCrop().into(binding.postImage)
            Glide.with(context).load(item.poster?.profile_picture).centerCrop().into(binding.profilePicture)
            binding.profileName.text = item.poster?.username
            binding.postDescription.text = item.post_description
            val createdAt = (item.created_at?.time?.div(1000L))?.let {
                TimeUtils.getTimeAgo(it.toInt())
            }
            binding.postTimestamp.text = createdAt
            tintHeartIcon(item)
            setLikeClickAction(item)
            setupLikeCount(item)
        }

        private fun setLikeClickAction(post: Post) {
            binding.imageLike.setOnClickListener {
                if (post.liked) post.apply { liked = false } else post.apply { liked = true }
                tintHeartIcon(post)
                postClickListener?.onLikeButtonClick(post, post.liked)
            }
        }

        private fun tintHeartIcon(post: Post){
            if (!post.liked){
                binding.imageLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_border_24))
            } else {
                binding.imageLike.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24))
                binding.imageLike.setColorFilter(R.color.red)
            }
        }

        private fun setupLikeCount(post: Post){
            if (post.likes > 0){
                binding.textLikesCount.visibility = View.VISIBLE
                binding.textLikesCount.text = "${post.likes} likes"
            } else{
                binding.textLikesCount.visibility = View.GONE
            }
        }
    }
}

interface OnPostClickListener{
    fun onLikeButtonClick(post: Post, liked: Boolean)
}