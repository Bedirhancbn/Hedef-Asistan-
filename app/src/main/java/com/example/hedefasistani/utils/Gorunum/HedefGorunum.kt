package com.example.hedefasistani.utils.Gorunum

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hedefasistani.databinding.HedefBinding
import com.example.hedefasistani.utils.data.HedefData

// hedef verileri
class HedefGorunum(private val list: MutableList<HedefData>) : RecyclerView.Adapter<HedefGorunum.TaskViewHolder>() {

    private  val TAG = "HedefGorunum"
    private var listener:TaskAdapterInterface? = null
    fun setListener(listener:TaskAdapterInterface){
        this.listener = listener
    }
    // her hedefin görünümünü tutar
    class TaskViewHolder(val binding: HedefBinding) : RecyclerView.ViewHolder(binding.root)

    // her hedefin görünümünü oluşturur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding =
            HedefBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // görünümü veriyle doldurur
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.hedefAmac.text = this.task

                Log.d(TAG, "onBindViewHolder: "+this)
                binding.hedefDuzenle.setOnClickListener {
                    listener?.hedefDuzenleme(this , position)
                }

                binding.hedefSil.setOnClickListener {
                    listener?.hedefSilme(this , position)
                }
                binding.detayButon.setOnClickListener {
                    listener?.hedefDetay(this , position)
                }
            }
        }
    }

    // recyclerview de kaç hedef olurcak
    override fun getItemCount(): Int {
        return list.size
    }
    // hedefleri silme ve düzenleme isteği
    //için arayüz
    interface TaskAdapterInterface{
        fun hedefSilme(hedefData: HedefData, position : Int)
        fun hedefDuzenleme(hedefData: HedefData, position: Int)
        fun hedefDetay(hedefData: HedefData, position: Int)
    }

}
