package com.homubee.waterrate.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.homubee.waterrate.databinding.ItemPublicRateBinding
import com.homubee.waterrate.model.PublicRate

class PublicRateViewHolder(val binding: ItemPublicRateBinding) : RecyclerView.ViewHolder(binding.root)

class PublicRateAdapter(val datas: MutableList<PublicRate>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var buttonClickListener: ButtonCallbackListener

    fun add(data: PublicRate) {
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
            RecyclerView.ViewHolder = PublicRateViewHolder(ItemPublicRateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as PublicRateViewHolder).binding
        binding.tvNameInput.text = datas[position].name
        binding.tvCountInput.text = datas[position].lastMonthCount.toString()

        // 삭제 버튼 이벤트
        binding.btnDelete.setOnClickListener {
            delete(position)
            buttonClickListener.callBack()
        }
    }
}