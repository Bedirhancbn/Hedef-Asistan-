package com.example.hedefasistani.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.hedefasistani.R
import com.example.hedefasistani.databinding.FragmentGirisYapBinding
import com.google.firebase.auth.FirebaseAuth

class GirisYapFragment : Fragment() {

    //nesneler
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentGirisYapBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // xml dosyasına erişim
        binding = FragmentGirisYapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //fragmentin görünümünün düzgün bir şekilde oluşturulmasını sağlar
        super.onViewCreated(view, savedInstanceState)

        basla(view)

        binding.yonlendirKayitOl.setOnClickListener {
            navController.navigate(R.id.action_girisYapFragment_to_uyeOlFragment)
        }

        binding.ileriButon2.setOnClickListener {
            val email = binding.emailGiris.text.toString()
            val pass = binding.parolaGiris.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty())

                girisYap(email, pass)
            else
                Toast.makeText(context, "Kullanıcı adı veya şifre boş bırakılamaz", Toast.LENGTH_SHORT).show()
        }
    }

    private fun girisYap(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful)
                navController.navigate(R.id.action_girisYapFragment_to_anaEkranFragment)
            else
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()

        }
    }

    // nesnelerin başlatılması
    private fun basla(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
    }

}
