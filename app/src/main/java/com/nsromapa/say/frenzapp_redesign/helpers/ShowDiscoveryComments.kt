package com.nsromapa.say.frenzapp_redesign.helpers

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PullHandleView
import com.nsromapa.say.frenzapp_redesign.R
import com.nsromapa.say.frenzapp_redesign.models.DiscoveryComment
import com.nsromapa.say.frenzapp_redesign.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_discovery_comment.view.*
import org.json.JSONArray
import org.json.JSONException

class ShowDiscoveryComments(private var dicoveryComment_jObj: String,
                            private var description:DiscoveryComment) : BottomDrawerFragment() {

    private var alphaCancelButton = 0f
    private lateinit var cancelButton: ImageView

    private lateinit var user_image: CircleImageView
    private lateinit var user_name: TextView
    private lateinit var create_comment: EditText
    private lateinit var bottom_sheetrecycler: RecyclerView
    private lateinit var send_comment : ImageView

    var commentList: MutableList<DiscoveryComment> = ArrayList()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_discovery, container, false)
        cancelButton = view.findViewById(R.id.cancel)
        addBottomSheetCallback {
            onSlide { _, slideOffset ->
                alphaCancelButton = (slideOffset - PERCENT) * (1f / (1f - PERCENT))
                cancelButton.alpha = alphaCancelButton
                cancelButton.isEnabled = alphaCancelButton > 0
            }
        }
        cancelButton.setOnClickListener { dismissWithBehavior() }


        user_image = view.findViewById(R.id.user_image)
        context?.let {
            Glide.with(it)
                    .load(Utils.getUserImage())
                    .into(user_image)
        }

        user_name = view.findViewById(R.id.user_name)
        user_name.text = Utils.getUserName()

        create_comment = view.findViewById(R.id.create_comment)
        send_comment = view.findViewById(R.id.send_comment)
        send_comment.setOnClickListener { createComment(create_comment) }

        bottom_sheetrecycler = view.findViewById(R.id.bottom_sheetrecycler)



        commentList.add(description)

        try {
            val jsonArray = JSONArray(dicoveryComment_jObj)
            for (i in 0 until jsonArray.length()) {

                val commentObj = jsonArray.getJSONObject(i)
                val commenterObj = commentObj.getJSONObject("1")

                commentList.add(DiscoveryComment(
                        commenterObj.getString("id"),
                        commenterObj.getString("username"),
                        commenterObj.getString("image"),
                        commenterObj.toString(),
                        commentObj.getString("id"),
                        commentObj.getString("0"),
                        commentObj.getString("comment"),
                        commentObj.getString("likes"),
                        commentObj.getString("dislikes"),
                        "comment"))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        setLinearAdapter(commentList as ArrayList<DiscoveryComment>)
        return view
    }



    class commentsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commenter_name = itemView.commenter_name!!
        val commenter_image = itemView.commenter_image!!
        val comment_time = itemView.comment_time!!
        val comment_tv = itemView.comment_tv!!
        val actionsContainerLL = itemView.actionsContainerLL!!
        val likers = itemView.likers!!
        val dislikers = itemView.dislikers!!
        val like_comment = itemView.like_comment!!
        val dislike_comment = itemView.dislike_comment!!
        val delete_comment = itemView.delete_comment!!

    }

    private fun setLinearAdapter(commentList: ArrayList<DiscoveryComment>?) {

        bottom_sheetrecycler.layoutManager = LinearLayoutManager(context)
        bottom_sheetrecycler.setHasFixedSize(true)

        val horizontalAdapter = object : RecyclerView.Adapter<commentsHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): commentsHolder {
                return commentsHolder(
                        layoutInflater.inflate(
                                R.layout.item_discovery_comment,
                                p0,
                                false
                        )
                )
            }

            override fun getItemCount(): Int = commentList!!.size

            override fun onBindViewHolder(p0: commentsHolder, p1: Int) {
                context?.let {
                    Glide.with(it)
                            .load(commentList?.get(p1)!!.commenter_image)
                            .into(p0.commenter_image)
                }
                p0.commenter_name.text = commentList?.get(p1)!!.commenter_name
                p0.comment_time.text = commentList.get(p1).comment_time
                p0.comment_tv.text = commentList.get(p1).comment
                p0.likers.text = commentList.get(p1).comment_likes
                p0.dislikers.text = commentList.get(p1).comment_dislikes

                p0.like_comment.setOnClickListener{
                    likeComment();
                }

                p0.dislike_comment.setOnClickListener{
                    dislikeComment();
                }


                if (commentList.get(p1).comment_or_description.equals("comment")
                        && commentList.get(p1).commenter_id.equals(Utils.getUserUid())){
                    p0.delete_comment.visibility = View.VISIBLE
                    p0.delete_comment.setOnClickListener {
                        deleteComment(commentList.get(p1).comment_id)
                        commentList.removeAt(p1)
                    }
                }
                else
                    p0.delete_comment.visibility = View.GONE


                if (commentList.get(p1).comment_or_description.equals("description")){
                    p0.actionsContainerLL.visibility = View.GONE
                }

            }

        }

        bottom_sheetrecycler.adapter = horizontalAdapter
    }





    private fun dislikeComment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun likeComment() {

    }


    private fun deleteComment(commentId: String?) {

    }

    private fun createComment(createComment: EditText?) {

    }




    override fun configureBottomDrawer(): BottomDrawerDialog {
        return BottomDrawerDialog.build(context!!) {
            theme = R.style.Pull
            handleView = PullHandleView(context).apply {
                val widthHandle =
                        resources.getDimensionPixelSize(R.dimen.thirty_six)
                val heightHandle =
                        resources.getDimensionPixelSize(R.dimen.twelve)
                val params =
                        FrameLayout.LayoutParams(widthHandle, heightHandle, Gravity.CENTER_HORIZONTAL)

                params.topMargin =
                        resources.getDimensionPixelSize(R.dimen.margin_view_typical)

                layoutParams = params
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat("alphaCancelButton", alphaCancelButton)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        alphaCancelButton = savedInstanceState?.getFloat("alphaCancelButton") ?: 0f
    }

    companion object {
        const val PERCENT = 0.65f
    }
}
