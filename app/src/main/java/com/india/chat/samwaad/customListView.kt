package com.india.chat.samwaad

import android.content.Context
import android.widget.ArrayAdapter
import com.india.chat.samwaad.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class customListView(context: Context?, var titleArray: Array<String>, var imagearray1: IntArray, var imageArray: IntArray) : ArrayAdapter<Any?>(context!!, R.layout.custom_list_view, R.id.idTitle, titleArray) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.custom_list_view, parent, false)
        val myImage = row.findViewById<ImageView>(R.id.idPic)
        val myTitle = row.findViewById<TextView>(R.id.idTitle)
        row.findViewById<ImageView>(R.id.idforward)
        myImage.setImageResource(imageArray[position])
        myTitle.text = titleArray[position]

        return row
    }
}