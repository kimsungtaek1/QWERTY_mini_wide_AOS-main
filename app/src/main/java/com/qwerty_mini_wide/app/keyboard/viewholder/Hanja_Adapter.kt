package com.qwerty_mini_wide.app.keyboard.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qwerty_mini_wide.app.R
import com.qwerty_mini_wide.app.keyboard.model.HanjaEntry
import com.qwerty_mini_wide.app.keyboard.model.KeyLetter

class Hanja_Adapter(
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<Hanja_Adapter.ViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(hanjaEntry: HanjaEntry)
    }

    private var items: List<HanjaEntry> = arrayListOf()
    // 1) ViewHolder 정의 (중첩 클래스)
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_hanja: TextView = itemView.findViewById(R.id.txt_hanja)
    }

    // 2) ViewHolder 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hanja_viewholder, parent, false)
        return ViewHolder(view)
    }

    // 3) 데이터 바인딩
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_hanja.text = items[position].meaning + " " + items[position].hanja
        holder.txt_hanja.setTextColor(if(KeyLetter.isLightMode)  Color.BLACK else Color.WHITE)
        // 클릭 이벤트 설정
        holder.itemView.setOnClickListener {
            listener.onItemClick(items[position])
        }
        // 클릭 리스너 등 추가 가능
    }

    override fun getItemCount(): Int = items.size

    fun setitem(items:List<HanjaEntry>){
        this.items = items
        notifyDataSetChanged()
    }
}