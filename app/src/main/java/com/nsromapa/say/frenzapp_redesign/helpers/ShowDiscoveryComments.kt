package com.nsromapa.say.frenzapp_redesign.helpers

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.heyalex.bottomdrawer.BottomDrawerDialog
import com.github.heyalex.bottomdrawer.BottomDrawerFragment
import com.github.heyalex.handle.PullHandleView
import com.nsromapa.say.frenzapp_redesign.R
import com.nsromapa.say.frenzapp_redesign.models.DiscoveryComment
import kotlinx.android.synthetic.main.item_discovery_comment.view.*
import org.json.JSONArray
import org.json.JSONException

class ShowDiscoveryComments(private var categoryId: String,
                            private var category_name_string: String,
                            private var icon_letter: String,
                            private var number_of_subcategories_string: String,
                            private var subCategory_jObj: String) : BottomDrawerFragment() {

    private var alphaCancelButton = 0f
    private lateinit var cancelButton: ImageView

    private lateinit var text_header: TextView
    private lateinit var category_name: TextView
    private lateinit var number_of_subcategories: TextView
    private lateinit var bottom_sheetrecycler: RecyclerView
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


        text_header = view.findViewById(R.id.text_header)
        text_header.text = icon_letter

        category_name = view.findViewById(R.id.category_name)
        category_name.text = category_name_string

        number_of_subcategories = view.findViewById(R.id.number_of_subcategories)
        number_of_subcategories.text = number_of_subcategories_string

        bottom_sheetrecycler = view.findViewById(R.id.bottom_sheetrecycler)

        commentList.add(DiscoveryComment(
                "Comment_id",
                "Comment Name",
                "commentImage.png",
                "json Object"))





        try {
//            val jsonObject = JSONObject(subCategory_jObj)
            val jsonArray = JSONArray(subCategory_jObj)
            //now looping through all the elements of the json array
            for (i in 0 until jsonArray.length()) {
                val subCateObj = jsonArray.getJSONObject(i)
                commentList.add(DiscoveryComment(
                        subCateObj.getString("id"),
                        subCateObj.getString("name"),
                        subCateObj.getString("image"),
                        subCateObj.toString()))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        setLinearAdapter(commentList as ArrayList<DiscoveryComment>)
        return view
    }


    class ParticipantHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.item_category_name!!
        val image = itemView.item_category_image!!
        val click = itemView.item_category_layout!!
    }

    private fun setLinearAdapter(subCategories: ArrayList<DiscoveryComment>?) {

//        bottom_sheetrecycler.layoutManager = GridLayoutManager(context, 4)
        bottom_sheetrecycler.layoutManager = LinearLayoutManager(context)
        bottom_sheetrecycler.setHasFixedSize(true)

        val horizontalAdapter = object : RecyclerView.Adapter<ParticipantHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ParticipantHolder {
                return ParticipantHolder(
                        layoutInflater.inflate(
                                R.layout.item_discovery_comment,
                                p0,
                                false
                        )
                )
            }

            override fun getItemCount(): Int = subCategories!!.size

            override fun onBindViewHolder(p0: ParticipantHolder, p1: Int) {
                context?.let {
                    Glide.with(it)
                            .load(subCategories?.get(p1)!!.commenter_image)
                            .into(p0.image)
                }
                p0.name.text = subCategories?.get(p1)!!.commenter_name//
//                p0.click.setOnClickListener {
//                    val intent = Intent(context, ProductsActivity::class.java).apply {
//                        putExtra("sub_category_id", subCategories[p1].subCategory_id)
//                        putExtra("category_name", subCategories[p1].subCategory_name)
//                        putExtra("sub_category_JObj", subCategories[p1].subCategory_JObj)
//                        putExtra("category_name_string", category_name_string)
//                    }
//                    context!!.startActivity(intent)
//                }
            }

        }

        bottom_sheetrecycler.adapter = horizontalAdapter
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
