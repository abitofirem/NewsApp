package com.example.navigationdrawerapp.ui.fragments
import com.example.navigationdrawerapp.R


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationdrawerapp.databinding.FragmentAboutBinding // Doğru Binding sınıfı

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    // Bu property, null olmayan bir değer döndürür ve _binding null olduğunda hata fırlatır.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View Binding'i kullanarak layout'u şişiriyoruz
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Uygulama versiyonu, geliştirici adı ve telif hakkı gibi bilgiler
        // doğrudan XML layout'unda veya strings.xml içinde tanımlanmıştır.
        // Bu fragment içinde dinamik olarak kodla atama yapılmasına gerek yoktur.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment'ın view'ı yok edildiğinde binding'i null'a set ederek bellek sızıntılarını önleriz.
        _binding = null
    }
}
