package com.homubee.waterrate.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.homubee.waterrate.databinding.ItemWaterRateBinding
import com.homubee.waterrate.model.WaterRate


class WaterRateViewHolder(val binding: ItemWaterRateBinding) : RecyclerView.ViewHolder(binding.root)

class WaterRateAdapter(private val type: Int, val dataList: MutableList<WaterRate>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var buttonClickListener: ButtonCallbackListener

    fun add(data: WaterRate) {
        dataList.add(data)
        notifyItemInserted(itemCount)
    }
    private fun delete(position: Int) : String {
        val name = dataList[position].name
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
        return name
    }

    interface ButtonCallbackListener {
        fun callBack(name: String)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder = WaterRateViewHolder(ItemWaterRateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as WaterRateViewHolder).binding
        binding.tvNameInput.text = dataList[position].name
        binding.tvCountInput.text = dataList[position].lastMonthCount.toString()

        // 공용 수도 관련 출력 부분
        if (type == 1) {
            binding.tvName.text = "상호명 :"
            binding.tvPublic.visibility = View.VISIBLE
            if (!dataList[position].waterRateList.isEmpty()) {
                binding.tvPublic.apply {
                    text = "공용 수도 : "
                    for (i in dataList[position].waterRateList.indices) {
                        text = text.toString() + if (i != 0) {", "} else {""} + dataList[position].waterRateList[i]
                    }
                }
            } else {
                binding.tvPublic.text = "공용 수도 : 없음"
            }
        }

        // 삭제 버튼 이벤트
        binding.btnDelete.setOnClickListener {
            val name = delete(position)
            buttonClickListener.callBack(name)
        }
    }
}