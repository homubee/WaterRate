package com.homubee.waterrate.view

import android.view.LayoutInflater
import android.view.View
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
        val binding = (holder as PrivateRateViewHolder).binding
        binding.tvName.text = "상호명 :"
        binding.tvNameInput.text = datas[position].name
        binding.tvCountInput.text = datas[position].lastMonthCount.toString()
        binding.tvPublic.visibility = View.VISIBLE

        // 공용 수도 관련 출력 부분
        if (!datas[position].publicList.isEmpty()) {
            binding.tvPublic.apply {
                text = "공용 수도 : "
                for (i in datas[position].publicList.indices) {
                    text = text.toString() + if (i != 0) {", "} else {""} + datas[position].publicList[i]
                }
            }
        } else {
            binding.tvPublic.text = "공용 수도 : 없음"
        }


        // 삭제 버튼 이벤트
        binding.btnDelete.setOnClickListener {
            delete(position)
            buttonClickListener.callBack()
        }
    }
}