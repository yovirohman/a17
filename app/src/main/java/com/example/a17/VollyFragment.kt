package com.example.a17


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VollyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var lapanganVollyAdapter: LapanganVollyAdapter
    private lateinit var lapanganList: List<LapanganVolly>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_volly, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewLapangan)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Siapkan data lapangan voli
        lapanganList = listOf(
            LapanganVolly(R.drawable.iconprofil, "Lapangan Voli A", -7.446620902997018, 112.70692890410777),
            LapanganVolly(R.drawable.icon_volley, "Lapangan Voli B", -7.446694202914505, 112.71697766113745),
            LapanganVolly(R.drawable.icon_volley, "Lapangan Voli B", -7.479250929215682, 112.59203653677116),
            // Tambahkan lebih banyak data sesuai kebutuhan
        )

        lapanganVollyAdapter = LapanganVollyAdapter(requireContext(), lapanganList)
        recyclerView.adapter = lapanganVollyAdapter

        return view // Kembalikan tampilan yang telah di-inflate
    }
}
