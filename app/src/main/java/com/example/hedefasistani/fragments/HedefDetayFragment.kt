package com.example.hedefasistani.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.hedefasistani.databinding.FragmentHedefDetayBinding
import com.example.hedefasistani.utils.data.HedefData
import com.google.android.material.textfield.TextInputEditText

class HedefDetayFragment : DialogFragment() {

    //nesneler
    private lateinit var binding: FragmentHedefDetayBinding
    private var listener: OnDialogNextBtnClickListener? = null
    private var hedefData: HedefData? = null

    // fragment içinde belirli bir butona tıklandığında veya
    // belirli bir işlem gerçekleştiğinde çağrılacak olan metodları içerir.
    fun setListener(listener: OnDialogNextBtnClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "HedefDetayFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            HedefDetayFragment().apply {
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
        binding = FragmentHedefDetayBinding.inflate(inflater, container, false)
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

        // Kullanıcının metni değiştirmesini engellemek için
        binding.hedefYazi.isFocusable = false
        binding.hedefYazi.isClickable = false

        binding.kapat.setOnClickListener {
            dismiss()
        }

    }
    // diğer sınıflarla iletişim kurmak
    interface OnDialogNextBtnClickListener {
        fun hedefiKaydet(todoTask: String, todoEdit: TextInputEditText)
        fun hedefiGuncelle(hedefData: HedefData, todoEdit: TextInputEditText)
    }
}
