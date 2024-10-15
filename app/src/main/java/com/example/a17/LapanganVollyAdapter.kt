package com.example.a17

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LapanganVollyAdapter(
    private val context: Context,
    private val lapanganList: List<LapanganVolly>
) : RecyclerView.Adapter<LapanganVollyAdapter.LapanganViewHolder>() {

    inner class LapanganViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageLapangan: ImageView = itemView.findViewById(R.id.imageLapangan)
        val namaLokasi: TextView = itemView.findViewById(R.id.namaLokasi)
        val btnTampilkanPeta: Button = itemView.findViewById(R.id.btnTampilkanPeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapanganViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_lapangan_volly, parent, false)
        return LapanganViewHolder(view)
    }

    override fun onBindViewHolder(holder: LapanganViewHolder, position: Int) {
        val lapangan = lapanganList[position]
        holder.imageLapangan.setImageResource(lapangan.gambar)
        holder.namaLokasi.text = lapangan.namaLokasi

        holder.btnTampilkanPeta.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:${lapangan.latitude},${lapangan.longitude}?q=${lapangan.latitude},${lapangan.longitude}(${lapangan.namaLokasi})")
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lapanganList.size
}
