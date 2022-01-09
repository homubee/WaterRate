package com.homubee.waterrate.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.homubee.waterrate.databinding.ItemPublicRateBinding
import com.homubee.waterrate.model.PrivateRate


class PrivateRateViewHolder(val binding: ItemPublicRateBinding) : RecyclerView.ViewHolder(binding.root)

class PrivateRateAdapter(val datas: MutableList<PrivateRate>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun add(data: PrivateRate) {
        datas.add(data)
        notifyItemInserted(itemCount)
    }
    private fun delete(position: Int) {
        datas.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemCount(): Int {
        return datas.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder = PrivateRateViewHolder(ItemPublicRateBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as PrivateRateViewHolder).binding
        binding.tvNameInput.text = datas[position].name
        binding.tvCountInput.text = datas[position].lastMonthCount.toString()

        // 삭제 버튼 이벤트
        binding.btnDelete.setOnClickListener {
            delete(position)
        }
    }
}