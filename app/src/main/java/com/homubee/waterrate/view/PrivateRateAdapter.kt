package com.homubee.waterrate.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.homubee.waterrate.databinding.ItemPublicRateBinding
import com.homubee.waterrate.model.PrivateRate


class PrivateRateViewHolder(val binding: ItemPublicRateBinding) : RecyclerView.ViewHolder(binding.root)

class PrivateRateAdapter(val datas: MutableList<PrivateRate>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var buttonClickListener: ButtonCallbackListener

    fun add(data: PrivateRate) {
        datas.add(data)
        notifyItemInserted(itemCount)
    }
    private fun delete(position: Int) {
        datas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    interface ButtonCallbackListener {
        fun callBack()
    }

    override fun getItemCount(): Int {
        return datas.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder = PrivateRateViewHolder(ItemPublicRateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val DYNAMICTEXTVIEW_ID = 2000
        val binding = (holder as PrivateRateViewHolder).binding
        binding.tvNameInput.text = datas[position].name
        binding.tvCountInput.text = datas[position].lastMonthCount.toString()

        // 동적으로 추가되는 공용 수도 관련 출력 부분
        if (!datas[position].publicList.isEmpty()) {
            val publicListTextView = TextView(holder.itemView.context)
            val clparams = ConstraintLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).run {
                topToBottom = binding.btnDelete.id
                leftToLeft = binding.root.id
                rightToRight = binding.root.id
                bottomToTop = binding.viewLine.id
                leftMargin = Math.round(10* binding.root.resources.displayMetrics.density)
                bottomMargin = Math.round(10* binding.root.resources.displayMetrics.density)
                this
            }

            publicListTextView.layoutParams = clparams
            publicListTextView.id = DYNAMICTEXTVIEW_ID
            publicListTextView.text = "공용 수도 : "

            for (i in datas[position].publicList.indices) {
                publicListTextView.text = publicListTextView.text.toString() + if (i != 0) {", "} else {""} + datas[position].publicList[i]
            }
            (binding.btnDelete.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = DYNAMICTEXTVIEW_ID
            (binding.tvCount.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = DYNAMICTEXTVIEW_ID
            (binding.tvCountInput.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = DYNAMICTEXTVIEW_ID
            (binding.tvName.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = DYNAMICTEXTVIEW_ID
            (binding.tvNameInput.layoutParams as ConstraintLayout.LayoutParams).bottomToTop = DYNAMICTEXTVIEW_ID
            binding.root.addView(publicListTextView)
        }

        // 삭제 버튼 이벤트
        binding.btnDelete.setOnClickListener {
            delete(position)
            buttonClickListener.callBack()
        }
    }
}