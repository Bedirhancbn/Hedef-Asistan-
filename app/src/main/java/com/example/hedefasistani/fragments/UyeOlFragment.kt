package com.example.hedefasistani.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.appcompat.app.AlertDialog
import com.example.hedefasistani.R
import com.example.hedefasistani.databinding.FragmentUyeOlBinding
import com.google.firebase.auth.FirebaseAuth



class UyeOlFragment : Fragment() {

    //nesneler
    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentUyeOlBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // xml dosyasına erişim
        binding = FragmentUyeOlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //fragmentin görünümünün düzgün bir şekilde oluşturulmasını sağlar
        super.onViewCreated(view, savedInstanceState)


        basla(view)

        binding.yonlendirGirisYap.setOnClickListener {
            navController.navigate(R.id.action_uyeOlFragment_to_girisYapFragment)
        }
        binding.parolaDetay.setOnClickListener{
            parolaKriterBilgisi()
        }

        binding.ileriButon2.setOnClickListener {
            val email = binding.emailGiris.text.toString()
            val pass = binding.parolaGiris.text.toString()
            val verifyPass = binding.parolaGirisTekrar.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {

                    uyeOl(email, pass)

                } else {
                    Toast.makeText(context, "Şifreniz Uyuşmuyor", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(context, "Kullanıcı adı veya şifreler boş bırakılamaz", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uyeOl(email: String, pass: String) {
        if (parolaUygunMu(pass)) {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful)
                    navController.navigate(R.id.action_uyeOlFragment_to_anaEkranFragment)
                else
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Lütfen kriterlere uygun bir şifre seçin." +
                    " Bilgi ekranını kontrol edin. ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parolaUygunMu(parola: String): Boolean {
        val uzunlukKriteri = parola.length >= 6
        val rakamKriteri = parola.any { it.isDigit() }
        val sembolKriteri = parola.any { it.isLetterOrDigit().not() }
        val buyukHarfKriteri = parola.any { it.isUpperCase() }
        val kucukHarfKriteri = parola.any { it.isLowerCase() }

        return uzunlukKriteri && rakamKriteri && sembolKriteri && buyukHarfKriteri && kucukHarfKriteri
    }

    private fun parolaKriterBilgisi() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Güçlü Parola")
        builder.setIcon(R.drawable.parola)
            .setMessage(
                """
            Parolanız aşağıdaki kriterlere uymalıdır:
            
            - En az 6 karakter uzunluğunda olmalıdır.
            
            - Harf, rakam ve sembollerden oluşmalıdır:
                   +Rakamlar (0-9),
                   +semboller (!£$%^&*, vb), 
                   +harfler (a-z),(A-Z) .
                   
            - En az bir büyük bir küçük harf içermelidir.
            """.trimIndent()
            )
            .setPositiveButton("Tamam") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }

    // nesnelerin başlatılması
    private fun basla(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
    }
}
