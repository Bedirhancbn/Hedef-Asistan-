package com.example.hedefasistani.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.hedefasistani.R
import com.google.firebase.auth.FirebaseAuth


class BaslangicEkraniFragment : Fragment() {


    //nesneler
    private lateinit var mAuth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // xml dosyasına erişim
        return inflater.inflate(R.layout.fragment_baslangic_ekrani, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //fragmentin görünümünün düzgün bir şekilde oluşturulmasını sağlar
        super.onViewCreated(view, savedInstanceState)

        basla(view)

        val isLogin: Boolean = mAuth.currentUser != null

        // 2 saniyelik gecikme sonrasında çalışmaya başlar
        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({

            if (isLogin)
                navController.navigate(R.id.action_baslangicEkraniFragment_to_anaEkranFragment)
            else
                navController.navigate(R.id.action_baslangicEkraniFragment_to_girisYapFragment)

        }, 2000)
    }

    // nesnelerin başlatılması
    private fun basla(view: View) {
        mAuth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
    }
}
