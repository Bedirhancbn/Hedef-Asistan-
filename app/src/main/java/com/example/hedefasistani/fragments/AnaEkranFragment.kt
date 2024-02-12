package com.example.hedefasistani.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AlertDialog
import com.example.hedefasistani.databinding.FragmentAnaEkranBinding
import com.example.hedefasistani.utils.Gorunum.HedefGorunum
import com.example.hedefasistani.utils.data.HedefData
import com.example.hedefasistani.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager


class AnaEkranFragment : Fragment(), HedefEklemeFragment.OnDialogNextBtnClickListener,
    HedefGorunum.TaskAdapterInterface, HedefDetayFragment.OnDialogNextBtnClickListener ,
    SensorEventListener{

    // nesneler
    private val TAG = "AnaEkranFragment"
    private lateinit var binding: FragmentAnaEkranBinding
    private lateinit var database: DatabaseReference
    private var frag: HedefEklemeFragment? = null
    private var frag2: HedefDetayFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String

    private lateinit var hedefGorunum: HedefGorunum
    private lateinit var HedefList: MutableList<HedefData>

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCount: Int = 0

    private val SHARED_PREF_NAME = "MySharedPrefs"
    private val KEY_STATIC_STEP_COUNT = "staticStepCount"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // xml dosyasına erişim
        binding = FragmentAnaEkranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //fragmentin görünümünün düzgün bir şekilde oluşturulmasını sağlar
        super.onViewCreated(view, savedInstanceState)

        basla()
        hedefleriAlFirebase()
        adimSayici()


        binding.hedefEkle.setOnClickListener {

            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()

            // ekrana hedef ekleme formunu oluşturur ve gösterir
            frag = HedefEklemeFragment()
            frag!!.setListener(this)

            frag!!.show(
                childFragmentManager,
                HedefEklemeFragment.TAG
            )

        }
        // giris ekranına geri dön
        binding.cikisButon.setOnClickListener {
            cikisOnay()
        }

        // tıklandığında görevleri sırala
        binding.imagesiralaAz.setOnClickListener {
            HedefList.sortBy { it.task }
            hedefGorunum.notifyDataSetChanged()
        }
        // tıklandığında görevleri sırala (Z'den A'ya)
        binding.imagesiralaZa.setOnClickListener {
            HedefList.sortByDescending { it.task }
            hedefGorunum.notifyDataSetChanged()
        }
        binding.ayakkabi.setOnClickListener {
            adimSayariGoster()
        }

    }

    private fun cikisOnay() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Çıkış Yap")
        builder.setIcon(R.drawable.cikis)
        builder.setMessage("Çıkış yapmak istediğinizden emin misiniz?")
        builder.setPositiveButton("Evet") { _, _ ->
            // Kullanıcı evet dediğinde çıkış işlemleri burada yapılır.
            val action = AnaEkranFragmentDirections.actionAnaEkranFragmentToGirisYapFragment()
            findNavController().navigate(action)
        }
        builder.setNegativeButton("Hayır") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()  // AlertDialog'ı oluştur
        alertDialog.show()  // AlertDialog'ı göster
    }


    private fun hedefleriAlFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                HedefList.clear()
                for (taskSnapshot in snapshot.children) {
                    val hedef =
                        taskSnapshot.key?.let { HedefData(it, taskSnapshot.value.toString()) }

                    if (hedef != null) {
                        HedefList.add(hedef)
                    }
                }
                Log.d(TAG, "onDataChange: " + HedefList)
                hedefGorunum.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    // nesnelerin başlatılması
    private fun basla() {

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser?.uid ?: "defaultUserId"
        database = Firebase.database.reference.child("Tasks")
            .child(authId)


        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)

        HedefList = mutableListOf()
        hedefGorunum = HedefGorunum(HedefList)
        hedefGorunum.setListener(this)
        binding.mainRecyclerView.adapter = hedefGorunum
    }

    override fun hedefiKaydet(todoTask: String, todoEdit: TextInputEditText) {

        database
            .push().setValue(todoTask)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Hedef Başarı İle Eklendi", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null

                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }

    override fun hedefiGuncelle(hedefData: HedefData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[hedefData.taskId] = hedefData.task
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Güncelleme İşlemi Başarılı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }

    override fun hedefSilme(hedefData: HedefData, position: Int) {
        database.child(hedefData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Silme İşlemi Başarılı", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun hedefDuzenleme(hedefData: HedefData, position: Int) {
        //daha önceden açılmış bir form varsa ve mevcutsa yeni
        // form başlatılınca mevcut form kapatılır
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        // hedef formunu güncel veriler ile oluşturup ekrana gösterir
        frag = HedefEklemeFragment.newInstance(hedefData.taskId, hedefData.task)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            HedefEklemeFragment.TAG
        )
    }

    override fun hedefDetay(hedefData: HedefData, position: Int) {
        //daha önceden açılmış bir form varsa ve mevcutsa yeni
        // form başlatılınca mevcut form kapatılır
        if (frag2 != null)
            childFragmentManager.beginTransaction().remove(frag2!!).commit()

        // hedef formunu güncel veriler ile oluşturup ekrana gösterir
        frag2 = HedefDetayFragment.newInstance(hedefData.taskId, hedefData.task)
        frag2!!.setListener(this)
        frag2!!.show(
            childFragmentManager,
            HedefEklemeFragment.TAG
        )
    }

    private fun adimSayici() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val sharedPreferences = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

        if (stepSensor == null) {
            Toast.makeText(context, "Step counter sensor not available", Toast.LENGTH_SHORT).show()
            // Her seferinde farklı bir random sayı oluştur ve kaydet
            val randomStepCount = (500..20000).random()
            stepCount = randomStepCount
            sharedPreferences.edit().putInt(KEY_STATIC_STEP_COUNT, randomStepCount).apply()

        } else {
            // Önceki kaydedilmiş statik adım sayısını al
            val savedStaticStepCount = sharedPreferences.getInt(KEY_STATIC_STEP_COUNT, -1)
            if (savedStaticStepCount == -1) {
                // Daha önce kaydedilmiş bir random sayı yoksa, mevcut random sayıyı kaydet
                sharedPreferences.edit().putInt(KEY_STATIC_STEP_COUNT, stepCount).apply()
            } else {
                // Daha önce kaydedilmiş bir random sayı varsa, onu kullan
                stepCount = savedStaticStepCount
            }
            // her adımda adım sayısı güncellenir
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }


    private fun adimSayariGoster() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Adım Sayısı")
        builder.setIcon(R.drawable.ayakkabi)
        builder.setMessage("Toplam Adım Sayısı: $stepCount")
        builder.setPositiveButton("Tamam") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    // veri yakalama
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            stepCount = event.values[0].toInt()
        }
    }

    // hassasiyet özellikleri
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Bu metotu boş bırakabilirsiniz, gerekli değilse.
    }
}
