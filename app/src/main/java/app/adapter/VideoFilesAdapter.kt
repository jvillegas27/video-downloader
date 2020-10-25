package app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.R
import domain.model.VideoFileModel
import kotlinx.android.synthetic.main.video_item.view.*

class VideoFilesAdapter(private val clickListener: (filePath: String) -> Unit) :
    RecyclerView.Adapter<VideoFilesAdapter.ViewHolder>() {

    private val items = mutableListOf<VideoFileModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = items[position]
    }

    override fun getItemCount() = items.size

    fun submitList(newList: List<VideoFileModel>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int): VideoFileModel {
        val temp = items[position]
        items.removeAt(position)
        notifyItemRemoved(position)
        return temp
    }

    class ViewHolder(itemView: View, private val clickListener: (filePath: String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        var item: VideoFileModel? = null
            set(value) {
                field = value
                value?.let {
                    bindData(it)
                }
            }

        private fun bindData(videoFileModel: VideoFileModel) {
            with(itemView) {
                video_item_name.text = videoFileModel.fileName
                setOnClickListener {
                    clickListener.invoke(videoFileModel.filePath)
                }
            }
        }
    }

}