package com.example.hedefasistani.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.hedefasistani.databinding.FragmentHedefEklemeBinding
import com.example.hedefasistani.utils.data.HedefData
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat



class HedefEklemeFragment : DialogFragment() {

    //nesneler
    private lateinit var binding: FragmentHedefEklemeBinding
    private var listener: OnDialogNextBtnClickListener? = null
    private var hedefData: HedefData? = null

    // fragment içinde belirli bir butona tıklandığında veya
    // belirli bir işlem gerçekleştiğinde çağrılacak olan metodları içerir.
    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "HedefEklemeFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            HedefEklemeFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // xml dosyasına erişim
        binding = FragmentHedefEklemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //fragmentin görünümünün düzgün bir şekilde oluşturulmasını sağlar
        super.onViewCreated(view, savedInstanceState)

        //nesnenin hedef adını girer eğer boş değilse
        if (arguments != null) {
            hedefData = HedefData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.hedefYazi.setText(hedefData?.task)
        }

        binding.tarihSec.setOnClickListener {
            takvimiGoster(binding.tarihSec)
        }

        binding.tarihSec2.setOnClickListener {
            takvimiGoster(binding.tarihSec2)
        }

        binding.kapat.setOnClickListener {
            dismiss()
        }

        binding.ileriButon.setOnClickListener {
            val todoTask = binding.hedefYazi.text.toString()
            if (todoTask.isNotEmpty()) {
                // TextView'lardaki tarihleri ayrıştırın
                val format = SimpleDateFormat("dd/mm/yyyy")
                val baslangicTarih = format.parse(binding.tarihSec.text.toString())
                val bitisTarih = format.parse(binding.tarihSec2.text.toString())

                // Tarihlerin doğru bir aralığa sahip olduğunu kontrol et
                if (bitisTarih.before(baslangicTarih)) {
                    Toast.makeText(requireContext(), "Bitiş Tarihi Başlangıç " +
                            "Tarihinden Önce Olamaz", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                //yeni bir görev eklemek veya mevcut bir
                // görevi güncellemek için kullanılır.
                if (hedefData == null) {
                    listener?.hedefiKaydet(todoTask, binding.hedefYazi)
                } else {
                    hedefData!!.task = todoTask
                    listener?.hedefiGuncelle(hedefData!!, binding.hedefYazi)
                }
            } else {
                // Hedef boş bırakılırsa uyarı verir
                Toast.makeText(requireContext(), "Lütfen Bir Hedef Girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takvimiGoster(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = selectedDate
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }


    // diğer sınıflarla iletişim kurmak
    interface OnDialogNextBtnClickListener {
        fun hedefiKaydet(todoTask: String, todoEdit: TextInputEditText)
        fun hedefiGuncelle(hedefData: HedefData, todoEdit: TextInputEditText)
    }
}
