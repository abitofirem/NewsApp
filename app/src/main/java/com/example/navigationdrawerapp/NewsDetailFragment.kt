package com.example.navigationdrawerapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.navigationdrawerapp.databinding.FragmentNewsDetailBinding


class NewsDetailFragment : Fragment() {

    private var _binding: FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Arguments'tan (Bundle'dan) gelen verileri al
        val haberBaslik = arguments?.getString("haber_baslik")
        val haberIcerik = arguments?.getString("haber_icerik")
        val haberGorselUrl = arguments?.getString("haber_gorsel_url")

        // Toolbar başlığını güncelle
        (activity as? MainActivity)?.setToolbarTitle(haberBaslik ?: getString(R.string.title_news)) // BURADA DEĞİŞİKLİK

        //Gelen verileri görünümlere bağla
        binding.detayBaslikTextView.text = haberBaslik
        binding.detayIcerikTextView.text = haberIcerik

        //Glide ile görseli yükle
        Glide.with(this)
            .load(haberGorselUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(binding.detayImageView)

        //Toolbar başlığını güncelle (isteğe bağlı)
        (activity as? MainActivity)?.setToolbarTitle(haberBaslik ?: getString(R.string.app_name))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        //Fragment kapatıldığında Toolbar başlığını varsayılana geri döndürmek isteyebilirsiniz.
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.title_news)) // BURADA DEĞİŞİKLİK, haber listesine dönerken başlık
    }
}