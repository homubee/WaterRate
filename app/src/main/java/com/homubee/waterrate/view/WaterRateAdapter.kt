package com.homubee.waterrate.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.homubee.waterrate.databinding.ItemWaterRateBinding
import com.homubee.waterrate.model.WaterRate

/**
 * 수도 요금 뷰 홀더
 */
class WaterRateViewHolder(val binding: ItemWaterRateBinding) : RecyclerView.ViewHolder(binding.root)

/**
 * 수도 요금 어댑터
 *
 * 입력 받은 내용으로 상호/설비명, 전월지침, 공용 수도 목록 출력
 */
class WaterRateAdapter(val dataList: MutableList<WaterRate>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var buttonClickListener: ButtonCallbackListener

    /**
     * Add water rate data to adapter.
     *
     * @param data 수도요금 데이터
     * @return 없음
     */
    fun add(data: WaterRate) {
        dataList.add(data)
        notifyItemInserted(itemCount)
    }
    /**
     * Delete water rate data from adapter at input position.
     *
     * @param position 수도요금 데이터
     * @return 삭제된 수도요금 설비/상호 이름
     */
    private fun delete(position: Int) : String {
        val name = dataList[position].name
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
        return name
    }

    /**
     * Callback Listener on delete button.
     *
     * @return 없음
     */
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
        if (dataList[position].type == 2) {
            binding.tvCountInput.text = "없음"
        }

        // 공용 수도 관련 출력 부분 (개인 수도만 출력)
        if (dataList[position].type != 0) {
            binding.tvName.text = "상호명 :"
            binding.tvPublic.visibility = View.VISIBLE
            // 공용 수도 사용 유무에 따라 내용 출력
            if (dataList[position].waterRateList.isNotEmpty()) {
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